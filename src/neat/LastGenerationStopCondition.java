package neat;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastGenerationStopCondition implements StopCondition {
    final private NEAT neat;

    public LastGenerationStopCondition(NEAT neat) {
        this.neat = neat;
    }

    public boolean isMet() {
        return neat.getPopulation().getGeneration() >= NEAT.getConfig().lastGeneration;
    }
}