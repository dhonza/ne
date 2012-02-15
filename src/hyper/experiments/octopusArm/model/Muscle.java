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
 * created by Brian Woolley on 10 October, 2009
 */
package hyper.experiments.octopusArm.model;

import hyper.experiments.octopusArm.OctopusArm;
import net.phys2d.raw.Body;
import net.phys2d.raw.SpringJoint;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements a muscle structure based on a linear spring model.  Contractions
 * are effected by varying the value of the compressed/stretched spring constants,
 * (Engel 2005).  Users can specify the muscle's contraction strengths by specifying
 * a percentage (0..1) of the muscle's contraction range.  The muscle's response to
 * contraction commands are applied in 1% increments to produce a smooth draw.
 * <p/>
 * This muscle model enhances a passive linear spring model (phys2d.raw.SpringJoint)
 * to provide a muscle structure for the Octopus Arm simulation domain.
 */
public class Muscle implements Simulatable {

    /**
     * Muscle constructor that connects {@link Body} 1 to {@link Body} 2 via linear {@link SpringJoint}.
     * The resistance component is used to set the tension of the muscle when relaxed.
     * The {@link Muscle}'s max strength is six times the relaxed value.
     *
     * @param springJoint is the musculature connecting {@link Body} 1 to {@link Body} 2
     * @param resistance  sets the operating range of the {@link Muscle} contractive force
     */
    public Muscle(SpringJoint springJoint, double resistance) {
        assert (springJoint != null);
        assert (resistance > 0);

        muscle = springJoint;
        currentForce = resistance;
        desiredForce = resistance;
        contractionMin = resistance;
        contractionRange = resistance * 5;
        contractionRate = contractionRange / OctopusArm.getMuscleResponse();

        currentLength = muscle.getSpringSize();
        desiredLength = currentLength / 1.5;
        lengthRate = currentLength / 200;
    }

    /**
     * Increases the contractive force of the muscle in increments until the
     * actual contractive force matches the specified force.  This method should
     * be called in conjunction with the stepped physic model.
     */
    public void step() {
        double diff;

        diff = desiredForce - currentForce;
        if (Math.abs(diff) > contractionRate) {
            if (currentForce > desiredForce)
                currentForce -= contractionRate;
            if (currentForce < desiredForce)
                currentForce += contractionRate;
        } else currentForce += diff;

        diff = desiredLength - currentLength;
        if (Math.abs(diff) > lengthRate) {
            if (currentLength > desiredLength)
                currentLength -= lengthRate;
            if (currentLength < desiredLength)
                currentLength += lengthRate;
        } else currentLength += diff;

        muscle.setStretchedSpringConst((float) currentForce);
        muscle.setCompressedSpringConst((float) currentForce);
        muscle.setSpringSize((float) currentLength);
    }

    /**
     * Returns the contraction force being applied at this muscle segment for this time-step.
     *
     * @return The force begin applied.
     */
    public double getEnergyUsed() {
        return (currentForce - contractionMin) / contractionRange;
    }

    /**
     * Pre-calculate everything and apply initial impulse before the simulation step takes place.
     *
     * @param invDT The amount of time the simulation is being stepped by.
     */
    protected void preStep(int invDT) {
        muscle.preStep(invDT);
    }

    /**
     * Sets the muscle contraction force to a percentage of the contraction range
     * The muscles actual contractive force will increase/decrease towards the value
     * specified in increments to produce a smooth frequency response.
     *
     * @param activation Accepts values between 0.0 and 1.0.
     */
    protected void setContraction(double activation) {
        if (activation > max) activation = max;
        if (activation < min) activation = min;
        desiredForce = activation * contractionRange + contractionMin;
    }

    /**
     * Returns the current length of the muscle connecting {@link Body} 1 and {@link Body} 2.
     *
     * @return The current length of the muscle.
     */
    protected double getLength() {
        return muscle.getSpringSize();
    }

    /**
     * Returns {@link Body} 1 in the {@link Muscle}.
     *
     * @return {@link Body} 1.
     */
    protected Body getBody1() {
        return muscle.getBody1();
    }

    /**
     * Returns {@link Body} 2 in the {@link Muscle}.
     *
     * @return {@link Body} 2.
     */
    protected Body getBody2() {
        return muscle.getBody2();
    }

    /**
     * Returns the current contractive force of the muscle.  Note that this value may differ from the desired force set by the user.
     *
     * @return The current contractive force of the muscle.
     */
    protected double getCurrentForce() {
        return currentForce;
    }

    /**
     * Returns the current contractive force specified by the user.  Notes that this value may differ from the current contractive
     * force of the muscle.
     *
     * @return The desired contractive force of the muscle
     */
    protected double getDesiredForce() {
        return desiredForce;
    }

    /**
     * Returns the minimum (relaxed) contractive force of the muscle.  Set at construction by the resistance parameter.
     *
     * @return The minimum contractive force of the muscle.
     */
    protected double getContractionMin() {
        return contractionMin;
    }

    /**
     * Returns the size of the contraction range of the muscle. Typically 6x the minimum (relaxed) contraction force.
     *
     * @return The size of the contraction range of the muscle.
     */
    protected double getContractionRange() {
        return contractionRange;
    }

    /**
     * Returns the contraction rate of the muscle per timestep.
     *
     * @return The contraction rate of the muscle per timestep.
     */
    protected double getContractionRate() {
        return contractionRate;
    }

    /**
     * @see hyper.experiments.octopusArm.model.Simulatable#getAllBodies()
     */
    public Collection<Body> getAllBodies() {
        return new ArrayList<Body>();
    }

    private final SpringJoint muscle;

    private double currentForce, desiredForce;
    private final double contractionMin;
    private final double contractionRange;
    private final double contractionRate;

    private double currentLength, desiredLength;
    private final double lengthRate;

    private final double max = 1.0;
    private final double min = 0.0;
}
