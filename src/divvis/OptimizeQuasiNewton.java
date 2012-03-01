package divvis;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 22:08:17
 */
public class OptimizeQuasiNewton implements OptimizeInterface, Uncmin_methods {
    //public class OptimizeConjugateGradientPAL implements MultivariateFunction {

    private int args;
    private double[][] D;

    private EquationsInterface equations;

    //statistics
    protected int numEvaluateCalls = 0;
    protected int numGradientCalls = 0;
    protected int numHessianCalls = 0;

    private double[] xvec;
    double[] grad;

    public OptimizeQuasiNewton(double[][] oD, EquationsInterface oequations) {
        this.D = oD;
        this.args = oequations.getDimension() * oD.length;
        this.equations = oequations;
    }

    public OptimizeQuasiNewton(double[][] oD, int oargs) {
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

    public double f_to_minimize(double oxvec[]) {
        numEvaluateCalls++;
        // correct indexes (starting from zero)
        System.arraycopy(oxvec, 1, xvec, 0, args);
        return equations.evaluate(D, xvec);
    }

    public void gradient(double[] oxvec, double[] ograd) {
        numGradientCalls++;
        // correct indexes (starting from zero)
        System.arraycopy(oxvec, 1, xvec, 0, args);
        System.arraycopy(ograd, 1, grad, 0, args);
        equations.gradient(D, xvec, grad);
        System.arraycopy(grad, 0, ograd, 1, args);
    }

    public void hessian(double x[], double h[][]) {
        System.out.println("QNOptimize: analytic hessian not implemented!");
        System.exit(1);
    }

    public double minimize(double[] op) {
        int argsP = args + 1;
        double[] ap = new double[argsP];
        System.arraycopy(op, 0, ap, 1, args);

        xvec = new double[args];
        grad = new double[args];

        double[] besta = new double[argsP];
        double[] f = new double[argsP];
        double[] g = new double[argsP];
        double[][] aa = new double[argsP][argsP];
        double[] udiag = new double[argsP];
        int[] info = new int[argsP];

        int[] msg = new int[2];
        double[] typsiz = new double[argsP];
        double[] dlt = new double[2];
        double[] fscale = new double[2];
        double[] stepmx = new double[2];
        int[] ndigit = new int[2];
        int[] method = new int[2];
        int[] iexp = new int[2];
        int[] itnlim = new int[2];
        int[] iagflg = new int[2];
        int[] iahflg = new int[2];
        double[] gradtl = new double[2];
        double[] steptl = new double[2];
        double epsm;

// SET TYPICAL SIZE OF X AND MINIMIZATION FUNCTION
        for (int i = 1; i < args; i++) {
            typsiz[i] = 1.0;
        }
        fscale[1] = 1.0;
// SET TOLERANCES
//            dlt[1] = -1.0;
        dlt[1] = 25;
        epsm = 1.12e-16;
//            epsm = 1.12e-282;
//        epsm = 1.4901161193847656E-8;//last
        gradtl[1] = Math.pow(epsm, 1.0 / 3.0);
        steptl[1] = Math.sqrt(epsm);
        stepmx[1] = 0.0;
// SET FLAGS
        method[1] = 3; // 1=line search, 2=double dogleg, 3=More-Hebdon
        iexp[1] = 1;
        msg[1] = 0;
        ndigit[1] = -25;
        itnlim[1] = 18600; //iteration limit
//            iagflg[0] = 0; //disable gradientCD - nothing changed
        iagflg[1] = 1; //gradientCD supplied, enable it
        iahflg[1] = 0; //hessian matrix NOT supplied

        Uncmin_f77.optif9_f77(args, ap, this, typsiz, fscale, method,
                iexp, msg, ndigit, itnlim, iagflg, iahflg, dlt,
                gradtl, stepmx, steptl, besta, f, g,
                info, aa, udiag);

        System.arraycopy(besta, 1, op, 0, args);

        System.out.print("f(x)=" + f[1]);
        System.out.println(" evaluations f:" + getNumEvaluateCalls() + " d:" + getNumGradientCalls() + " H:" + getNumHessianCalls());
        System.out.println("finished");
        return f[1];
    }
}
