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

/**
 * This interface provides a simple interface for controlling the arm octopus arm.
 *
 * @author Brian Woolley
 */
public interface ArmController {

    /**
     * Applies a rotational force to the arm base.
     *
     * @param force The rotational force to apply at the arm base.  This is not currently supported.
     * @see ArmBase#setRotationalForce(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void applyRotationForce(double force);

    /**
     * Applies a contraction force to the dorsal muscle of segment(i).
     *
     * @param i     The index of the segment being activated
     * @param force The contraction force to be applied
     * @see ArmSegment#setDorsalContraction(double)
     * @see Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractDorsalMuscle(int i, double force);

    /**
     * Applies a contraction force to the transverse muscle of segment(i).
     *
     * @param i     The index of the segment being activated.
     * @param force The contraction force to be applied.
     * @see ArmSegment#setTransverseContraction(double)
     * @see Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractTransverseMuscle(int i, double force);

    /**
     * Applies a contraction force to the ventral muscle of segment(i).
     *
     * @param i     The index of the segment being activated.
     * @param force The contraction force to be applied.
     * @see ArmSegment#setVentralContraction(double)
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractVentralMuscle(int i, double force);

}
