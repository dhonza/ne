package opt.sade;

import common.evolution.IStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxEvaluationsStopCondition implements IStopCondition {
    final private SADE sade;

    public MaxEvaluationsStopCondition(SADE sade) {
        this.sade = sade;
    }

    public boolean isMet() {
        return sade.getEvaluations() >= sade.fitnessCallsLimit;
    }
}