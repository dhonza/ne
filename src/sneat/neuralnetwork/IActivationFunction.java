package sneat.neuralnetwork;

public interface IActivationFunction {
    float calculate(float inputSignal);

    double calculate(double inputSignal);

    /// <summary>
    /// Unique ID. Stored in network XML to identify which function network the network is supposed to use.
    /// </summary>
    String getFunctionId();

    /// <summary>
    /// The function as a string in a platform agnostic form. For documentation purposes only, this isn;t actually compiled!
    /// </summary>
    String FunctionString();

    /// <summary>
    /// A human readable / verbose description of the activation function.
    /// </summary>
    String FunctionDescription();
}

