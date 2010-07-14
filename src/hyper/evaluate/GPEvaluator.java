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
 * Date: Jun 20, 2009
 * Time: 3:45:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPEvaluator implements Evaluable<Forest> {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private IProblem problem;

    public GPEvaluator(EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(Forest forest) {
        CPPN aCPPN = new BasicGPCPPN(forest, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        return problem.evaluate(hyperNet);
    }

    public EvaluationInfo evaluateGeneralization(Forest individual) {
        CPPN aCPPN = new BasicGPCPPN(individual, substrateBuilder.getSubstrate().getMaxDimension());
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
