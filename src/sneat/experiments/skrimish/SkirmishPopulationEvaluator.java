package sneat.experiments.skrimish;

import common.evolution.Evaluable;
import common.evolution.GenotypeToPhenotype;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.INetwork;

//class SkirmishPopulationEvaluator extends MultiThreadedPopulationEvaluator {
class SkirmishPopulationEvaluator<P> extends SingleFilePopulationEvaluator<P> {

    public SkirmishPopulationEvaluator(GenotypeToPhenotype<INetwork, P>[] perThreadConverters, Evaluable<P>[] perThreadEvaluators) {
        super(perThreadConverters, perThreadEvaluators, null);
    }
}

