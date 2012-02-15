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
 * created by Brian Woolley on May 6, 2010
 */
package hyper.experiments.octopusArm.model;

import hyper.experiments.octopusArm.OctopusArm;
import hyper.experiments.octopusArm.fitness.FitnessStrategy;
import hyper.experiments.octopusArm.userInterface.Renderable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages the simulation environment, the evaluation target(s), the agent being evaluated,
 * the run results, and the simulation viewer (if applicable).
 *
 * @author Brian Woolley
 */
public class Simulation {

    /**
     * Overloaded constructor creates a run with a single evaluation target.
     *
     * @param agent  The {@link Agent} to be evaluated.
     * @param target The evaluation {@link Target}.
     * @see #Simulation(Agent)
     */
    public Simulation(Agent agent, Target target) {
        this(agent);
        assert (target != null);
        f_targets.add(target);
    }

    /**
     * Overloaded constructor creates a run with multiple evaluation targets.
     *
     * @param agent   The {@link Agent} to be evaluated.
     * @param targets The collection of evaluation targets (one per {@link Trial}).
     * @see #Simulation(Agent)
     */
    public Simulation(Agent agent, Collection<Target> targets) {
        this(agent);
        assert (targets != null);
        f_targets.addAll(targets);
    }

    /**
     * Constructs the simulator by creates a blank set of {@link RunMetrics}, a new {@link Environment}
     * and a list of evaluation targets (initially empty).
     *
     * @param agent The {@link Agent} to be evaluated.
     */
    public Simulation(Agent agent) {
        assert (agent != null);
        f_agent = agent;
        f_results = new RunMetrics();
        f_env = new Environment(OctopusArm.getSegmentCount(), f_results);
        f_targets = new ArrayList<Target>();
    }

    /**
     * Runs the simulation by evaluating how the agent responds to each of the evaluation targets.
     */
    public void run() {
        for (Target t : f_targets) {
            f_env.nextTrial(t);

            while (f_env.isRunning()) {
                for (int i = 0; i < 10; i++) step();
                if (display != null) {
                    display.renderArmPostion(f_env);
                }
            }
        }
        f_agent.addFitnessValue(f_evaluator.calculateFitness(f_results));
    }

    public void step() {
        if (f_env.isRunning()) {
            Action action = f_agent.genAction(f_env.getArmState());
            action.execute(f_env.getArmController());
            f_env.step();
        }
    }

    public Environment getEnvironment() {
        return f_env;
    }

    public FitnessStrategy getFitnessFunction() {
        return f_evaluator;
    }

    public void enableDisplay(Renderable simView) {
        assert (simView != null);
        display = simView;
    }

    public RunMetrics getRunMetrics() {
        return f_results;
    }

    public Agent getAgent() {
        return f_agent;
    }

    private final Agent f_agent;
    private final List<Target> f_targets;
    private final RunMetrics f_results;
    private final Environment f_env;
    private final FitnessStrategy f_evaluator = OctopusArm.getFitnessStrategy();
    private Renderable display = null;
}
