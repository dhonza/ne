/**
 * Copyright (C) 2011 Brian Woolley
 *
 * This file is part of the octopusArm simulator.
 *
 * The octopusArm simulator is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *
 * created by Brian Woolley on 10 October 2009
 */
package hyper.experiments.octopusArm.model;

import hyper.experiments.octopusArm.OctopusArm;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;

/**
 * Establishes an environment, including physics effects, for the simulation
 * of an Octopus Arm.  The environment acts as an interface between the physics
 * engine, the agent and the arm components.  No other class has knowledge of the
 * phys2d.raw.World object.
 * <p/>
 * This environment provides the primary linkage between the Agent, the phys2D
 * physics engine and the Octopus Arm model.  This class works with the Factory
 * class to provide services that ensures the consistency of the simulation by
 * never sharing the instantiated phys2d.raw.World object.
 * <p/>
 * *** Future work will implement Environment as a runnable object to allow multiple
 * simulation threads in isolated universes. ***
 */
public class Environment implements CollisionListener {

    /**
     * Creates a new environment for testing an n segment arm.
     *
     * @param n       The number of segments in the Arm, including the base segment
     * @param results A place to record the run metrics
     */
    public Environment(int n, RunMetrics results) {
        this(n, results, false);
    }

    /**
     * Creates a new environment for testing an n segment arm.
     *
     * @param n       The number of segments in the Arm, including the base segment
     * @param results A place to record the run metrics
     * @param debug   The flag bypasses the arm stabilization step for JUnit testing and debugging purposes.
     */
    public Environment(int n, RunMetrics results, boolean debug) {
        assert (n > 0);
        armSize = n;
        assert (results != null);
        f_results = results;

        f_world = new World(new Vector2f(0.0f, 0.0f), 10);
        f_world.addListener(this);

        f_factory = new Factory(f_world);
        arm = f_factory.createNewArm(armSize);
        state = new ArmState(arm, new Target(0, 0, 0));

        setGravityEffects(-OctopusArm.getGravity());

        if (!debug) init();
    }

    /**
     * Steps the arm and the world for 200 timesteps to reach a stable state.  This method can be
     * bypassed by setting the debug flag in the {@link #Environment(int, RunMetrics, boolean)} constructor.
     */
    private void init() {
        for (int i = 0; i < 200; i++) {
            try {
                arm.step();
            } catch (InvalidArmConfigurationException e) {
                handleArmBreakException(e);
                break;
            }
            f_world.step();
        }
    }

    /**
     * Detects and handles CollisionEvents that occur within the physics environment.  When a point on the arm
     * (or arm tip if specified by {@link OctopusArm#requireTipTouch()}) collides with the targets, the hit is
     * recorded by calling {@link RunMetrics#hitTargetEvent()} and the {@link #setRunComplete()} is called.
     *
     * @param ce The {@link CollisionEvent} thrown by the {@link World}.
     * @see CollisionListener
     */
    public void collisionOccured(CollisionEvent ce) {
        Body a = ce.getBodyA();
        Body b = ce.getBodyB();

//		System.out.println("Collision between: "+Util.getLabel(a)+" and " + Util.getLabel(b));
        if (a.equals(state.getTarget().getBody()) || b.equals(state.getTarget().getBody())) {

            if (OctopusArm.requireTipTouch()) {
                Segment tip = arm.getArmTip();
                if (a.equals(tip.getDorsalNode()) || a.equals(tip.getVentralNode()) ||
                        b.equals(tip.getDorsalNode()) || b.equals(tip.getVentralNode())) {

                    f_results.hitTargetEvent();
                    setRunComplete();
                } else {
                    // Do Nothing
                }
            } else {
                f_results.hitTargetEvent();
                setRunComplete();
            }
        } else {
            // Do Nothing
        }
    }

