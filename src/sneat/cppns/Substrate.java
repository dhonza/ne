package sneat.cppns;

import sneat.experiments.HyperNEATParameters;
import sneat.neatgenome.*;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;

public class Substrate {
    public int inputCount;
    public int outputCount;
    public int hiddenCount;

    public float inputDelta;
    public float hiddenDelta;
    public float outputDelta;

    public double threshold;
    public double weightRange;
    public IActivationFunction activationFunction;
    public NeuronGeneList neurons;

    public Substrate() {
    }

    public Substrate(int input, int output, int hidden, IActivationFunction function) {
        weightRange = HyperNEATParameters.weightRange;
        threshold = HyperNEATParameters.threshold;

        inputCount = input;
        outputCount = output;
        hiddenCount = hidden;
        activationFunction = function;

        inputDelta = 2.0f / (inputCount);
        if (hiddenCount != 0)
            hiddenDelta = 2.0f / (hiddenCount);
        else
            hiddenDelta = 0;
        outputDelta = 2.0f / (outputCount);

        //SharpNEAT requires that the neuronlist be input|bias|output|hidden
        neurons = new NeuronGeneList(inputCount + outputCount + hiddenCount);
        //setup the inputs
        for (int a = 0; a < inputCount; a++) {
            neurons.add(new NeuronGene(a, NeuronType.INPUT, activationFunction));
        }

        //setup the outputs
        for (int a = 0; a < outputCount; a++) {
            neurons.add(new NeuronGene(a + inputCount, NeuronType.OUTPUT, activationFunction));
        }
        for (int a = 0; a < hiddenCount; a++) {
            neurons.add(new NeuronGene(a + inputCount + outputCount, NeuronType.HIDDEN, activationFunction));
        }


    }

    public INetwork generateNetwork(INetwork CPPN) {
        return generateGenome(CPPN).decode(null);
    }

    public NeatGenome generateGenome(INetwork network) {
        float[] coordinates = new float[4];
        float output;
        int connectionCounter = 0;
        int iterations = 2 * (network.getTotalNeuronCount() - (network.getInputNeuronCount() + network.getOutputNeuronCount())) + 1;
        ConnectionGeneList connections = new ConnectionGeneList();
        if (hiddenCount > 0) {
            coordinates[0] = -1 + inputDelta / 2.0f;
            coordinates[1] = -1;
            coordinates[2] = -1 + hiddenDelta / 2.0f;
            coordinates[3] = 0;
            for (int input = 0; input < inputCount; input++, coordinates[0] += inputDelta) {
                coordinates[2] = -1 + hiddenDelta / 2.0f;
                for (int hidden = 0; hidden < hiddenCount; hidden++, coordinates[2] += hiddenDelta) {
                    network.clearSignals();
                    network.setInputSignals(coordinates);
                    network.multipleSteps(iterations);
                    output = network.getOutputSignal(0);

                    if (Math.abs(output) > threshold) {
                        float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                        connections.add(new ConnectionGene(connectionCounter++, input, hidden + inputCount + outputCount, weight));
                    }
                }
            }
            coordinates[0] = -1 + hiddenDelta / 2.0f;
            coordinates[1] = 0;
            coordinates[2] = -1 + outputDelta / 2.0f;
            coordinates[3] = 1;
            for (int hidden = 0; hidden < hiddenCount; hidden++, coordinates[0] += hiddenDelta) {
                coordinates[2] = -1 + outputDelta / 2.0f;
                for (int outputs = 0; outputs < outputCount; outputs++, coordinates[2] += outputDelta) {
                    network.clearSignals();
                    network.setInputSignals(coordinates);
                    network.multipleSteps(iterations);
                    output = network.getOutputSignal(0);

                    if (Math.abs(output) > threshold) {
                        float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                        connections.add(new ConnectionGene(connectionCounter++, hidden + inputCount + outputCount, outputs + inputCount, weight));
                    }
                }
            }
        } else {
            coordinates[0] = -1 + inputDelta / 2.0f;
            coordinates[1] = -1;
            coordinates[2] = -1 + outputDelta / 2.0f;
            coordinates[3] = 1;
            for (int input = 0; input < inputCount; input++, coordinates[0] += inputDelta) {
                coordinates[2] = -1 + outputDelta / 2.0f;
                for (int outputs = 0; outputs < outputCount; output++, coordinates[2] += outputDelta) {
                    network.clearSignals();
                    network.setInputSignals(coordinates);
                    network.multipleSteps(iterations);
                    output = network.getOutputSignal(0);

                    if (Math.abs(output) > threshold) {
                        float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                        connections.add(new ConnectionGene(connectionCounter++, input, outputs + inputCount, weight));
                    }
                }
            }
        }
        return new NeatGenome(0, neurons, connections, inputCount, outputCount);
    }

}

