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

import net.phys2d.math.ROVector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * The simulator
 *
 * @author Brian Woolley
 *         Created 9/21/2009
 */
public class ArmState {

    public ArmState(Arm aArm, Target aNewTarget) {
        assert (aArm != null);
        arm = aArm;
        assert (aNewTarget != null);
        target = aNewTarget;
    }

    /**
     * Returns the current target.
     *
     * @return The current target.
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets the current target.
     *
     * @param aNewTarget The new Target.
     */
    protected void setTarget(Target aNewTarget) {
        if (aNewTarget == null) throw new NullPointerException("The new target cannot be null.");
        target = aNewTarget;
    }

    /**
     * Returns the current position of the target, no information about size or shape is provided.
     *
     * @return the current position of the target.
     */
    public ROVector2f getTargetPosition() {
        return target.getPosition();
    }

    /**
     * Returns the distance from the arm tip (at alpha) to the target surface.
     *
     * @return The distance from the arm tip to the target surface.
     */
    public double getDistanceToTarget() {
        return arm.getArmTip().getAlpha().distance(target.getPosition()) - target.getRadius();
    }

    /**
     * Returns the total energy used by the arm in this timestep.  The value is the sum
     * of the contractive setting of each muscle in the arm at this timestep.
     *
     * @return the total energy used in this timestep
     */
    public double getEnergyUsed() {
        return arm.getEnergyUsed();
    }

    /**
     * Returns the number of segments in the arm (including the arm base).
     *
     * @return The number of segments in the arm (including the arm base).
     */
    public int getSegmentCount() {
        return arm.size();
    }

    /**
     * Returns the position for each dorsal node of the arm.  Nodes are ordered in
     * the list from 0 (the arm's base segment) to n-1 (the tip of the arm).
     *
     * @return The position for each dorsal node.
     */
    public List<ROVector2f> getDorsalPositions() {
        List<ROVector2f> dorsalPositionList = new ArrayList<ROVector2f>();

        for (Segment s : arm.getSegmentList()) {
            dorsalPositionList.add(s.getDorsalNode().getPosition());
        }
        return dorsalPositionList;
    }

    /**
     * Returns the position for each ventral node of the arm.  Nodes are ordered in
     * the list from 0 (the base segment) to n-1 (the tip of the arm).
     *
     * @return The position for each ventral node.
     */
    public List<ROVector2f> getVentralPositions() {
        List<ROVector2f> ventralPositionList = new ArrayList<ROVector2f>();

        for (Segment s : arm.getSegmentList()) {
            ventralPositionList.add(s.getVentralNode().getPosition());
        }
        return ventralPositionList;
    }

    /**
     * Returns the position for each alpha node (the midpoint along the transverse muscle) of the arm.
     * Nodes are ordered in the list from 0 (the base segment) to n-1 (the tip of the arm).
     *
     * @return The position for each alpha node (the arm's centerline).
     */
    public List<ROVector2f> getAlphaPositions() {
        List<ROVector2f> alphaPositionList = new ArrayList<ROVector2f>();

        for (Segment s : arm.getSegmentList()) {
            alphaPositionList.add(s.getAlpha());
        }
        return alphaPositionList;
    }

    /**
     * Returns the alpha angles for each alpha node (the midpoint along the transverse muscle) of the arm.
     *
     * @return The alpha angle (between -pi and +pi) for each segment along the centerline.
     */
    public List<Double> getAlphaAngles() {
        List<Double> alphaAngleList = new ArrayList<Double>();

        for (Segment s : arm.getSegmentList()) {
            alphaAngleList.add(s.getAlphaAngle());
        }
        return alphaAngleList;
    }

    /**
     * Returns the velocity for each dorsal node of the arm.  Nodes are ordered in
     * the list from 0 (the base segment) to n-1 (the tip of the arm).
     *
     * @return The velocity for each dorsal node.
     */
    public List<ROVector2f> getDorsalVelocities() {
        List<ROVector2f> dorsalVelocityList = new ArrayList<ROVector2f>();

        for (Segment s : arm.getSegmentList()) {
            dorsalVelocityList.add(s.getDorsalNode().getVelocity());
        }
        return dorsalVelocityList;
    }

