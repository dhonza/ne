package sneat.neuralnetwork.concurrentnetwork;

import sneat.neuralnetwork.AbstractNetwork;

import java.util.List;

/// <summary>
/// A network that simulates a network in real-time. That is, each neuron in the network
/// calculates its accumulated input and output from the previous timestep's outputs.
/// Each neuron then switches to the new 'next timestep' state in unison.

///
/// This is opposed to an activation traversal network where the output signal updates
/// are updated by a traversal algorithm that follows the network's connections.
/// </summary>
public class ConcurrentNetwork extends AbstractNetwork {
    public ConcurrentNetwork(List<Neuron> neuronList) {
        super(neuronList);
    }

    @Override
    public void singleStep() {
        int loopBound = masterNeuronList.size();
        for (int j = 0; j < loopBound; j++)
            masterNeuronList.get(j).recalc();

        for (int j = 0; j < loopBound; j++)
            masterNeuronList.get(j).useRecalculatedValue();
    }

    @Override
    public void multipleSteps(int numberOfSteps) {
        for (int i = 0; i < numberOfSteps; i++)
            singleStep();
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="maxSteps">The number of timesteps to run the network before we give up.</param>
    /// <param name="maxAllowedSignalDelta"></param>
    /// <returns>False if the network did not relax. E.g. due to oscillating signals.</returns>
    @Override
    public boolean relaxNetwork(int maxSteps, double maxAllowedSignalDelta) {
        // Perform at least one step.
        singleStep();

        // Now perform steps until the network is relaxed or maxSteps is reached.
        int loopBound;
        boolean isRelaxed = false;
        for (int i = 0; i < maxSteps && !isRelaxed; i++) {
            isRelaxed = true;    // Assume true.

            // foreach syntax is 30% slower then this!
            loopBound = masterNeuronList.size();
            for (int j = 0; j < loopBound; j++) {
                Neuron neuron = masterNeuronList.get(j);
                neuron.recalc();

                // If this flag is set then keep testing neurons. Otherwise there is no need to
                // keep testing.
                if (isRelaxed) {
                    if (neuron.getNeuronType() == NeuronType.HIDDEN || neuron.getNeuronType() == NeuronType.OUTPUT) {
                        if (neuron.getOutputDelta() > maxAllowedSignalDelta)
                            isRelaxed = false;
                    }
                }
            }

            for (int j = 0; j < loopBound; j++)
                masterNeuronList.get(j).useRecalculatedValue();
        }

        return isRelaxed;
    }

    @Override
    public int getTotalNeuronCount() {
        return 0;
    }
}
