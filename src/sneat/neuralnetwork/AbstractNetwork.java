package sneat.neuralnetwork;

import sneat.neuralnetwork.concurrentnetwork.Neuron;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// A base class for neural networks. This class provides the underlying data structures

/// for neurons and connections but not a technique for 'executing' the network.
/// </summary>
public abstract class AbstractNetwork implements INetwork {
    // The master list of ALL neurons within the network.
    protected List<Neuron> masterNeuronList;

    // There follows a number of Lists that hold various neuron subsets. Perhaps not
    // a particularly efficient way of doing things, but at least clear!

    // All input neurons. *Not* including single bias neuron. Used by SetInputSignal().
    List<Neuron> inputNeuronList;

    // All output neurons. Used by GetOutputSignal().
    List<Neuron> outputNeuronList;

    public AbstractNetwork(List<Neuron> neuronList) {
        inputNeuronList = new ArrayList<Neuron>();
        outputNeuronList = new ArrayList<Neuron>();
        loadNeuronList(neuronList);
    }

    public int getInputNeuronCount() {
        return inputNeuronList.size();
    }

    public int getOutputNeuronCount()

    {
        return outputNeuronList.size();
    }

    abstract public int getTotalNeuronCount();

    public List<Neuron> getMasterNeuronList() {
        return masterNeuronList;
    }

    public void setInputSignal(int index, double signalValue) {
        inputNeuronList.get(index).setOutputValue(signalValue);
    }

    public void setInputSignals(float[] signalArray) {
        // For speed we don't bother with bounds checks.
        for (int i = 0; i < signalArray.length; i++)
            inputNeuronList.get(i).setOutputValue(signalArray[i]);
    }

    public float getOutputSignal(int index) {
        return (float) outputNeuronList.get(index).getOutputValue();
    }

    public void clearSignals() {
        int loopBound = masterNeuronList.size();
        for (int j = 0; j < loopBound; j++) {
            Neuron neuron = masterNeuronList.get(j);
            if (neuron.getNeuronType() != NeuronType.BIAS)
                neuron.setOutputValue(0);
        }
    }

    abstract public void singleStep();

    abstract public void multipleSteps(int numberOfSteps);

    /// <summary>
    ///
    /// </summary>
    /// <param name="maxSteps">The number of timesteps to run the network before we give up.</param>
    /// <param name="maxAllowedSignalDelta"></param>
    /// <returns>False if the network did not relax. E.g. due to oscillating signals.</returns>
    abstract public boolean relaxNetwork(int maxSteps, double maxAllowedSignalDelta);

    /// <summary>
    /// Accepts a list of interconnected neurons that describe the network and loads them into this class instance
    /// so that the network can be run. This primarily means placing input and output nodes into their own Lists
    /// for use during the run.
    /// </summary>
    /// <param name="neuronList"></param>
    private void loadNeuronList(List<Neuron> neuronList) {
        masterNeuronList = neuronList;

        int loopBound = masterNeuronList.size();
        for (int j = 0; j < loopBound; j++) {
            Neuron neuron = masterNeuronList.get(j);

            switch (neuron.getNeuronType()) {
                case INPUT:
                    inputNeuronList.add(neuron);
                    break;
                case OUTPUT:
                    outputNeuronList.add(neuron);
                    break;
            }
        }
    }

}
