package hyper.evaluate;

import common.evolution.IStopCondition;
import common.evolution.PopulationManager;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 4:15:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolvedStopCondition<G, P> implements IStopCondition {
    final private PopulationManager<G, P> populationManager;

    public SolvedStopCondition(PopulationManager<G, P> populationManager) {
        this.populationManager = populationManager;
    }

    public boolean isMet() {
        return populationManager.isSolved();
    }
}
