package hyper.evaluate;

import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.cppn.FakeArrayCPPN;
import opt.cmaes.CMAESObjectiveFunction;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:30:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectCMAESObjectiveFunction implements CMAESObjectiveFunction {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private IProblem problem;

    private int numOfLinks;
    private boolean solved = false;

    public DirectCMAESObjectiveFunction(EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
        numOfLinks = substrateBuilder.getSubstrate().getNumOfLinks();
    }

    public int getDim() {
        return numOfLinks;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean isFeasible(double[] x) {
        return true;
    }

    public double valueOf(double[] weights) {
        CPPN aCPPN = new FakeArrayCPPN(weights, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        double fitness = problem.evaluate(hyperNet).getFitness();
        solved = solved || problem.isSolved();
//        System.out.println(fitness);
        return -fitness;
    }
}