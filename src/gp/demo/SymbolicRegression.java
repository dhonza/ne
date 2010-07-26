package gp.demo;

import common.evolution.IEvaluable;
import common.evolution.EvaluationInfo;
import gp.Forest;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 5:02:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SymbolicRegression implements IEvaluable<Forest> {
    public EvaluationInfo evaluate(Forest forest) {
        int steps = 20;
        double startX = -10.0;
        double endX = 10.0;
        double scaleX = endX - startX;
        double stepX = scaleX / (steps - 1);
        double x = startX;
        double error = 0.0;
        for (int i = 0; i < steps; i++) {
            forest.loadInputs(new double[]{x});
            double output = forest.getOutputs()[0];
            error -= Math.abs((x * x * x + 1.5) - output);
            x += stepX;
        }
        return new EvaluationInfo(error / steps);
    }

    public EvaluationInfo evaluateGeneralization(Forest forest) {
        return evaluate(forest);
    }

    public boolean isSolved() {
        return false;
    }

    public int getNumberOfInputs() {
        return 1;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}