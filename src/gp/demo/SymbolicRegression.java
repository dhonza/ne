package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import common.pmatrix.ParameterCombination;
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
    private interface F {
        double f(double x);
    }

    private final F f;
    private final int steps;
    private final double a; //1st parameter
    private boolean solved = false;

    public SymbolicRegression(ParameterCombination combination) {
        steps = combination.getInteger("SYMBOLIC_REGRESSION_1D.STEPS");
        a = combination.getDouble("SYMBOLIC_REGRESSION_1D.A");
        String fName = combination.getString("SYMBOLIC_REGRESSION.F");
        if (fName.equals("A")) {
            f = new F() {
                public double f(double x) {
                    return x;
                }
            };
        } else if (fName.equals("B")) {
            f = new F() {
                public double f(double x) {
                    return -x;
                }
            };
        } else if (fName.equals("C")) {
            f = new F() {
                public double f(double x) {
                    return 1.5 * x;
                }
            };
        } else if (fName.equals("D")) {
            f = new F() {
                public double f(double x) {
                    return 1.5 * x + 2.3;
                }
            };
        } else if (fName.equals("E")) {
            f = new F() {
                public double f(double x) {
                    return 1.5 * x * x + 2.3 * x - 1.1;
                }
            };
        } else if (fName.equals("F")) {
            f = new F() {
                public double f(double x) {
                    return 1.5 * x * x * x + 2.3 * x * x - 1.1 * x + 3.7;
                }
            };
        } else if (fName.equals("G")) {
            f = new F() {
                public double f(double x) {
                    return 1.5 * x * x * x * x + 2.3 * x * x * x - 1.1 * x * x + 3.7 * x - 4.5;
                }
            };
        } else if (fName.equals("G2")) {//same as G but normalized to <0;1>
            f = new F() {
                public double f(double x) {
                    return -0.0002612861082885761 + 0.0002148352445928292 * x - 0.00006386993758165193 * x * x + 0.0001335462331252722 * x * x * x + 0.00008709536942952533 * x * x * x * x;
                }
            };
        } else if (fName.equals("H")) {
            f = new F() {
                public double f(double x) {
                    return 0.1 * x * x + a * Math.sin(x);
                }
            };
        } else {
            throw new IllegalStateException("Bad \"SYMBOLIC_REGRESSION_1D.F\" given!");
        }
        //other older functions
//            error -= Math.abs((x * x * x + 1.5) - output);
//            error -= Math.abs((x * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((x * x * x + -5 * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((-1.1 * x * x * x + 2.3 * x + 1.5) - output);
//            error -= Math.abs((x * x) - output);
//            error -= Math.abs(x - output);
//            error -= Math.abs(1.5 - output);
    }

    public EvaluationInfo evaluate(IGPForest forest) {
        double startX = -10.0;
        double endX = 10.0;
        double scaleX = endX - startX;
        double stepX = scaleX / (steps - 1);
        double error = 0.0;
        double diff;
        for (int i0 = 0; i0 < steps; i0++) {
            double x = startX + i0 * stepX;
            forest.loadInputs(new double[]{x});
            double output = forest.getOutputs()[0];
            diff = f.f(x) - output;

            error += diff * diff;
        }
        //Now it's MSE.
        error /= steps;
        //To <0;1>.
        error = 1 / (1 + error);

        if (error >= GP.TARGET_FITNESS) {
            solved = true;
        }
        return new EvaluationInfo(error);
    }

    public EvaluationInfo evaluateGeneralization(IGPForest forest) {
        return evaluate(forest);
    }

    public void show(IGPForest individual) {
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