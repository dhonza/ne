package neat.demo;

import neat.Evaluable;
import neat.Net;
import neat.Genome;

/**
 * User: honza
 * Date: May 19, 2006
 * Time: 8:52:38 AM
 */
public class EvaluateXOR implements Evaluable {
    private final double[][] in = {{1.0, 0.0, 0.0}, // the first number for bias
            {1.0, 0.0, 1.0}, {1.0, 1.0, 0.0}, {1.0, 1.0, 1.0}};

    private final double[] out = {0.0, 1.0, 1.0, 0.0};


    public double evaluate(Genome og) {
        Net n = og.getNet();

        og.setError(0.0);
        for (int i = 0; i < 4; i++) {
            n.loadInputs(in[i]);
            n.reset();
            activate(n);
            og.setError(og.getError() + Math.abs(out[i] - n.getOutputValues()[0]));
        }
        return Math.pow((4.0 - og.getError()), 2);
    }

    public void evaluateAll(Genome[] opop, double[] ofitnessValues) {
        for (int i = 0; i < opop.length; i++) {
            Genome tg = opop[i];
            Net n = tg.getNet();

            tg.setError(0.0);
            for (int j = 0; j < 4; j++) {
                n.loadInputs(in[j]);
                n.reset();
                activate(n);
                tg.setError(tg.getError() + Math.abs(out[j] - n.getOutputValues()[0]));
            }
            ofitnessValues[i] = Math.pow((4.0 - tg.getError()), 2);
        }
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public void storeEvaluation(Genome og) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double[][][] getStoredInputs() {
        return new double[0][][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double[][][] getStoredOutputs() {
        return new double[0][][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean[] getNetResets() {
        return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void activate(Net on) {
        for (int i = 0; i < 5; i++)
            on.activate();
    }

}