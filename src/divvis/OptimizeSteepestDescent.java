package divvis;

import common.MachineAccuracy;
import common.function.BasicObjectiveFunction;
import divvis.numopt.LineSearchException;
import divvis.numopt.LineSearchFactory;
import divvis.numopt.SteepestDescentMinimization;
import divvis.numopt.SteepestDescentMinimizationException;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 22:08:17
 */
public class OptimizeSteepestDescent extends BasicObjectiveFunction implements OptimizeInterface {
    private int args;
    private double[][] D;

    private EquationsInterface equations;

    public OptimizeSteepestDescent(double[][] oD, EquationsInterface oequations) {
        this.D = oD;
        this.equations = oequations;
        this.args = oequations.getDimension() * oD.length;
//        NumericalDifferentiation.checkAnalyticGradient(this, op);
    }

    public OptimizeSteepestDescent(double[][] oD, int oargs) {
        this(oD, EquationsFactory.createDefault());
    }

    public double evaluate(double[] oxvec) {
        numEvaluateCalls++;
        return equations.evaluate(D, oxvec);
    }

    public void gradient(double[] oxvec, double[] ograd) {
        numGradientCalls++;
        equations.gradient(D, oxvec, ograd);
    }


    public void hessian(double[] oxvec, double[][] ohessian) {
        numHessianCalls++;
        System.out.println("OptimizeSteepestDescent: analytic hessian not implemented!");
    }

    public int getNumArguments() {
        return args;
    }

    public boolean isAnalyticGradient() {
        return true;
    }

    public boolean isAnalyticHessian() {
        return false;
    }

    public double minimize(double[] op) {
        SteepestDescentMinimization sdm = new SteepestDescentMinimization(this, LineSearchFactory.createDefault(this), MachineAccuracy.SQRT_EPSILON, 20000);
//        SteepestDescentMinimization sdm = new SteepestDescentMinimization(this, LineSearchFactory.createDefault(this), 1000*MachineAccuracy.SQRT_EPSILON, 20000);

        try {
            sdm.minimize(op);
        } catch (SteepestDescentMinimizationException e) {
            e.printStackTrace();
        } catch (LineSearchException e) {
            e.printStackTrace();
        }
        System.out.print("iteration " + sdm.getIteration() + ": f(x)=" + sdm.getFx());
        System.out.println(" evaluations f:" + getNumEvaluateCalls() + " d:" + getNumGradientCalls() + " H:" + getNumHessianCalls());
        System.out.println("Steepest Descent finished");
        return sdm.getFx();
    }
}
