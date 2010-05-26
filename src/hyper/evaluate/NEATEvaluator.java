package hyper.evaluate;

import hyper.builder.NEATSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import neat.Evaluable;
import neat.Genome;
import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 11:09:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATEvaluator implements Evaluable {
    final private NEATSubstrateBuilder substrateBuilder;
    final private Problem problem;

    public NEATEvaluator(NEATSubstrateBuilder substrateBuilder, Problem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public double evaluate(Genome og) {
        CPPN aCPPN = new BasicNetCPPN(og.getNet(), substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        Net hyperNet = substrateBuilder.getNet();

        return problem.evaluate(hyperNet);
    }

    public void evaluateAll(Genome[] opop, double[] ofitnessValues) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public int getNumberOfInputs() {
        return 2 * substrateBuilder.getSubstrate().getMaxDimension();
    }

    public int getNumberOfOutputs() {
        return substrateBuilder.getSubstrate().getNumOfConnections();
    }

    public void storeEvaluation(Genome og) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public double[][][] getStoredInputs() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public double[][][] getStoredOutputs() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
