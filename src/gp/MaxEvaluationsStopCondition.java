package gp;

import common.evolution.IStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxEvaluationsStopCondition implements IStopCondition {
    final private IGP gp;
    final private int maxEvaluations;

    public MaxEvaluationsStopCondition(IGP gp, int maxEvaluations) {
        this.gp = gp;
        this.maxEvaluations = maxEvaluations;
    }

    public boolean isMet() {
        return gp.getEvaluations() >= maxEvaluations;
    }
}