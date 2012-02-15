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
package hyper.experiments.octopusArm.fitness;

import hyper.experiments.octopusArm.OctopusArm;
import hyper.experiments.octopusArm.model.RunMetrics;
import hyper.experiments.octopusArm.model.Trial;

/**
 * This fitness function scores individuals on the arm's average distance to the target during the trial.
 * Measuring the average distance from the arm tip to the target simultaneously rewards speed and the ability
 * to approach the target.  When the target is touched during a trial the average distance score is increased
 * by 25%.  In this way arm controllers that do touch the target are rewarded even if they are not the fastest
 * solution in the population.  The energy expended during the run is not a factor.
 *
 * @author Brian Woolley
 */
public class AvgDistToTarget implements FitnessStrategy {

    /**
     * Calculates the fitness (sum of all trials) based on the results of the run provided.
     *
     * @param results The {@link RunMetrics} to be evaluated.
     * @return The fitness score (averaged over trials).
     * @see hyper.experiments.octopusArm.fitness.FitnessStrategy#calculateFitness
     */
    public int calculateFitness(RunMetrics results) {
        int fitness = 0;
        for (Trial t : results.getTrials()) {
            fitness += calculateFitness(t);
        }
        return Math.min(fitness, getMaxFitnessValue());
    }

    /**
     * Calculate the fitness of a single trial.
     *
     * @param trial The results of a single trial.
     * @return The fitness score of this trial.
     * @see FitnessStrategy#calculateFitness(Trial)
     */
    public int calculateFitness(Trial trial) {
        // Reduce fitness as a function of the average distance of the arm tip from the target
        double avgDist = trial.getAverageDistToTarget();
        double approachRwd = tgtValue - tgtValue * (Math.pow(avgDist, 2));
        approachRwd = Math.max(approachRwd, 0.0);
        approachRwd = Math.min(approachRwd, tgtValue);

        // Reward touching the target with a 25% bonus
        if (trial.getTargetHitCount() > 0)
            approachRwd *= 1.25;

        return (int) Math.floor(approachRwd);
    }

    /**
     * The maximum theoretic fitness score is 1.25 times {@link OctopusArm#getTargetReward()}.
     *
     * @return The maximum possible fitness score.
     */
    public int getMaxFitnessValue() {
        return (int) Math.floor(1.25 * tgtValue * OctopusArm.getNumTrials());
    }

    private int tgtValue = OctopusArm.getTargetReward();
}
