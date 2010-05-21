package sneat.neuralnetwork.fastconcurrentnetwork;

import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;
import sneat.neuralnetwork.activationfunctions.Modulus;

/// <summary>
/// A fast implementation of a network with concurrently activated neurons, that is, each

/// neuron's output signal is calculated for a given timestep using the output signals
/// from the previous timestep. This then simulates each neuron activating concurrently.
/// </summary>
public class FloatFastConcurrentNetwork implements INetwork {
    IActivationFunction[] activationFnArray;

    Modulus mod = (Modulus) ActivationFunctionFactory.getActivationFunction("Modulus");
    // Neurons are ordered with bias and input nodes at the head of the list, then output nodes and
    // hidden nodes on the array's tail.
    public float[] neuronSignalArray;
    float[] _neuronSignalArray;
    public FloatFastConnection[] connectionArray;

    /// <summary>
    /// The number of input neurons. Also the index 1 after the last input neuron.
    /// </summary>
    int inputNeuronCount;
    int totalInputNeuronCount;
    int outputNeuronCount;

    /// <summary>
    /// This is the index of the first hidden neuron in the array (inputNeuronCount + outputNeuronCount).
    /// </summary>
    int biasNeuronCount;

    public FloatFastConcurrentNetwork(int biasNeuronCount,
                                      int inputNeuronCount,
                                      int outputNeuronCount,
                                      int totalNeuronCount,
                                      FloatFastConnection[] connectionArray,
                                      IActivationFunction[] activationFnArray) {
        this.biasNeuronCount = biasNeuronCount;
        this.inputNeuronCount = inputNeuronCount;
        this.totalInputNeuronCount = biasNeuronCount + inputNeuronCount;
        this.outputNeuronCount = outputNeuronCount;

        this.connectionArray = connectionArray;
        this.activationFnArray = activationFnArray;

        //----- Allocate the arrays that make up the neural network.
        // The neuron signals are initialised to 0 by default. Only bias nodes need setting to 1.
        neuronSignalArray = new float[totalNeuronCount];
        _neuronSignalArray = new float[totalNeuronCount];

        for (int i = 0; i < biasNeuronCount; i++)
            neuronSignalArray[i] = 1.0F;
    }

    public void singleStep() {
        // Loop connections. Calculate each connection's output signal.
        for (int i = 0; i < connectionArray.length; i++)
            connectionArray[i].signal = neuronSignalArray[connectionArray[i].sourceNeuronIdx] * connectionArray[i].weight;

        // Loop the connections again. This time add the signals to the target neurons.
        // This will largely require out of order memory writes. This is the one loop where
        // this will happen.
        for (int i = 0; i < connectionArray.length; i++)
            _neuronSignalArray[connectionArray[i].targetNeuronIdx] += connectionArray[i].signal;

        // Now loop _neuronSignalArray, pass the signals through the activation function
        // and store the result back to neuronSignalArray. Skip over input neurons - these
        // neurons should be untouched.
        for (int i = totalInputNeuronCount; i < _neuronSignalArray.length; i++) {
            //TODO: DAVID STUFF
            neuronSignalArray[i] = activationFnArray[i].calculate(_neuronSignalArray[i]);
            //neuronSignalArray[i] = 1.0F+(_neuronSignalArray[i]/(0.1F+Math.Abs(_neuronSignalArray[i])));

            // Take the opportunity to reset the pre-activation signal array.
            _neuronSignalArray[i] = 0.0F;
        }
    }

    public void multipleSteps(int numberOfSteps) {
        //System.IO.StreamWriter write = new System.IO.StreamWriter("nodes20.txt");
        for (int i = 0; i < numberOfSteps; i++) {
            //foreach (float f in neuronSignalArray)
            //  write.Write(f + " ");
            //write.WriteLine();
            //write.WriteLine();
            singleStep();

        }
        //foreach (float f in neuronSignalArray)
        //    write.Write(f + " ");
        //write.WriteLine();
        //write.WriteLine();
        //write.WriteLine("**");
        //write.Close();
    }

    public void multipleStepsWithMod(int numberOfSteps, int factor) {
        //System.IO.StreamWriter write = new System.IO.StreamWriter("nodes1.txt");
        for (int i = 0; i < numberOfSteps; i++) {
            singleStep(factor);
            // foreach (float f in neuronSignalArray)
            //     write.Write(f + " ");
            //write.WriteLine();
        }
        //write.WriteLine("**");
    }

