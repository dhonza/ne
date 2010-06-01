package gp;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 3:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MaxGenerationsStopCondition implements StopCondition {
    final private GP gp;

    public MaxGenerationsStopCondition(GP gp) {
        this.gp = gp;
    }

    public boolean isMet() {
        return gp.getGeneration() >= GP.MAX_GENERATIONS;
    }
}
