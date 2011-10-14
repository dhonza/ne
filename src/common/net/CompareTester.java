package common.net;

import common.net.cascade.ActivationFunctionSigmoid;
import common.net.cascade.NeuralNetwork;
import common.net.precompiled.PrecompiledFeedForwardNet;
import common.net.precompiled.PrecompiledXORTestStub;
import org.apache.commons.lang.ArrayUtils;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 3, 2010
 * Time: 5:29:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompareTester {
    private final double[][] in = {{0.0, 0.0},
            {0.0, 1.0}, {1.0, 0.0}, {1.0, 1.0}};

    public final double[] weights = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};

    public CompareTester() {
    }

    public void eval(INet net) {
        net.initSetBias();
        double[] outputs = null;
        for (double[] input : in) {
            net.loadInputs(input);
            net.reset();
            for (int i = 0; i < 1; i++) {
                net.activate();
            }
            outputs = net.getOutputs();
            System.out.println(ArrayUtils.toString(input) + ":  " + outputs[0]);
        }
        System.out.println("----------");
    }

    public static void main(String[] args) {
        CompareTester t = new CompareTester();
        try {
            NeuralNetwork cnet = new NeuralNetwork(new int[]{2, 2, 1}, true, new ActivationFunctionSigmoid());
            cnet.setSynapseWeights(t.weights);
            t.eval(cnet);


            PrecompiledFeedForwardNet pnet = new PrecompiledFeedForwardNet(new PrecompiledXORTestStub(), t.weights.clone());
            t.eval(pnet);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
