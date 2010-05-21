package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

public class SteepenedSigmoid implements IActivationFunction {
    public double calculate(double inputSignal) {
        /*if (inputSignal == 0)
       return 0;*/
        // good for x input range -1.0->1.0 (y 0.0->1.0)
        //if (inputSignal > -.25 && inputSignal < .25)
        //    return 0;
        return 1.0 / (1.0 + Math.exp(-4.9 * inputSignal));
    }

    public float calculate(float inputSignal) {
        /* if (inputSignal == 0)
       return 0;*/
        // good for x input range -1.0->1.0 (y 0.0->1.0)
        //if (inputSignal > -.25f && inputSignal < .25f)
        //    return 0;
        return 1.0F / (1.0F + (float) Math.exp(-4.9F * inputSignal));
    }

    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    public String FunctionString() {
        return "1.0/(1.0 + exp(-4.9*inputSignal))";
    }

    public String FunctionDescription() {
        return "Steepened sigmoid [xrange -1.0,1.0][yrange, 0.0,1.0]";
    }
}