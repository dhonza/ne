package common.net.precompiled;

import common.net.INet;

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

    final private double weights[];

    private boolean activated = false;

    public PrecompiledFeedForwardNet(IPrecompiledFeedForwardStub stub, double[] weights) {
        this.stub = stub;
        this.weights = weights;
    }

    public int getNumInputs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumOutputs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumHidden() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumLinks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadInputs(double[] inputs) {
        bias = inputs[0];
        System.arraycopy(inputs, 1, this.inputs, 0, inputs.length - 1);
    }

    public void loadInputsNotBias(double[] inputs) {
        bias = 1.0;
        this.inputs = inputs;
    }

    public void activate() {
        if (activated) {
            throw new IllegalStateException("FF precompiled network activated more than once!");
        }
        outputs = stub.propagate(bias, inputs, weights);
    }

    public void reset() {
        if (activated) {
            activated = false;
        }
    }

    public void initSetBias() {
        //not needed for this implementation
    }

    public double[] getOutputValues() {
        if (!activated) {
            throw new IllegalStateException("FF precompiled network not activated");
        }
        return outputs;
    }
}
