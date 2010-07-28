package sneat.experiments.skrimish;

import common.evolution.PopulationManager;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.INetwork;

//class SkirmishPopulationEvaluator extends MultiThreadedPopulationEvaluator {
class SkirmishPopulationEvaluator<P> extends SingleFilePopulationEvaluator<P> {

    public SkirmishPopulationEvaluator(PopulationManager<INetwork, P> populationManager) {
        super(populationManager, null);
    }
}

