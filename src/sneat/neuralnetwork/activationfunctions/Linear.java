package sneat.neuralnetwork.activationfunctions;

import sneat.neuralnetwork.IActivationFunction;

public class Linear implements IActivationFunction {
    public double calculate(double inputSignal) {
        /* if(inputSignal<-1.0)
     return 0.0;
 else if(inputSignal>1.0)
     return 1.0;
 else
     return (inputSignal+1.0)*0.5;  */
        return Math.abs(inputSignal);
    }

    public float calculate(float inputSignal) {
        /* if(inputSignal<0.0F)
      return 0.0F;
  else if(inputSignal>1.0F)
      return 1.0F;
  else
      return (inputSignal+1.0F)*0.5F; */
        return Math.abs(inputSignal);
    }

    /// <summary>
    /// Unique ID. Stored in network XML to identify which function network the network is supposed to use.
    /// </summary>
    public String getFunctionId() {
        return this.getClass().getSimpleName();
    }

    /// <summary>
    /// The function as a string in a platform agnostic form. For documentation purposes only, this isn;t actually compiled!
    /// </summary>
    public String FunctionString() {
        return "(x+1)/2 [min=0, max=1]";
    }

    /// <summary>
    /// A human readable / verbose description of the activation function.
    /// </summary>
    public String FunctionDescription() {
        return "Linear";
    }
}
