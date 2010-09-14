package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import gp.IGPForest;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 5:02:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvolveConstant implements IEvaluable<IGPForest> {
    public EvaluationInfo evaluate(IGPForest forest) {
        double output = forest.getOutputs()[0];
        return new EvaluationInfo(0.0 - Math.abs(2.1 - output));
    }

    public EvaluationInfo evaluateGeneralization(IGPForest forest) {
        return evaluate(forest);
    }

    public boolean isSolved() {
        return false;
    }

    public int getNumberOfInputs() {
        return 0;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}
