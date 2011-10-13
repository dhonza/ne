package neat.demo;

import common.evolution.EvaluationInfo;
import common.evolution.IEvaluable;
import common.net.linked.Net;

/**
 * User: honza
 * Date: May 19, 2006
 * Time: 8:52:38 AM
 */
public class EvaluateXOR implements IEvaluable<Net> {
    private final double[][] in = {{1.0, 0.0, 0.0}, // the first number for bias
            {1.0, 0.0, 1.0}, {1.0, 1.0, 0.0}, {1.0, 1.0, 1.0}};

    private final double[] out = {0.0, 1.0, 1.0, 0.0};


    public EvaluationInfo evaluate(Net n) {
        double error = 0.0;
        for (int i = 0; i < 4; i++) {
            n.loadInputs(in[i]);
            n.reset();
            activate(n);
            error += Math.abs(out[i] - n.getOutputs()[0]);
        }
        return new EvaluationInfo(Math.pow((4.0 - error), 2));
    }

    public EvaluationInfo evaluateGeneralization(Net individual) {
        return evaluate(individual);
    }

    public void show(Net individual) {
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