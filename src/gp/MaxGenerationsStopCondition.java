package gp;

import common.evolution.IStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxGenerationsStopCondition implements IStopCondition {
    final private IGP gp;
    final private int maxGenerations;

    public MaxGenerationsStopCondition(IGP gp, int maxGenerations) {
        this.gp = gp;
        this.maxGenerations = maxGenerations;
    }

    public boolean isMet() {
        return gp.getGeneration() >= maxGenerations;
    }
}
