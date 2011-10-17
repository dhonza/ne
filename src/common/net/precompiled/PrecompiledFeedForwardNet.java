package common.net.precompiled;

import common.net.INet;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 11:39:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrecompiledFeedForwardNet implements INet {
    final private IPrecompiledFeedForwardStub stub;

    private double bias = 1.0;
    private double[] inputs;
    private double[] outputs;

    final double weights[];

    private boolean activated = false;

    public PrecompiledFeedForwardNet(IPrecompiledFeedForwardStub stub, double[] weights) {
        this.stub = stub;
        this.weights = weights;
    }

    public int getNumInputs() {
        return stub.getNumberOfInputs();
    }

    public int getNumOutputs() {
        throw new IllegalStateException("NOT YET IMPLEMENTED!");
    }

    public int getNumHidden() {
        throw new IllegalStateException("NOT YET IMPLEMENTED!");
    }

    public int getNumLinks() {
        return weights.length;
    }

    public void loadInputsWithBias(double[] inputs) {
        bias = inputs[0];
        System.arraycopy(inputs, 1, this.inputs, 0, inputs.length - 1);
    }

    public void loadInputs(double[] inputs) {
        bias = 1.0;
        this.inputs = inputs;
    }

    public void propagate() {
        activate();
    }

    public void activate() {
        if (activated) {
//            throw new IllegalStateException("FF precompiled network activated more than once!");
            return;
        }
        outputs = stub.propagate(bias, inputs, weights);
        activated = true;
    }

    public void reset() {
        if (activated) {
            activated = false;
        }
    }

    public void initSetBias() {
        //not needed for this implementation
    }

    public double[] getOutputs() {
        if (!activated) {
            throw new IllegalStateException("FF precompiled network not activated");
        }
        return outputs;
    }

    @Override
    public String toString() {
        return "PrecompiledFeedForwardNet{" +
                "weights=" + Arrays.asList(ArrayUtils.toObject(weights)) +
                '}';
    }
}
