package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

class BipolarSigmoid implements IActivationFunction {

    public double calculate(double inputSignal) {
        return (2.0 / (1.0 + Math.exp(-4.9 * inputSignal))) - 1.0;
    }

    public float calculate(float inputSignal) {
        return (2.0F / (1.0F + (float) Math.exp(-4.9F * inputSignal))) - 1.0F;
    }

    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    public String FunctionString() {
        return "2.0/(1.0 + exp(-4.9*inputSignal)) - 1.0";
    }

    public String FunctionDescription() {
        return "bipolar steepend sigmoid";
    }
}
