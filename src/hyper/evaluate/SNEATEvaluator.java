package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.CPPN;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 10:39:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATEvaluator implements Evaluable<INetwork> {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private IProblem problem;

    public SNEATEvaluator(EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(INetwork network) {
        CPPN aCPPN = new BasicSNEATCPPN(network, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        INet hyperNet = substrateBuilder.getNet();

        return problem.evaluate(hyperNet);
    }

    public EvaluationInfo evaluateGeneralization(INetwork network) {
        CPPN aCPPN = new BasicSNEATCPPN(network, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        INet hyperNet = substrateBuilder.getNet();

        return ((IProblemGeneralization)problem).evaluateGeneralization(hyperNet);
    }

    public boolean isSolved() {
        return problem.isSolved();
    }

    public int getNumberOfInputs() {
        return 2 * substrateBuilder.getSubstrate().getMaxDimension();
    }

    public int getNumberOfOutputs() {
        return substrateBuilder.getSubstrate().getNumOfLayerConnections();
    }
}