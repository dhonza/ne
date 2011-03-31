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
 * Used to "learn" FindCluster 4D substrate.
 */
public class SymbolicRegression4D implements IEvaluable<IGPForest> {
    private boolean solved = false;

    public EvaluationInfo evaluate(IGPForest forest) {
        int steps = 5;
        double startX = -1.0;
        double endX = 1.0;
        double scaleX = endX - startX;
        double stepX = scaleX / (steps - 1);
        double error = 0.0;
        for (int i0 = 0; i0 < steps; i0++) {
            for (int i1 = 0; i1 < steps; i1++) {
                for (int i2 = 0; i2 < steps; i2++) {
                    for (int i3 = 0; i3 < steps; i3++) {
                        double x0 = startX + i0 * stepX;
                        double x1 = startX + i1 * stepX;
                        double x2 = startX + i2 * stepX;
                        double x3 = startX + i3 * stepX;
                        forest.loadInputs(new double[]{x0, x1, x2, x3});
                        double output = forest.getOutputs()[0];
                        double f = Math.exp(-1.3670864746106228 * x2 * x2) * x1 *
                                (2.434541692158718 * Math.exp(-x3 * x3) - 0.3937877771163354 * Math.sin(1 - x1));
                        error -= Math.abs(f - output);
                    }
                }
            }
        }
        error /= steps;
        error = 100 + error;
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
        return 4;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}