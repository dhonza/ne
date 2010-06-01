package sneat;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxEvaluationsStopCondition implements StopCondition {
    final private SNEAT sneat;

    public MaxEvaluationsStopCondition(SNEAT sneat) {
        this.sneat = sneat;
    }

    public boolean isMet() {
        return sneat.getEvaluations() >= sneat.getNeatParameters().maxEvaluations;
    }
}