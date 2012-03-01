package divvis.numopt.test;

import common.MathUtil;
import common.function.ObjectiveFunction;
import divvis.numopt.LineSearchException;
import divvis.numopt.LineSearchStrongWolfe;

/**
 * User: drchaj1
 * Date: 24.3.2007
 * Time: 20:23:25
 */
public class LineSearchStrongWolfeExample {
    public static void main(String[] args) {
        ObjectiveFunction func = new TestFunction1b();
        LineSearchStrongWolfe lmds = new LineSearchStrongWolfe(func);
        double[] arg = {1.0};
        double f = func.evaluate(arg);
        double[] grad = new double[func.getNumArguments()];
        func.gradient(arg, grad);
        double[] dir = {-grad[0]};

        System.out.print("dir=");
        MathUtil.printlnVector(dir);
        double fAlpha = Double.NaN;
        try {
            fAlpha = lmds.minimize(arg, dir, f, grad);
        } catch (LineSearchException e) {
            e.printStackTrace();
        }
        System.out.print("xAlpha=");
        MathUtil.printlnVector(arg);
        System.out.println(" alpha=" + lmds.getAlpha());
        System.out.println("fAlpha=" + fAlpha);
    }
}
