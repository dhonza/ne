package sneat.experiments.skrimish;

import common.evolution.Evaluable;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.INetwork;

//class SkirmishPopulationEvaluator extends MultiThreadedPopulationEvaluator {
class SkirmishPopulationEvaluator extends SingleFilePopulationEvaluator {

    public SkirmishPopulationEvaluator(Evaluable<INetwork> eval) {
        super(eval, null);
    }
}

