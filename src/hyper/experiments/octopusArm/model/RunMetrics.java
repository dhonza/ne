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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Woolley
 *         This object is used to collect metrics about the simulation run
 */
public class RunMetrics {


    private Trial currentTrial;
    private List<Trial> trials = new ArrayList<Trial>();

    /**
     * Ends the current {@link Trial} and creates a new one.
     */
    public void newTrial() {
        currentTrial = new Trial();
        trials.add(currentTrial);
    }

    /**
     * Records specific metrics from the state of the run at this timestep.
     *
     * @param state The current {@link ArmState}
     */
    public void step(ArmState state) {
        currentTrial.step(state);
    }

    /**
     * Signal that the {@link Target} was hit during the trial.  Multiple calls to this
     * method are counted as the same event until {@link Trial#step(ArmState)} is called.
     */
    public void hitTargetEvent() {
        currentTrial.hitTarget();
    }

    /**
     * Returns the number of {@link Target}s hit during the run (all {@link Trial}s).
     *
     * @return The {@link Target} hit count
     */
    public int hitTargetCount() {
        int total = 0;
        for (Trial t : trials) {
            total += t.getTargetHitCount();
        }
        return total;
    }

    /**
     * Returns the number of timesteps seen since the {@link Trial} started.
     *
     * @return The number of timesteps since the {@link Trial} started.
     */
    public int getTimeElapsed() {
        return currentTrial.getTimeElapsed();
    }

    /**
     * Returns the total energy used by the system since the run started (all trials).
     *
     * @return The amount of energy used since the run started.
     */
    public double getTotalEnergyExpended() {
        double total = 0.0;
        for (Trial t : trials) {
            total += t.getTotalEnergyExpended();
        }
        return total;
    }

    /**
     * Returns the average distance of the tip of the arm to the target during the run (all trials).
     *
     * @return average distance value during the simulation.
     */
    public double getAverageDistToTarget() {
        double avg = 0.0;
        for (Trial t : trials) {
            avg += t.getAverageDistToTarget();
        }
        return avg / trials.size();
    }

    /**
     * Provides the list of trials that were performed during this run
     *
     * @return the list of trials
     */
    public List<Trial> getTrials() {
        return trials;
    }
}
