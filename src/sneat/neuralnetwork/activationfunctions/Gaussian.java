package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

class Gaussian implements IActivationFunction {
    public double calculate(double inputSignal) {
        return 2 * Math.exp(-Math.pow(inputSignal * 2.5, 2)) - 1;
    }

    public float calculate(float inputSignal) {
        return (float) (2 * Math.exp(-Math.pow(inputSignal * 2.5, 2)) - 1);
    }

    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    public String FunctionString() {
        return "2*e^(-(input*2.5)^2) - 1";
    }

    public String FunctionDescription() {
        return "bimodal gaussian";
    }
}
