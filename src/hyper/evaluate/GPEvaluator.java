package hyper.evaluate;

import gp.Evaluable;
import gp.Forest;
import hyper.builder.NEATSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.CPPN;
import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 20, 2009
 * Time: 3:45:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPEvaluator implements Evaluable {
    final private NEATSubstrateBuilder substrateBuilder;
    final private Problem problem;

    public GPEvaluator(NEATSubstrateBuilder substrateBuilder, Problem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public double evaluate(Forest forest) {
        CPPN aCPPN = new BasicGPCPPN(forest, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        Net hyperNet = substrateBuilder.getNet();

        return problem.evaluate(hyperNet);
    }

    public int getNumberOfInputs() {
        return 2 * substrateBuilder.getSubstrate().getMaxDimension();
    }

    public int getNumberOfOutputs() {
        return substrateBuilder.getSubstrate().getNumOfConnections();
    }
}
