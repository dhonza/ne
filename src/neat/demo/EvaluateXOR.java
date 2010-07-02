package neat.demo;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.net.linked.Net;
import neat.Genome;

/**
 * User: honza
 * Date: May 19, 2006
 * Time: 8:52:38 AM
 */
public class EvaluateXOR implements Evaluable<Genome> {
    private final double[][] in = {{1.0, 0.0, 0.0}, // the first number for bias
            {1.0, 0.0, 1.0}, {1.0, 1.0, 0.0}, {1.0, 1.0, 1.0}};

    private final double[] out = {0.0, 1.0, 1.0, 0.0};


    public EvaluationInfo evaluate(Genome og) {
        Net n = og.getNet();

        og.setError(0.0);
        for (int i = 0; i < 4; i++) {
            n.loadInputs(in[i]);
            n.reset();
            activate(n);
            og.setError(og.getError() + Math.abs(out[i] - n.getOutputValues()[0]));
        }
        return new EvaluationInfo(Math.pow((4.0 - og.getError()), 2));
    }

    public boolean isSolved() {
        return false; //TODO implement check for solved problem
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }

    public void activate(Net on) {
        for (int i = 0; i < 5; i++)
            on.activate();
    }

}