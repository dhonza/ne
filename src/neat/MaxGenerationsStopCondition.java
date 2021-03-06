package neat;

import common.evolution.IStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxGenerationsStopCondition implements IStopCondition {
    final private NEAT neat;

    public MaxGenerationsStopCondition(NEAT neat) {
        this.neat = neat;
    }

    public boolean isMet() {
        return neat.getPopulation().getGeneration() >= NEAT.getConfig().maxGenerations;
    }
}