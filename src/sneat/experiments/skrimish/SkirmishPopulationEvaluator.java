package sneat.experiments.skrimish;

import sneat.experiments.INetworkEvaluator;
import sneat.experiments.SingleFilePopulationEvaluator;

//TODO X was multithreaded

//class SkirmishPopulationEvaluator extends MultiThreadedPopulationEvaluator {
class SkirmishPopulationEvaluator extends SingleFilePopulationEvaluator {

    public SkirmishPopulationEvaluator(INetworkEvaluator eval) {
        super(eval, null);
    }
}

