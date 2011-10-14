package neat.demo;

import common.RND;
import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import common.net.linked.Net;
import neat.Genome;

/**
 * User: honza
 * Date: May 19, 2006
 * Time: 8:52:38 AM
 */
public class EvaluateSpirals implements IEvaluable<Net> {
    public static double DENSITY = 1.0,
            MAX_RADIUS = 0.8,
            CENTER = 0.0;

    protected static double[][] in;
    protected static double[] out;
    protected static int size = 0;
    protected static int from = 0;

    static {
        DENSITY = 2;
        double angle, radius;
        size = 100;
        in = new double[2 * size][];
        for (int i = 0; i < size / 2; i++) {
            angle = 2.0 * (i * Math.PI) / (16 * DENSITY);
            double[] t = new double[3];
            t[0] = 1.0;
            t[1] = 0.9 * Math.cos(angle) + CENTER;
            t[2] = 0.9 * Math.sin(angle) + CENTER;
            in[i] = t;
        }
        for (int i = 0; i < size / 2; i++) {
            angle = 2.0 * (i * Math.PI) / (16 * DENSITY);
            double[] t = new double[3];
            t[0] = 1.0;
            t[1] = 0.7 * Math.cos(angle) + CENTER;
            t[2] = 0.7 * Math.sin(angle) + CENTER;
            in[size / 2 + i] = t;
        }
        for (int i = 0; i < size; i++) {
            angle = (i * Math.PI) / (16 * DENSITY);
            double[] t = new double[3];
            t[0] = 1.0;
            t[1] = 0.8 * Math.cos(angle) + CENTER;
            t[2] = 0.8 * Math.sin(angle) + CENTER;
            in[size + i] = t;
        }

    }

    /*
    static {
        double angle, radius;
//    size = 32*(int)Math.ceil(DENSITY) + 1;
        size = 96 * (int) Math.ceil(DENSITY) + 1;
        in = new double[2 * size][];
        for (int i = 0; i < size; i++) {
            angle = (i * Math.PI) / (16 * DENSITY);
            radius = MAX_RADIUS * ((104 * DENSITY) - i) / (104 * DENSITY);
            double[] t = new double[3];
            t[0] = 1.0;
            t[1] = radius * Math.cos(angle) + CENTER;
            t[2] = radius * Math.sin(angle) + CENTER;
            in[i] = t;
        }
        for (int i = 0; i < size; i++) {
            angle = (i * Math.PI) / (16 * DENSITY);
            radius = MAX_RADIUS * ((104 * DENSITY) - i) / (104 * DENSITY);
            double[] t = new double[3];
            t[0] = 1.0;
            t[1] = -radius * Math.cos(angle) + CENTER;
            t[2] = -radius * Math.sin(angle) + CENTER;
            in[size + i] = t;
        }
    }
      */

    public double[][] jitter(double[][] in) {
        double[][] tin = new double[in.length][];
        for (int i = 0; i < in.length; i++) {
            tin[i] = new double[3];
            tin[i][0] = 1.0;
            tin[i][1] = in[i][1] + RND.getDouble(-0.05, 0.05);
            tin[i][2] = in[i][2] + RND.getDouble(-0.05, 0.05);
        }
        return tin;
    }

    public EvaluationInfo evaluate(Net net) {
        double[] o;

        double error = 0.0;
        double[][] tin = jitter(in);
        double c1 = 0.0, c2 = 0.0;
        for (int i = from; i < size; i++) {
            net.loadInputsWithBias(tin[i]);
            net.reset();
            activate(net);
            o = net.getOutputs();
            if (o[0] < 0.0) {
                c1 += 1.0;
            }
        }
        for (int i = from; i < size; i++) {
            net.loadInputsWithBias(tin[i + size]);
            net.reset();
            activate(net);
            o = net.getOutputs();
            if (o[0] > 0.0) {
                c2 += 1.0;
            }
        }
        return new EvaluationInfo(c1 + c2);
    }

    public EvaluationInfo evaluateGeneralization(Net net) {
        return evaluate(net);
    }

    public void show(Net individual) {
    }

    public double evaluate2(Genome og) {
        Net n = og.getNet();
        double[] o;

        og.setError(0.0);
        double c1 = 0.0, c2 = 0.0;
        for (int i = from; i < size; i++) {
            n.loadInputsWithBias(in[i]);
            n.reset();
            activate(n);
            o = n.getOutputs();
            if (o[0] < o[1]) {
                c1 += 1.0;
            }
        }
        for (int i = from; i < size; i++) {
            n.loadInputsWithBias(in[i + size]);
            n.reset();
            activate(n);
            o = n.getOutputs();
            if (o[0] > o[1]) {
                c2 += 1.0;
            }
        }
        return c1 + c2;
    }

    public boolean isSolved() {
        return false; //TODO implement check for solved problem
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
//        return 2;
    }

    public void activate(Net on) {
        for (int i = 0; i < 5; i++)
            on.activate();
    }
}