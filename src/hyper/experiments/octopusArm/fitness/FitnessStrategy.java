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

import hyper.experiments.octopusArm.model.RunMetrics;
import hyper.experiments.octopusArm.model.Trial;

/**
 * A strategy pattern for interchangeable fitness functions.
 *
 * @author Brian Woolley on 19 October 2009
 */
public interface FitnessStrategy {

    /**
     * Calculates the fitness over all trials based on the run results provided.
     *
     * @param results Result of the evaluation run.
     * @return The fitness over all trials based on the run results provided.
     */
    public int calculateFitness(RunMetrics results);

    /**
     * Calculates the fitness over a single trial.
     *
     * @param trial The results of a single trial.
     * @return The fitness of this trial.
     */
    public int calculateFitness(Trial trial);

    /**
     * The maximum theoretic fitness score that can be returned by the fitness function.
     *
     * @return The maximum possible fitness score.
     */
    public int getMaxFitnessValue();
}
