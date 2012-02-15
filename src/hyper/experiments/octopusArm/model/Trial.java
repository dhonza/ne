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
import net.phys2d.math.ROVector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps a record of information about a single trial that can be used later to calculate and compare different controller behaviors.
 *
 * @author Brian Woolley on Feb 23, 2011
 */
public class Trial {
    private int timeElapsed = -1;
    private int runLength = OctopusArm.getMaxTimesteps();
    private ArrayList<Double> energyExpended = new ArrayList<Double>();
    private ArrayList<Double> distToTarget = new ArrayList<Double>();
    private List<List<ROVector2f>> centerline = new ArrayList<List<ROVector2f>>();
    private List<ROVector2f> targetPosition = new ArrayList<ROVector2f>();
    private boolean hitTargetFlag = false;
    private int hitTargetCount = 0;

    /**
     * Captures key information about the run at this timestep.  The timestep is the index for accessing information
     * about the run to calculate various measures of fitness.
     *
     * @param state The current state of the simulation
     */
    public void step(ArmState state) {
        assert (state != null);

        timeElapsed++;
        distToTarget.add(timeElapsed, state.getDistanceToTarget());
        energyExpended.add(timeElapsed, state.getEnergyUsed());
        centerline.add(timeElapsed, state.getAlphaPositions());
        targetPosition.add(timeElapsed, state.getTargetPosition());

        if (hitTargetFlag) {
            hitTargetFlag = false;
            hitTargetCount++;
        }
    }

    /**
     * Signal that the target was hit during the trial.  Multiple calls to this
     * method are counted as the same event until {@link #step(ArmState)} is called.
     */
    public void hitTarget() {
        hitTargetFlag = true;
    }

    /**
     * Returns the number of hitTarget events that were recorded during this trial.
     *
     * @return The number of hitTarget events that have occurred.
     */
    public int getTargetHitCount() {
        return hitTargetCount;
    }

    /**
     * Returns the number of timesteps that have elapsed since the trial began.
     *
     * @return The number of timesteps that have elapsed since the trial began.
     */
    public int getTimeElapsed() {
        return timeElapsed;
    }

    /**
     * Returns the total energy expended by the arm during this trial (i.e. from timestep 0 to timeElapsed).
     *
     * @return The total energy expended by the arm in this trial.
     */
    public double getTotalEnergyExpended() {
        return getEnergyExpended(0, timeElapsed);
    }

    /**
     * Returns the energy expended by the arm during the simulation time period specified.
     *
     * @param fromTimestep The first timestep in the query.
     * @param toTimestep   The last timestep in the query.
     * @return The energy expended during the given period.
     */
    public double getEnergyExpended(int fromTimestep, int toTimestep) {
        double total = 0.0;
        for (Double energy : energyExpended.subList(fromTimestep, toTimestep)) {
            total += energy;
        }
        return total;
    }

    /**
     * Returns the average distance of the tip of the arm to the target during the trial.
     * When a trial ends early (i.e. before maxTimesteps) the final distance is used to fill
     * the remaining timesteps, thus making comparisons between trials that touch the target
     * comparable to trials that end prematurely.
     *
     * @return average distance value during the simulation
     */
    public double getAverageDistToTarget() {
        double avg = 0.0;
        int padding = runLength - timeElapsed;
        for (Double x : distToTarget) {
            avg += x;
        }
        avg += padding * getFinalDistToTarget();
        return avg / runLength;
    }

    /**
     * Returns the distance from the arm tip to the target surface when the trial began.
     *
     * @return the initial distance from the arm tip to the target surface.
     */
    public double getInitialDistToTarget() {
        return distToTarget.get(0);
    }

    /**
     * Returns the distance from the arm tip to the target surface at the end of the trial.
     *
     * @return The final distance from the arm tip to the target surface.
     */
    public double getFinalDistToTarget() {
        return distToTarget.get(timeElapsed);
    }

    /**
     * Returns the maximum distance that the arm tip was from the target surface in this trial.
     *
     * @return The largest distance value recorded during the trial.
     */
    public double getMaxDistToTarget() {
        double max = 0.0;
        for (Double x : distToTarget) {
            max = Math.max(max, x);
        }
        return max;
    }

    /**
     * Get the minimum distance that the arm tip was from the target surface in this trial.
     *
     * @return The smallest distance value recorded during the trial.
     */
    public double getMinDistToTarget() {
        double min = 0.0;
        for (Double x : distToTarget) {
            min = Math.min(min, x);
        }
        return min;
    }

    /**
     * Returns the arm's motion trajectory during the trial.  By recording the position of the
     * arm's centerline, the trial provides a record of the arm's trajectory throughout the trial.
     *
     * @return The ordered list of alpha node positions.
     */
    public List<List<ROVector2f>> getMotionTrajectory() {
        return centerline;
    }

    /**
     * Returns the position of the target at the end of the trial.
     *
     * @return The final position of the target.
     */
    public ROVector2f getFinalTargetPosition() {
        return targetPosition.get(timeElapsed);
    }
}