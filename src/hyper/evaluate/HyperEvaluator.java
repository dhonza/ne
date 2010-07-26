package hyper.evaluate;

import common.evolution.IEvaluable;
import common.evolution.EvaluationInfo;
import hyper.builder.IEvaluableSubstrateBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:34:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class HyperEvaluator<INet> implements IEvaluable<INet> {
    final private IEvaluableSubstrateBuilder substrateBuilder;
    final private IProblem<INet> problem;

    public HyperEvaluator(IEvaluableSubstrateBuilder substrateBuilder, IProblem<INet> problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        return problem.evaluate(hyperNet);
    }

    public EvaluationInfo evaluateGeneralization(INet hyperNet) {
        return ((IProblemGeneralization<INet>)problem).evaluateGeneralization(hyperNet);
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
