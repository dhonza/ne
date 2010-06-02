package sneat.experiments;

import sneat.neuralnetwork.INetwork;

import java.util.concurrent.Semaphore;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 10:39:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class XORNetworkEvaluator implements INetworkEvaluator {
//    private final float[][] in = {{1.0f, 0.0f, 0.0f}, // the first number for bias
//            {1.0f, 0.0f, 1.0f}, {1.0f, 1.0f, 0.0f}, {1.0f, 1.0f, 1.0f}};

    private final float[][] in = {{0.0f, 0.0f},
            {0.0f, 1.0f}, {1.0f, 0.0f}, {1.0f, 1.0f}};

    private final double[] out = {0.0, 1.0, 1.0, 0.0};

    public double evaluateNetwork(INetwork network) {
        float error = 0;
        for (int i = 0; i < 4; i++) {
            network.clearSignals();
            network.setInputSignals(in[i]);
            network.multipleSteps(5);
            error += Math.abs(out[i] - network.getOutputSignal(0));
        }
        return Math.pow((4.0 - error), 2);
    }

    public double threadSafeEvaluateNetwork(INetwork network, Semaphore sem) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public boolean isSolved() {
        return false;  //TODO implement
    }

    public String getEvaluatorStateMessage() {
        return "";
    }
}
