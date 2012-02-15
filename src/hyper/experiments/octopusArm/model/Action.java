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


/**
 * Action objects are used by low-level behaviors to pass their recommended actions up the behavior framework.
 * Multiple controllers can interact by passing their action recommendations to a higher level of the framework.
 * Arbitration techniques are then applied to create higher order behaviors by managing execution or subsumption
 * of recommended action objects.
 * <p/>
 * For more information, see Unified behavior framework for reactive robot control, Journal of Intelligent & Robotic
 * Systems, 55(2�3), by Brian G. Woolley and Gilbert L. Peterson at <href>http://www.springerlink.com/content/8468r442420x47t6/</href>
 *
 * @author Brian Woolley
 */
public class Action {

    /**
     * Constructs a new object that begin with all muscle in a relaxed state (i.e. contractive force = 0.0).
     */
    public Action() {
        dorsalContractions = new double[OctopusArm.getSegmentCount() + 1];
        transverseContractions = new double[OctopusArm.getSegmentCount() + 1];
        ventralContractions = new double[OctopusArm.getSegmentCount() + 1];
    }

    /**
     * Relaxes all muscles in the arm, i.e. the contractive force for all muscles is set to zero.
     */
    public void relax() {
        for (int i = 0; i <= OctopusArm.getSegmentCount(); i++) {
            dorsalContractions[i] = 0.0;
            transverseContractions[i] = 0.0;
            ventralContractions[i] = 0.0;
        }
    }

    /**
     * Sets the contractive force for a specific dorsal muscle.
     *
     * @param segment The dorsal muscle at this segment (1 to n, where n is {@link OctopusArm#getSegmentCount()}).
     * @param force   The contractive force for the specified muscle.
     */
    public void setDorsalMuscleContraction(int segment, double force) {
        dorsalContractions[segment] = force;
    }

    /**
     * Sets the contractive force for a specific transverse muscle.
     *
     * @param segment The transverse muscle at this segment (1 to n, where n is {@link OctopusArm#getSegmentCount()}).
     * @param force   The contractive force for the specified muscle.
     */
    public void setTransverseMuscleContraction(int segment, double force) {
        transverseContractions[segment] = force;
    }

    /**
     * Sets the contractive force for a specific ventral muscle.
     *
     * @param segment The ventral muscle at this segment (1 to n, where n is {@link OctopusArm#getSegmentCount()}).
     * @param force   The contractive force for the specified muscle.
     */
    public void setVentralMuscleContraction(int segment, double force) {
        ventralContractions[segment] = force;
    }

    /**
     * Apply the current contractive forces to an {@link ArmController}.
     *
     * @param arm The arm controller
     */
    public void execute(ArmController arm) {
        for (int i = 1; i <= OctopusArm.getSegmentCount(); i++) {
            arm.contractDorsalMuscle(i, dorsalContractions[i]);
            arm.contractTransverseMuscle(i, transverseContractions[i]);
            arm.contractVentralMuscle(i, ventralContractions[i]);
            // arm.applyRotationalForceAtBase(baseRotationForce);
//			System.out.print("(" +dorsalContractions[i]+ ") ");
        }
    }

    private final double[] dorsalContractions;
    private final double[] transverseContractions;
    private final double[] ventralContractions;
}
