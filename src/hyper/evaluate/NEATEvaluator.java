package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import neat.Genome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 11:09:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATEvaluator implements Evaluable<Genome> {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private Problem problem;

    public NEATEvaluator(EvaluableSubstrateBuilder substrateBuilder, Problem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(Genome og) {
        CPPN aCPPN = new BasicNetCPPN(og.getNet(), substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);

        INet hyperNet = substrateBuilder.getNet();

        return problem.evaluate(hyperNet);
    }

    public boolean isSolved() {
        return problem.isSolved();
    }

    public int getNumberOfInputs() {
        return 2 * substrateBuilder.getSubstrate().getMaxDimension();
    }

    public int getNumberOfOutputs() {
        return substrateBuilder.getSubstrate().getNumOfConnections();
    }
}