    /**
     * Returns the velocity for each ventral node of the arm.  Nodes are ordered in
     * the list from 0 (the base segment) to n-1 (the tip of the arm).
     *
     * @return The velocity for each ventral node
     */
    public List<ROVector2f> getVentralVelocities() {
        List<ROVector2f> ventralVelocityList = new ArrayList<ROVector2f>();

        for (Segment s : arm.getSegmentList()) {
            ventralVelocityList.add(s.getVentralNode().getVelocity());
        }
        return ventralVelocityList;
    }

    /**
     * Returns the current strength of the dorsal contractions at each arm segment.  Nodes are
     * ordered in the list from 0 (the base segment) to n-1 (the tip of the arm).  The dorsal
     * contraction strength of the base segment is a place holder and is always zero.
     *
     * @return The current dorsal contraction strengths.
     */
    public List<Double> getDorsalContractions() {
        List<Double> dorsalContractions = new ArrayList<Double>();

        for (Segment s : arm.getSegmentList()) {
            if (s instanceof ArmSegment)
                dorsalContractions.add(((ArmSegment) s).getDorsalContraction());
            else dorsalContractions.add(0.0);
        }
        return dorsalContractions;
    }

    /**
     * Returns the current strength of the transverse contractions at each arm segment.  Nodes are
     * ordered in the list from 0 (the base segment) to n-1 (the tip of the arm).  The transverse
     * contraction strength of the base segment is a place holder and is always zero.
     *
     * @return The current transverse contraction strengths.
     */
    public List<Double> getTransverseContractions() {
        List<Double> transverseContractions = new ArrayList<Double>();

        for (Segment s : arm.getSegmentList()) {
            if (s instanceof ArmSegment)
                transverseContractions.add(((ArmSegment) s).getTransverseContraction());
            else transverseContractions.add(0.0);
        }
        return transverseContractions;
    }

    /**
     * Returns the current strength of the ventral contractions at each arm segment.  Nodes are
     * ordered in the list from 0 (the base segment) to n-1 (the tip of the arm).  The ventral
     * contraction strength of the base segment is a place holder and is always zero.
     *
     * @return The current ventral contraction strengths.
     */
    public List<Double> getVentralContractions() {
        List<Double> ventralContractions = new ArrayList<Double>();

        for (Segment s : arm.getSegmentList()) {
            if (s instanceof ArmSegment)
                ventralContractions.add(((ArmSegment) s).getVentralContraction());
            else ventralContractions.add(0.0);
        }
        return ventralContractions;
    }

    /**
     * Returns the current strength of the dorsal muscle contraction at the segment specified.
     *
     * @param i The specified segment.
     * @return The current strength of the dorsal muscle contraction at segment specified.
     */
    public double getDorsalMuscleContraction(int i) {
        return arm.getArmSegment(i).getDorsalContraction();
    }

    /**
     * Returns the current strength of the transverse muscle contraction at the segment specified.
     *
     * @param i The specified segment.
     * @return The current strength of the transverse muscle contraction at segment specified.
     */
    public double getTransverseMuscleContraction(int i) {
        return arm.getArmSegment(i).getTransverseContraction();
    }

    /**
     * Returns the current strength of the ventral muscle contraction at the segment specified.
     *
     * @param i The specified segment.
     * @return The current strength of the ventral muscle contraction at segment specified.
     */
    public double getVentralMuscleContraction(int i) {
        return arm.getArmSegment(i).getVentralContraction();
    }

    /**
     * Changes the current arm that the state values are based on.
     *
     * @param aNewArm The new arm that state values will be based on.
     */
    protected void setArm(Arm aNewArm) {
        assert (aNewArm != null);
        arm = aNewArm;
    }

    /**
     * Provides access to the arm object for JUnit testing
     */
    protected Arm getArm() {
        return arm;
    }


    private Arm arm;
    private Target target;
}
