package sneat.experiments;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 10:39:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class XORNetworkEvaluator implements Evaluable<INetwork> {
//    private final float[][] in = {{1.0f, 0.0f, 0.0f}, // the first number for bias
//            {1.0f, 0.0f, 1.0f}, {1.0f, 1.0f, 0.0f}, {1.0f, 1.0f, 1.0f}};

    private final float[][] in = {{0.0f, 0.0f},
            {0.0f, 1.0f}, {1.0f, 0.0f}, {1.0f, 1.0f}};

    private final double[] out = {0.0, 1.0, 1.0, 0.0};

    public EvaluationInfo evaluate(INetwork network) {
        float error = 0;
        for (int i = 0; i < 4; i++) {
            network.clearSignals();
            network.setInputSignals(in[i]);
            network.multipleSteps(5);
            error += Math.abs(out[i] - network.getOutputSignal(0));
        }
        return new EvaluationInfo(Math.pow((4.0 - error), 2));
    }

    public boolean isSolved() {
        return false;  //TODO implement
    }

    public int getNumberOfInputs() {
        return 2;
    }

    public int getNumberOfOutputs() {
        return 1;
    }
}
