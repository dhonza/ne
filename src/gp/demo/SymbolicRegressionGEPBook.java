package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import gp.IGPForest;

/**
 * This is a simple symbolic regression y = (a^2)/2 + 3a task from the GEP book (chapter 3.4)
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 5:02:46 PM
 */
public class SymbolicRegressionGEPBook implements IEvaluable<IGPForest> {
    private boolean solved = false;

    //ten randomly sampled points
    double[][] data = new double[][]{
            {6.9408, 44.909752},
            {-7.8664, 7.3409245},
            {-2.7861, -4.4771234},
            {-5.0944, -2.3067443},
            {9.4895, 73.493805},
            {-9.6197, 17.410214},
            {-9.4145, 16.072905},
            {-0.1432, -0.41934688},
            {0.9107, 3.1467872},
            {2.1762, 8.8965232}
    };


    public EvaluationInfo evaluate(IGPForest forest) {
        double sum = 0;
        boolean solved = true;
        for (int i = 0; i < data.length; i++) {
            forest.loadInputs(new double[]{data[i][0]});
            double output = forest.getOutputs()[0];
            double diff = Math.abs(output - data[i][1]);
            if (diff > 0.01) {
                solved = false;
            }
            sum += 100 - diff;//selection range 100
        }
        this.solved = solved;
        return new EvaluationInfo(sum);
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