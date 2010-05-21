package sneat.neuralnetwork.concurrentnetwork;

import sneat.neuralnetwork.IActivationFunction;

import java.util.ArrayList;
import java.util.List;


public class Neuron {
    NeuronType neuronType;
    int id;

    double outputValue;    // Output signal. Can be initialised when neuron is created.
    double outputRecalc;    // The recalculated output is not updated immediately. A complete pass of the network is
    // done using the existing output values, and then we switch the network over to the the
    // recalced values in a second pass. This way we simulate the workings of a parallel network.

    List<Connection> connectionList;                    // All of the incoming connections to a neuron. The neuron can recalculate it's own output value by iterating throgh this collection.

    IActivationFunction activationFn;

    public Neuron(IActivationFunction activationFn, NeuronType neuronType, int id) {
        this.activationFn = activationFn;
        this.neuronType = neuronType;
        this.id = id;
        connectionList = new ArrayList<Connection>();

        if (neuronType == NeuronType.BIAS)
            this.outputValue = 1.0D;
        else
            this.outputValue = 0.0D;
    }

    public NeuronType getNeuronType() {
        return neuronType;
    }

    public int getId() {
        return id;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(double value) {    // Set is required for input nodes.
        outputValue = value;
    }

    /// <summary>
    /// The OutputValue delta between this timestep and the previous. This property is only valid
    /// after calleing Recalc() and before calling UseRecalculatedValue().
    /// </summary>
    public double getOutputDelta() {
        return Math.abs(outputValue - outputRecalc);
    }

    public List<Connection> getConnectionList() {
        return connectionList;
    }

    /// <summary>
    /// Recalculate this neuron's output value.
    /// </summary>
    public void recalc() {
        // No recalculation required for input or bias nodes.
        if (neuronType == NeuronType.INPUT || neuronType == NeuronType.BIAS)
            return;

        // Iterate the connections and total up the input signal from all of them.
        double accumulator = 0;
        int loopBound = connectionList.size();
        for (int i = 0; i < loopBound; i++) {
            Connection connection = connectionList.get(i);
            accumulator += connection.getSourceNeuron().outputValue * connection.getWeight();
        }

        // Apply the activation function to the accumulated input signal.
        // A parameterised sigmoid from PEANNUT.
        //			outputRecalc = activationParams.a +
        //							activationParams.b *
        //							Math.Tanh(activationParams.c * accumulator - activationParams.d);

        // Functions from original NEAT code

        //RIGHT SHIFTED ---------------------------------------------------------
        //return (1/(1+(exp(-(slope*activesum-constant))))); //ave 3213 clean on 40 runs of p2m and 3468 on another 40
        //41394 with 1 failure on 8 runs

        //LEFT SHIFTED ----------------------------------------------------------
        //return (1/(1+(exp(-(slope*activesum+constant))))); //original setting ave 3423 on 40 runs of p2m, 3729 and 1 failure also

        //PLAIN SIGMOID ---------------------------------------------------------
        //outputRecalc = (1.0D/(1.0D+(Math.Exp(-accumulator))));  // good for x range of 0+-5

        //DOUBLE POLE EXPERIMENT. Lazy/sloping sigmoid from -1 to 1 with output range -1 to 1.
        //outputRecalc = 1.0/(1.0 + Math.Exp(-4.9*accumulator));

        outputRecalc = activationFn.calculate(accumulator);

//			System.Diagnostics.Debug.WriteLine("out=" + outputRecalc);
        // range
//			outputRecalc = 1.0      *  Math.Tanh(0.2 * accumulator);

        //LEFT SHIFTED NON-STEEPENED---------------------------------------------
        //return (1/(1+(exp(-activesum-constant)))); //simple left shifted

        //NON-SHIFTED STEEPENED
        //return (1/(1+(exp(-(slope*activesum))))); //Compressed

    }

    public void useRecalculatedValue() {
        // No recalculation required for input or bias nodes.
        if (neuronType == NeuronType.INPUT || neuronType == NeuronType.BIAS)
            return;

        outputValue = outputRecalc;
    }
}
