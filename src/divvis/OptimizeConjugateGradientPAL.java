package divvis;

import common.MachineAccuracy;
import pal.math.ConjugateGradientSearch;
import pal.math.MFWithGradient;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 22:08:17
 */
public class OptimizeConjugateGradientPAL implements OptimizeInterface, MFWithGradient {
    //public class OptimizeConjugateGradientPAL implements OptimizeInterface, MultivariateFunction {
    private int args;
    private double[][] D;

    private EquationsInterface equations;

    //statistics
    protected int numEvaluateCalls = 0;
    protected int numGradientCalls = 0;
    protected int numHessianCalls = 0;


    public OptimizeConjugateGradientPAL(double[][] oD, EquationsInterface oequations) {
        this.D = oD;
        this.args = oequations.getDimension() * oD.length;
        this.equations = oequations;
    }

    public OptimizeConjugateGradientPAL(double[][] oD, int oargs) {
        this(oD, EquationsFactory.createDefault());
    }

    public int getNumEvaluateCalls() {
        return numEvaluateCalls;
    }

    public int getNumGradientCalls() {
        return numGradientCalls;
    }

    public int getNumHessianCalls() {
        return numHessianCalls;
    }

    public double evaluate(double[] oxvec) {
        numEvaluateCalls++;
        return equations.evaluate(D, oxvec);
    }

    public double evaluate(double[] oxvec, double[] ograd) {
        computeGradient(oxvec, ograd);
        return evaluate(oxvec);
    }

    public void computeGradient(double[] oxvec, double[] ograd) {
        numGradientCalls++;
        equations.gradient(D, oxvec, ograd);
    }

    public int getNumArguments() {
        return args;
    }

    public double getLowerBound(int i) {
//        return Double.MIN_VALUE;
        return -15;
    }

    public double getUpperBound(int i) {
//        return Double.MAX_VALUE;
        return 15;
    }

    public double minimize(double[] op) {

        ConjugateGradientSearch cgs = new ConjugateGradientSearch(ConjugateGradientSearch.BEALE_SORENSON_HESTENES_STIEFEL_UPDATE);
        cgs.prin = 0;
//        cgs.optimize(this, op, 1E-3, 1E-3);
        cgs.optimize(this, op, MachineAccuracy.SQRT_EPSILON, MachineAccuracy.SQRT_EPSILON);
//        cgs.optimize(this, op, MachineAccuracy.SQRT_EPSILON*1000, MachineAccuracy.SQRT_EPSILON*1000);
        double fx = this.evaluate(op);
//        System.out.println("finished: " + evaluate(xvec));

        System.out.print("f(x)=" + fx);
        System.out.println(" evaluations f:" + getNumEvaluateCalls() + " d:" + getNumGradientCalls() + " H:" + getNumHessianCalls());
        System.out.println("Conjugate Gradient PAL finished");
        return fx;
    }
}
