package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

class Sine implements IActivationFunction {
    public double calculate(double inputSignal) {
        return Math.sin(2 * inputSignal);

    }

    public float calculate(float inputSignal) {
        return (float) Math.sin(2 * inputSignal);
    }

    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    public String FunctionString() {
        return "Sin(2*inputSignal)";
    }

    public String FunctionDescription() {
        return "Sin function with doubled period";
    }
}
