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
 * Used to "learn" FindCluster 4D substrate.
 */
public class SymbolicRegression4D implements IEvaluable<IGPForest> {
    private interface F {
        double f(double x1, double x2, double x3, double x4);
    }

    private final F f;
    private final int steps;
    private boolean solved = false;

    public SymbolicRegression4D(ParameterCombination combination) {
        steps = combination.getInteger("SYMBOLIC_REGRESSION_4D.STEPS");
        String fName = combination.getString("SYMBOLIC_REGRESSION_4D.F");
        if (fName.equals("A")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return x1 * x2 * x3 * x4;
                }
            };
        } else if (fName.equals("B")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return -x1 * x2 * x3 * x4;
                }
            };
        } else if (fName.equals("C")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return 1.5 * x1 * x2 * x3 * x4;
                }
            };
        } else if (fName.equals("D")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return 1.5 * x1 * x2 + 2.3 * x1 - 1.1 * x3 * x4;
                }
            };
        } else if (fName.equals("E")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return 1.5 * x1 * x2 + 2.3 * x1 + x2 * x3 * x4 - 1.1 * x3;
                }
            };
        } else if (fName.equals("F")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return 1.5 * x1 * x2 * x3 + 2.3 * x1 - 1.1 * x2 - 1.1 * x4;
                }
            };
        } else if (fName.equals("G")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return 1.5 * x1 * x2 * x2 * x3 * x4 + 2.3 * x1 - 1.1 * x2;
                }
            };
        } else if (fName.equals("H")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return x1 * x2 * x3 + x1 - x2 - x4;
                }
            };
        } else if (fName.equals("I")) {
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return x1 * x2 * x2 * x3 * x4 + x1 - x2 * x4;
                }
            };
        } else if (fName.equals("FC55")) {//FIND CLUSTER 5x5 solution
            f = new F() {
                public double f(double x1, double x2, double x3, double x4) {
                    return Math.exp(-1.3670864746106228 * x2 * x2) * x1 *
                            (2.434541692158718 * Math.exp(-x3 * x3) - 0.3937877771163354 * Math.sin(1 - x1));
                }
            };
        } else {
            throw new IllegalStateException("Bad \"SYMBOLIC_REGRESSION_4D.F\" given!");
        }
    }

    public EvaluationInfo evaluate(IGPForest forest) {
        double startX = -1.0;
        double endX = 1.0;
        double scaleX = endX - startX;
        double stepX = scaleX / (steps - 1);
        double error = 0.0;
        double diff;
        for (int i1 = 0; i1 < steps; i1++) {
            for (int i2 = 0; i2 < steps; i2++) {
                for (int i3 = 0; i3 < steps; i3++) {
                    for (int i4 = 0; i4 < steps; i4++) {
                        double x1 = startX + i1 * stepX;
                        double x2 = startX + i2 * stepX;
                        double x3 = startX + i3 * stepX;
                        double x4 = startX + i4 * stepX;
                        forest.loadInputs(new double[]{x1, x2, x3, x4});
                        double output = forest.getOutputs()[0];
                        diff = f.f(x1, x2, x3, x4) - output;

                        error += diff * diff;
                    }
                }
            }
        }
        //Now it's MSE.
        error /= (steps * steps * steps * steps);
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