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
    final private GP gp;

    public MaxEvaluationsStopCondition(GP gp) {
        this.gp = gp;
    }

    public boolean isMet() {
        return gp.getEvaluations() >= GP.MAX_EVALUATIONS;
    }
}