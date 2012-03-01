package divvis;

import common.MachineAccuracy;
import common.function.BasicObjectiveFunction;
import divvis.numopt.ConjugateGradientMinimization;
import divvis.numopt.ConjugateGradientMinimizationException;
import divvis.numopt.LineSearchException;
import divvis.numopt.LineSearchFactory;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 22:08:17
 */
public class OptimizeConjugateGradient extends BasicObjectiveFunction implements OptimizeInterface {
    private int args;
    private double[][] D;

    private EquationsInterface equations;

    public OptimizeConjugateGradient(double[][] oD, EquationsInterface oequations) {
        this.D = oD;
        this.args = oequations.getDimension() * oD.length;
        this.equations = oequations;
//        NumericalDifferentiation.checkAnalyticGradient(this, op);
    }

    public OptimizeConjugateGradient(double[][] oD, int oargs) {
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
        System.out.println("OptimizeConjugateGradient: analytic hessian not implemented!");
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
        ConjugateGradientMinimization cgs = new ConjugateGradientMinimization(this, LineSearchFactory.createDefault(this), MachineAccuracy.SQRT_EPSILON, ConjugateGradientMinimization.Method.BEALE_SORENSON_HESTENES_STIEFEL, 20000);
//        ConjugateGradientMinimization cgs = new ConjugateGradientMinimization(this, new LineSearchBrentWithDerivatives(this), MachineAccuracy.SQRT_EPSILON, ConjugateGradientMinimization.Method.BEALE_SORENSON_HESTENES_STIEFEL, 20000);
//        ConjugateGradientMinimization cgs = new ConjugateGradientMinimization(this, LineSearchFactory.createDefault(this), MachineAccuracy.SQRT_EPSILON*1000, ConjugateGradientMinimization.Method.BEALE_SORENSON_HESTENES_STIEFEL, 20000);

        try {
            cgs.minimize(op);
        } catch (ConjugateGradientMinimizationException e) {
            e.printStackTrace();
        } catch (LineSearchException e) {
            e.printStackTrace();
        }
        //System.out.print("iteration " + cgs.getIteration() + ": f(x)=" + cgs.getFx());
        //System.out.println(" evaluations f:" + getNumEvaluateCalls() + " d:" + getNumGradientCalls() + " H:" + getNumHessianCalls());
        //System.out.println("Conjugate Gradient finished");
        return cgs.getFx();
    }
}
