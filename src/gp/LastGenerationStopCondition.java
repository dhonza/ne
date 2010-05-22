package gp;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastGenerationStopCondition implements StopCondition {
    final private GP gp;

    public LastGenerationStopCondition(GP gp) {
        this.gp = gp;
    }

    public boolean isMet() {
        return gp.getGeneration() >= GP.LAST_GENERATION;
    }
}
