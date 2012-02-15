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

import net.phys2d.raw.Body;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The arm is an implementation of the simulated hydrostat (octopus arm) introduced by Engel and Yekutieli.  The arm is
 * constructed as a series of quadrilateral polygons with fixed area.  Muscle contractions are simulated by increasing
 * and decreasing the k-value of a linear spring model that connects a series of point masses.
 *
 * @author Brian Woolley
 * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
 * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
 */
public class Arm implements Simulatable, ArmController {

    /**
     * Add a base segment to the arm.  Because the arm base is always the first segment added to the arm, this method clears the list of segments.
     *
     * @param base The base of the arm.
     */
    public void add(ArmBase base) {
        segmentList.clear();
        segmentList.add(base);
    }

    /**
     * Add a segment to the arm.  Arm segments have a definite order and must be added sequentially
     * where segment 0 is the arm base, segment 1 is arm segment attached to the arm base, segment 2
     * is attached to segment 1, and so forth.
     *
     * @param segment The arm segment to append.
     */
    public void add(ArmSegment segment) {
        if (segmentList.isEmpty())
            throw new AssertionError("The first segment added to the arm must be an base segment.");
        if (!segment.getPreviousSegment().equals(getSegment(size() - 1)))
            throw new AssertionError("Arm segments must be added in order.  segment.getPreviousSegment() does not match the last segment added.");

        segmentList.add(segment);
    }

    /**
     * Add a segment to the arm.  Complies with all restrictions governing the addition of the arm base and additional arm segments.
     *
     * @param segment The segment to add to the arm.
     * @see #add(ArmBase)
     * @see #add(ArmSegment)
     */
    public void add(Segment segment) {
        if (segment instanceof ArmBase) add((ArmBase) segment);
        if (segment instanceof ArmSegment) add((ArmSegment) segment);
    }

    /**
     * Returns the number of segments in the arm (including the arm base).
     *
     * @return The number segments in the arm.
     */
    public int size() {
        return segmentList.size();
    }

    /**
     * Returns the list of segments in the arm (including the arm base).
     *
     * @return The ordered list of segments in the arm.
     */
    public List<Segment> getSegmentList() {
        return segmentList;
    }

    /**
     * Returns the distal end of the arm (i.e. the arm tip).
     *
     * @return The last segment in the arm.
     */
    public ArmSegment getArmTip() {
        return (ArmSegment) segmentList.get(size() - 1);
    }

    /**
     * Returns the segment (either an ArmBase or an ArmSegment) at the position indicated.
     *
     * @param i The index of the segment requested.
     * @return The arm segment at the specified position.
     */
    public Segment getSegment(int i) {
        return segmentList.get(i);
    }

    /**
     * Returns the arm's base segment.
     *
     * @return The arm's base segment.
     */
    public ArmBase getArmBase() {
        return (ArmBase) getSegment(0);
    }

    /**
     * Returns the arm segment at the position indicated (position 0 is invalid since it is the ArmBase).
     *
     * @param i The index of the arm segment.
     * @return The arm segment at position i.
     */
    public ArmSegment getArmSegment(int i) {
        if (i == 0) throw new ArrayIndexOutOfBoundsException("Segment 0 is the ArmBase.");
        return (ArmSegment) getSegment(i);
    }

    /**
     * Returns the energy used by the arm during this timestep.  The energy used is the sum of the current contractive
     * forces at each muscle in the arm.
     *
     * @return The energy expended by the arm in this timestep.
     * @see hyper.experiments.octopusArm.model.Simulatable#getEnergyUsed()
     */
    public double getEnergyUsed() {
        double sum = 0.0;
        for (Segment s : segmentList) {
            sum += s.getEnergyUsed();
        }
        return sum;
    }

    /**
     * Advances the arm one simulation timestep.  To be successful, each arm segment must pass a convex check, otherwise an
     * {@link InvalidArmConfigurationException} is thrown.
     *
     * @throws InvalidArmConfigurationException
     *
     * @see hyper.experiments.octopusArm.model.Simulatable#step()
     */
    public void step() throws InvalidArmConfigurationException {
        for (Segment s : segmentList) s.step();
    }

    /**
     * Sets the rotational force in the base segment.  The function (currently unimplemented) allows the arm to rotate
     * around the base.
     *
     * @param force The rotational force at the arm base.
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     * @see hyper.experiments.octopusArm.model.ArmBase#setRotationalForce(double)
     */
    public void applyRotationForce(double force) {
        getArmBase().setRotationalForce(force);
    }

    /**
     * Applies a contraction force to the dorsal muscle of segment(i).  Values of force typically range from 0.0 to 1.0.
     *
     * @param i     is this index of the segment being activated
     * @param force is the contraction force to be applied
     * @see ArmSegment#setDorsalContraction(double)
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractDorsalMuscle(int i, double force) {
        getArmSegment(i).setDorsalContraction(force);
    }

    /**
     * Applies a contraction force to the transverse muscle of segment(i).  Values of force typically range from 0.0 to 1.0.
     *
     * @param i     is this index of the segment being activated
     * @param force is the contraction force to be applied
     * @see ArmSegment#setTransverseContraction(double)
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractTransverseMuscle(int i, double force) {
        getArmSegment(i).setTransverseContraction(force);
    }

    /**
     * Applies a contraction force to the ventral muscle of segment(i).  Values of force typically range from 0.0 to 1.0.
     *
     * @param i     is this index of the segment being activated
     * @param force is the contraction force to be applied
     * @see ArmSegment#setVentralContraction(double)
     * @see hyper.experiments.octopusArm.model.Muscle#setContraction(double)
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.83.8883&rep=rep1&type=pdf>Learning to control an octopus arm with gaussian process temporal difference methods</a>
     * @see <a href=http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.137.7975&rep=rep1&type=pdf>Dynamic model of the octopus arm: Biomechanics of the octopus reaching movement.</a>
     */
    public void contractVentralMuscle(int i, double force) {
        getArmSegment(i).setVentralContraction(force);
    }

    /**
     * Returns a collection of all the {@link Body} objects in the arm.
     *
     * @return The collection of {@link Body} objects in the arm.
     * @see Simulatable#getAllBodies()
     */
    public Collection<Body> getAllBodies() {
        Collection<Body> bodies = new ArrayList<Body>();
        for (Segment s : segmentList) {
            bodies.addAll(s.getAllBodies());
        }
        return bodies;
    }


    private final List<Segment> segmentList = new ArrayList<Segment>();
}
