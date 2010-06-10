package hyper.evaluate;

import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.CPPN;
import sneat.experiments.INetworkEvaluator;
import sneat.neuralnetwork.INetwork;

import java.util.concurrent.Semaphore;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 10:39:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATNetworkEvaluator implements INetworkEvaluator {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private Problem problem;

    public SNEATNetworkEvaluator(EvaluableSubstrateBuilder substrateBuilder, Problem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public double evaluateNetwork(INetwork network) {
        CPPN aCPPN = new BasicSNEATCPPN(network, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        INet hyperNet = substrateBuilder.getNet();

        return problem.evaluate(hyperNet);
    }

    public double threadSafeEvaluateNetwork(INetwork network, Semaphore sem) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public boolean isSolved() {
        return problem.isSolved();
    }

    public String getEvaluatorStateMessage() {
        return "";
    }
}