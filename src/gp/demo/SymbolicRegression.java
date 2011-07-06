package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import gp.GP;
import gp.IGPForest;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 5:02:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SymbolicRegression implements IEvaluable<IGPForest> {
    private boolean solved = false;

    public EvaluationInfo evaluate(IGPForest forest) {
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
//            error -= Math.abs((x * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((x * x * x + -5 * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((-1.1 * x * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((x * x) - output);
//            error -= Math.abs(x - output);
//            error -= Math.abs(1.5 - output);
            x += stepX;
        }
        error /= steps;
        error = 30 + error;
        if (error < 0.0) {
            error = 0.0;
        }
        if (error >= GP.TARGET_FITNESS) {
            solved = true;
        }
        return new EvaluationInfo(error);
    }

    public EvaluationInfo evaluateGeneralization(IGPForest forest) {
        return evaluate(forest);
    }

    public boolean isSolved() {
        return solved;
    }

    public int getNumberOfInputs() {
        return 1;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}