package divvis.numopt.test;

import common.Bench;
import common.MathUtil;
import common.function.BasicObjectiveFunction;
import divvis.numopt.ConjugateGradientMinimization;
import divvis.numopt.ConjugateGradientMinimizationException;
import divvis.numopt.LineSearchException;
import divvis.numopt.events.IterationAdapter;
import divvis.numopt.events.IterationEvent;
import divvis.numopt.events.OptimizationEvent;
import divvis.numopt.events.OptimizationListener;

/**
 * User: honza
 * Date: 17.2.2007
 * Time: 22:02:36
 */
public class ConjugateGradientMinimizationExample {

    public static void main(String[] args) {
        final BasicObjectiveFunction function = new TestFunction2();
        ConjugateGradientMinimization ex = new ConjugateGradientMinimization(function);

        ex.addOptimizationListener(new OptimizationListener() {
            Bench bench = new Bench();

            public void OptimizationStart(OptimizationEvent oie) {
                bench.start();
            }

            public void OptimizationEnd(OptimizationEvent oie) {
                System.out.println("finished in " + bench.stop() + "ms");
                System.out.println(" evaluations f:" + function.getNumEvaluateCalls() + " d:" + function.getNumGradientCalls() + " H:" + function.getNumHessianCalls());
            }
        });

        ex.addIterationListener(new IterationAdapter() {
            Bench bench = new Bench();

            public void IterationStart(IterationEvent oie) {
                bench.start();
            }

            public void IterationEnd(IterationEvent oie) {
                ConjugateGradientMinimization cgm = (ConjugateGradientMinimization) oie.getSource();
                System.out.print("iteration " + cgm.getIteration() + ": f(x)=" + cgm.getFx() + "; x=[");
                for (int i = 0; i < cgm.getX().length - 1; i++) {
                    System.out.print(cgm.getX()[i] + ",");
                }
                System.out.println(cgm.getX()[cgm.getX().length - 1] + "]; time=" + bench.stop() + "ms");
            }
        });

        double[] initPs = {2.0, 3.0};

        try {
            ex.minimize(initPs);
            MathUtil.printlnVector(ex.getX());
        } catch (ConjugateGradientMinimizationException e) {
            e.printStackTrace();
        } catch (LineSearchException e) {
            e.printStackTrace();
        }
//        x=1/3, y=-1
//        NumericalDifferentiation.checkAnalyticGradient(function, new double[]{1.56, 1.1});
    }
}
