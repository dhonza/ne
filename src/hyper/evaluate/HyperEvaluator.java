package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.net.INet;
import gp.Forest;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.CPPN;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:34:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class HyperEvaluator implements Evaluable<INet> {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private IProblem problem;

    public HyperEvaluator(EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        return problem.evaluate(hyperNet);
    }

    public EvaluationInfo evaluateGeneralization(INet hyperNet) {
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
