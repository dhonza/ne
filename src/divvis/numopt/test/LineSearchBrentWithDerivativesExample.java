package divvis.numopt.test;

import common.MathUtil;
import common.function.ObjectiveFunction;
import divvis.numopt.LineSearchBrentWithDerivatives;
import divvis.numopt.LineSearchException;

/**
 * User: drchaj1
 * Date: 24.3.2007
 * Time: 20:23:25
 */
public class LineSearchBrentWithDerivativesExample {
    public static void main(String[] args) {
        ObjectiveFunction func = new TestFunction1b();
        LineSearchBrentWithDerivatives lsb = new LineSearchBrentWithDerivatives(func);
        double[] arg = {1.0};
        double f = func.evaluate(arg);
        double[] grad = new double[func.getNumArguments()];
        func.gradient(arg, grad);
        double[] dir = {-grad[0]};

        System.out.print("dir=");
        MathUtil.printlnVector(dir);
        System.out.print("x0=");
        MathUtil.printlnVector(arg);
        double fAlpha = Double.NaN;
        try {
            fAlpha = lsb.minimize(arg, dir, f);
        } catch (LineSearchException e) {
            e.printStackTrace();
        }

        System.out.print("xAlpha=");
        MathUtil.printlnVector(arg);
        System.out.println(" alpha=" + lsb.getAlpha());
        System.out.println("fAlpha=" + fAlpha);

//        System.out.println("returnCode=" + lsb.getReturnCode());
    }
}
