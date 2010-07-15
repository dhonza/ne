package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.cppn.FakeArrayCPPN;
import opt.sade.SADEGenome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 20, 2009
 * Time: 3:45:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectSADEEvaluator implements Evaluable<SADEGenome> {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private IProblem problem;

    public DirectSADEEvaluator(EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public EvaluationInfo evaluate(SADEGenome genome) {
        CPPN aCPPN = new FakeArrayCPPN(genome.genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        return problem.evaluate(hyperNet);
    }

    public EvaluationInfo evaluateGeneralization(SADEGenome genome) {
        CPPN aCPPN = new FakeArrayCPPN(genome.genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        return ((IProblemGeneralization) problem).evaluateGeneralization(hyperNet);
    }

    public boolean isSolved() {
        return problem.isSolved();
    }

    public int getNumOfLinks() {
        return substrateBuilder.getSubstrate().getNumOfLinks();
    }

    public int getNumberOfInputs() {
        throw new IllegalStateException("DirectSADEEvaluator.getNumberOfInputs() not supported for direct methods");
    }

    public int getNumberOfOutputs() {
        throw new IllegalStateException("DirectSADEEvaluator.getNumberOfInputs() not supported for direct methods");
    }
}