    public void singleStep(int factor) {
        // Loop connections. Calculate each connection's output signal.
        for (int i = 0; i < connectionArray.length; i++)
            connectionArray[i].signal = neuronSignalArray[connectionArray[i].sourceNeuronIdx] * connectionArray[i].weight;

        // Loop the connections again. This time add the signals to the target neurons.
        // This will largely require out of order memory writes. This is the one loop where
        // this will happen.
        for (int i = 0; i < connectionArray.length; i++)
            _neuronSignalArray[connectionArray[i].targetNeuronIdx] += connectionArray[i].signal;

        // Now loop _neuronSignalArray, pass the signals through the activation function
        // and store the result back to neuronSignalArray. Skip over input neurons - these
        // neurons should be untouched.
        for (int i = totalInputNeuronCount; i < _neuronSignalArray.length; i++) {
            //TODO: DAVID STUFF
            if (activationFnArray[i] == mod)
                neuronSignalArray[i] = mod.calculate(_neuronSignalArray[i], factor);
            else
                neuronSignalArray[i] = activationFnArray[i].calculate(_neuronSignalArray[i]);
            //neuronSignalArray[i] = 1.0F+(_neuronSignalArray[i]/(0.1F+Math.Abs(_neuronSignalArray[i])));

            // Take the opportunity to reset the pre-activation signal array.
            _neuronSignalArray[i] = 0.0F;
        }
    }

    /// <summary>
    /// Using RelaxNetwork erodes some of the perofrmance gain of FastConcurrentNetwork because of the slightly
    /// more complex implemementation of the third loop - whe compared to SingleStep().
    /// </summary>
    /// <param name="maxSteps"></param>
    /// <param name="maxAllowedSignalDelta"></param>
    /// <returns></returns>
    public boolean relaxNetwork(int maxSteps, double maxAllowedSignalDelta) {
        boolean isRelaxed = false;
        for (int j = 0; j < maxSteps && !isRelaxed; j++) {
            isRelaxed = true;    // Assume true.

            // Loop connections. Calculate each connection's output signal.
            for (int i = 0; i < connectionArray.length; i++)
                connectionArray[i].signal = neuronSignalArray[connectionArray[i].sourceNeuronIdx] * connectionArray[i].weight;

            // Loop the connections again. This time add the signals to the target neurons.
            // This will largely require out of order memory writes. This is the one loop where
            // this will happen.
            for (int i = 0; i < connectionArray.length; i++)
                _neuronSignalArray[connectionArray[i].targetNeuronIdx] += connectionArray[i].signal;

            // Now loop _neuronSignalArray, pass the signals through the activation function
            // and store the result back to neuronSignalArray. Skip over input neurons - these
            // neurons should be untouched.
            for (int i = totalInputNeuronCount; i < _neuronSignalArray.length; i++) {
                //TODO: DAVID STUFF
                float oldSignal = neuronSignalArray[i];
                neuronSignalArray[i] = activationFnArray[i].calculate(_neuronSignalArray[i]);
                //neuronSignalArray[i] = 1.0F+(_neuronSignalArray[i]/(0.1F+Math.Abs(_neuronSignalArray[i])));

                if (Math.abs(neuronSignalArray[i] - oldSignal) > maxAllowedSignalDelta)
                    isRelaxed = false;

                // Take the opportunity to reset the pre-activation signal array.
                _neuronSignalArray[i] = 0.0F;
            }
        }

        return isRelaxed;
    }

    public void setInputSignal(int index, double signalValue) {
        neuronSignalArray[biasNeuronCount + index] = (float) signalValue;
    }

    public void setInputSignals(double[] signalArray) {
        // For speed we don't bother with bounds checks.
        for (int i = 0; i < signalArray.length; i++)
            neuronSignalArray[i + biasNeuronCount] = (float) signalArray[i];
    }

    public void setInputSignal(int index, float signalValue) {
        neuronSignalArray[biasNeuronCount + index] = signalValue;
    }

    public void setInputSignals(float[] signalArray) {
        // For speed we don't bother with bounds checks.
        System.arraycopy(signalArray, 0, neuronSignalArray, biasNeuronCount, signalArray.length);
    }

    public float getOutputSignal(int index) {
        return neuronSignalArray[totalInputNeuronCount + index];
    }

    public void clearSignals() {
        // Clear signals for input, hidden and output nodes. Only the bias node is untouched.
        for (int i = biasNeuronCount; i < neuronSignalArray.length; i++)
            neuronSignalArray[i] = 0.0F;
    }

    public int getInputNeuronCount() {
        return inputNeuronCount;
    }

    public int getOutputNeuronCount() {
        return outputNeuronCount;
    }


    public int getTotalNeuronCount() {
        return neuronSignalArray.length;
    }
}
