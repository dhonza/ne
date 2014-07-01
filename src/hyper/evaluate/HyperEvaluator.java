package hyper.evaluate;

import com.google.common.collect.ImmutableList;
import common.evolution.EvaluationInfo;
import common.evolution.IBehavioralDiversity;
import common.evolution.IEvaluable;
import hyper.builder.IEvaluableSubstrateBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:34:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class HyperEvaluator<INet> implements IEvaluable<INet>, IBehavioralDiversity {
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
        return ((IProblemGeneralization<INet>) problem).evaluateGeneralization(hyperNet);
    }

    public void show(INet individual) {
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

    @Override
    public ImmutableList<Double> behavioralDiversity(ImmutableList<EvaluationInfo> evaluationInfos) {
        //Class cast exception when not implementing IBehavioralDiversity
        return ((IBehavioralDiversity) problem).behavioralDiversity(evaluationInfos);
    }
}
