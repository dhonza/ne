package hyper.evaluate;

import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.cppn.FakeArrayCPPN;
import opt.sade.ObjectiveFunction;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:30:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectSADEObjectiveFunction implements ObjectiveFunction {
    final private EvaluableSubstrateBuilder substrateBuilder;
    final private Problem problem;

    public DirectSADEObjectiveFunction(EvaluableSubstrateBuilder substrateBuilder, Problem problem) {
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public int getDim() {
        return 9;
    }

    public double getDomain(int x, int y) {
        if (y == 0) {
            return -10;
        }
        if (y == 1) {
            return 10;
        }
        return 0;
    }

    public double getOptimum() {
        return 4;
    }

    public double getPrecision() {
        return 0.0001;
    }

    public boolean getReturnToDomain() {
        return false;
    }

    public double value(double[] weights) {
        CPPN aCPPN = new FakeArrayCPPN(weights, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        double fitness = problem.evaluate(hyperNet).getFitness();
//        System.out.println(fitness);
        return fitness;
    }

    public boolean isFeasible(double[] doubles) {
        return true;
    }
}
