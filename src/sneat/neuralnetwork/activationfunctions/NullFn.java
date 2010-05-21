package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

public class NullFn implements IActivationFunction {
    public double calculate(double inputSignal) {
        return 0.0;
    }

    public float calculate(float inputSignal) {
        return 0.0F;
    }

    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    public String FunctionString() {
        return "";
    }

    public String FunctionDescription() {
        return "";
    }
}
