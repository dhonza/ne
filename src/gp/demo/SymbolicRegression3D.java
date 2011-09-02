package gp.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import common.pmatrix.ParameterCombination;
import gp.GP;
import gp.IGPForest;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Tests for 2 inputs.
 */
public class SymbolicRegression3D implements IEvaluable<IGPForest> {
    private interface F {
        double f(double x, double y, double z);
    }

    private final F f;
    private final int steps;
    private boolean solved = false;

    public SymbolicRegression3D(ParameterCombination combination) {
        steps = combination.getInteger("SYMBOLIC_REGRESSION_3D.STEPS");
        String fName = combination.getString("SYMBOLIC_REGRESSION.F");
        if (fName.equals("A")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return x * y * z;
                }
            };
        } else if (fName.equals("B")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return -x * y * z;
                }
            };
        } else if (fName.equals("C")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y * z;
                }
            };
        } else if (fName.equals("D")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y + 2.3 * x - 1.1 * z;
                }
            };
        } else if (fName.equals("E")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y + 2.3 * x + y * z - 1.1 * z;
                }
            };
        } else if (fName.equals("F")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y * z + 2.3 * x - 1.1 * y - 1.1 * z;
                }
            };
        } else if (fName.equals("G")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y * y * z + 2.3 * x - 1.1 * y;
                }
            };
        } else if (fName.equals("H")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y * y * z + 2.3 * x * y * z - 1.1 * y + 5.3;
                }
            };
        } else if (fName.equals("H2")) {//H bez konstant
            f = new F() {
                public double f(double x, double y, double z) {
                    return x * y * y * z + x * y * z - y;
                }
            };
        } else if (fName.equals("I")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return 1.5 * x * y * y + 2.3 * x * y * z - 1.1 * y * y;
                }
            };
        } else if (fName.equals("J")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return x * y * y + x * z - y * z;
                }
            };
        } else if (fName.equals("K")) {
            f = new F() {
                public double f(double x, double y, double z) {
                    return x * y * y * z + z * y - x * y;
                }
            };
        } else {
            throw new IllegalStateException("Bad \"SYMBOLIC_REGRESSION_3D.F\" given!");
        }
    }

    public EvaluationInfo evaluate(IGPForest forest) {
        double startX = -10.0;
        double endX = 10.0;
        double scaleX = endX - startX;
        double stepX = scaleX / (steps - 1);
        double error = 0.0;
        double diff;
        for (int i0 = 0; i0 < steps; i0++) {
            for (int i1 = 0; i1 < steps; i1++) {
                for (int i2 = 0; i2 < steps; i2++) {
                    double x = startX + i0 * stepX;
                    double y = startX + i1 * stepX;
                    double z = startX + i2 * stepX;
                    forest.loadInputs(new double[]{x, y, z});
                    double output = forest.getOutputs()[0];
                    diff = f.f(x, y, z) - output;

                    error += diff * diff;
                }
            }
        }
        //Now it's MSE.
        error /= (steps * steps * steps);
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
        return 3;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}