    /**
     * Advances the simulation environment one timestep.  This includes stepping the arm and the physics environment, recording
     * the current state in the {@link RunMetrics}, handling {@link InvalidArmConfigurationException}s, and checks for the end of run.
     */
    public void step() {
        try {
            arm.step();
        } catch (InvalidArmConfigurationException e) {
            handleArmBreakException(e);
        }

        f_world.step();
        f_results.step(state);

        if (f_results.getTimeElapsed() == MAX_RUN_TIME - 1)
            setRunComplete();
    }

    /**
     * Handles the {@link InvalidArmConfigurationException} based on the {@link OctopusArm#resetOnArmBreak()} setting.
     *
     * @param e The {@link InvalidArmConfigurationException}.
     */
    private void handleArmBreakException(InvalidArmConfigurationException e) {
        if (OctopusArm.resetOnArmBreak()) {
            resetArm();
        } else {
            setRunComplete();
        }
    }

    /**
     * Sets up a new trial by: moving the target, marking a new trial in the {@link RunMetrics}, and resetting the arm
     * (if specified by {@link OctopusArm#resetBetweenTrials()}).
     *
     * @param aNewTarget The target for the next Trial.
     */
    public void nextTrial(Target aNewTarget) {
        f_world.remove(state.getTarget().getBody());
        f_world.add(aNewTarget.getBody());
        state.setTarget(aNewTarget);
        f_results.newTrial();

        if (OctopusArm.resetBetweenTrials()) {
            resetArm();
        }

        setRunning();
    }

    /**
     * Resets the arm to the initial position.  This is useful when an {@link InvalidArmConfigurationException} is detected or
     * when setting up a new trial.
     *
     * @return The new arm.
     */
    public Arm resetArm() {
        removeFromWorld(arm);
        arm = f_factory.createNewArm(armSize);
        init();
        state.setArm(arm);
        return getArm();
    }

    /**
     * Remove bodies from the physics environment when no longer needed.
     *
     * @param object The simulatable object to be removed.
     */
    private void removeFromWorld(Simulatable object) {
        for (Body body : object.getAllBodies()) {
            f_world.remove(body);
        }
    }

    /**
     * Set the gravity applied in the world.  To simulate typical Earth gravity, a downward
     * force of 9.8 meters per second squared, use the value -9.8
     *
     * @param gravity The gravity force applied to all objects
     */
    public void setGravityEffects(double gravity) {
        f_world.setGravity(0.0f, (float) gravity);
    }

    /**
     * Returns the current ArmState object--provides information about the arm
     *
     * @return The current ArmState object
     */
    public ArmState getArmState() {
        return state;
    }

    /**
     * Returns the current {@link ArmController}. An interface that provides the ability to manipulate the arm.
     *
     * @return The current {@link ArmController}.
     */
    public ArmController getArmController() {
        return arm;
    }

    /**
     * Returns the current target.
     *
     * @return The current target.
     */
    protected Target getTarget() {
        return state.getTarget();
    }

    /**
     * Returns the {@link RunMetrics} object created to record the evaluation.
     *
     * @return The metrics for this run.
     */
    public RunMetrics getRunMetrics() {
        return f_results;
    }

    /**
     * Signals the current running status of the Environment.
     *
     * @return The current running status of the Environment.
     */
    public boolean isRunning() {
        return isRunning;
    }

    private void setRunning() {
        isRunning = true;
    }

    /**
     * Sets the isRunning status of the Environment to false.
     */
    private void setRunComplete() {
        isRunning = false;
    }

    /**
     * Provides access to the arm object for JUnit testing
     */
    protected Arm getArm() {
        return arm;
    }

    private Arm arm;
    private ArmState state;
    private boolean isRunning = false;

    private final World f_world;
    private final Factory f_factory;
    private final RunMetrics f_results;
    private final int armSize;

    public static final int MAX_RUN_TIME = OctopusArm.getMaxTimesteps();
    public static final float MASS = 0.125f;
    public static final float RADIUS = 0.000001f;
}