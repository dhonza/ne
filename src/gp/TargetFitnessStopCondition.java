package gp;

import common.evolution.IStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TargetFitnessStopCondition implements IStopCondition {
    final private IGP gp;
    final private double targetFitness;

    public TargetFitnessStopCondition(IGP gp, double targetFitness) {
        this.gp = gp;
        this.targetFitness = targetFitness;
    }

    public boolean isMet() {
        return gp.getBestSoFar().getFitness() >= targetFitness;
    }
}