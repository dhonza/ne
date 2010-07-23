package sneat.experiments.skrimish;

import common.evolution.ParallelPopulationEvaluator;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.INetwork;

//class SkirmishPopulationEvaluator extends MultiThreadedPopulationEvaluator {
class SkirmishPopulationEvaluator<P> extends SingleFilePopulationEvaluator<P> {

    public SkirmishPopulationEvaluator(ParallelPopulationEvaluator<INetwork, P> populationEvaluator) {
        super(populationEvaluator, null);
    }
}

