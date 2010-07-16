package opt.cmaes;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TargetFitnessStopCondition implements StopCondition {
    final private CMAES cmaes;

    public TargetFitnessStopCondition(CMAES cmaes) {
        this.cmaes = cmaes;
    }

    public boolean isMet() {
        return cmaes.getMaxFitnessReached() >= cmaes.getOptions().stopFitness;
    }
}