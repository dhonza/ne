package sneat.experiments.skrimish;

import sneat.cppns.Substrate;
import sneat.neatgenome.ConnectionGene;
import sneat.neatgenome.ConnectionGeneList;
import sneat.neatgenome.NeatGenome;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.fastconcurrentnetwork.FloatFastConcurrentNetwork;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SkirmishSubstrate extends Substrate {
    /* This substrate configuration is a bit different than the ones normally used, as the different layers have
    * different numbers of nodes and I wanted them all to line up like this:
    * -0-0-0- Outputs
    * -00000- Hidden
    * -00000- Inputs
    * This way there is a clear correlation between the x values of the sensors and effectors.  To achieve this
    * efficiently, I had to do some hacky stuff, which will have to be altered for different substrates.  The
    * Substrate class has a much simpler and more generic way of querying the connections.
    */

    public static final boolean OUTPUT = true;

    public SkirmishSubstrate(int inputs, int outputs, int hidden, IActivationFunction function) {
        super(inputs, outputs, hidden, function);
    }

    @Override
    public NeatGenome generateGenome(INetwork network) {

        PrintStream sw = null;
        if (OUTPUT) {
            try {
                sw = new PrintStream(new BufferedOutputStream(new FileOutputStream("testfile.txt")));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        ConnectionGeneList connections = new ConnectionGeneList((inputCount * hiddenCount) + (hiddenCount * outputCount));
        float[] coordinates = new float[4];
        float output;
        int connectionCounter = 0;
        int iterations = 2 * (network.getTotalNeuronCount() - (network.getInputNeuronCount() + network.getOutputNeuronCount())) + 1;

        coordinates[0] = -1 + inputDelta / 2.0f;
        coordinates[1] = -1;
        coordinates[2] = -1 + hiddenDelta / 2.0f;
        coordinates[3] = 0;

        for (int source = 0; source < inputCount; source++, coordinates[0] += inputDelta) {
            coordinates[2] = -1 + hiddenDelta / 2.0f;
            for (int target = 0; target < hiddenCount; target++, coordinates[2] += hiddenDelta) {

                //Since there are an equal number of input and hidden nodes, we check these everytime
                network.clearSignals();
                network.setInputSignals(coordinates);
                network.multipleSteps(iterations);
                output = network.getOutputSignal(0);
                if (OUTPUT) {
                    //TODO X uz asi bylo vypnute
//                    for (double d : inputs)
//                        sw.print(d + " ");
                    sw.print(output);
                    sw.println();
                }
                if (Math.abs(output) > threshold) {
                    float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                    connections.add(new ConnectionGene(connectionCounter++, source, target + inputCount + outputCount, weight));
                }

                //Since every other hidden node has a corresponding output node, we check every other time
                if (target % 2 == 0) {
                    network.clearSignals();
                    coordinates[1] = 0;
                    coordinates[3] = 1;
                    network.setInputSignals(coordinates);
                    network.multipleSteps(iterations);
                    output = network.getOutputSignal(0);
                    if (OUTPUT) {
//                        for (double d : inputs)
//                            sw.print(d + " ");
                        sw.print(output);
                        sw.println();
                    }
                    if (Math.abs(output) > threshold) {
                        float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                        connections.add(new ConnectionGene(connectionCounter++, source + inputCount + outputCount, (target / 2) + inputCount, weight));
                    }
                    coordinates[1] = -1;
                    coordinates[3] = 0;

                }
            }
        }
        if (OUTPUT) {
            sw.close();
        }
        return new NeatGenome(0, neurons, connections, inputCount, outputCount);
    }

    public INetwork generateMultiNetwork(INetwork network, int numberOfAgents) {
        return generateMultiGenomeModulus(network, numberOfAgents).decode(activationFunction);
    }

    public NeatGenome generateMultiGenomeModulus(INetwork network, int numberOfAgents) {
        PrintStream sw = null;
        if (OUTPUT) {
            try {
                sw = new PrintStream(new BufferedOutputStream(new FileOutputStream("testfile.txt")));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        float[] coordinates = new float[4];
        float output;
        int connectionCounter = 0;

        int inputsPerAgent = inputCount / numberOfAgents;
        int hiddenPerAgent = hiddenCount / numberOfAgents;
        int outputsPerAgent = outputCount / numberOfAgents;

        ConnectionGeneList connections = new ConnectionGeneList((inputCount * hiddenCount) + (hiddenCount * outputCount));

        int iterations = 2 * (network.getTotalNeuronCount() - (network.getInputNeuronCount() + network.getOutputNeuronCount())) + 1;

        coordinates[0] = -1 + inputDelta / 2.0f;    //x1
        coordinates[1] = -1;                        //y1
        coordinates[2] = -1 + hiddenDelta / 2.0f;   //x2
        coordinates[3] = 0;                         //y2

        for (int agent = 0; agent < numberOfAgents; agent++) {
            coordinates[0] = -1 + (agent * inputsPerAgent * inputDelta) + inputDelta / 2.0f;
            for (int source = 0; source < inputsPerAgent; source++, coordinates[0] += inputDelta) {
                coordinates[2] = -1 + (agent * hiddenPerAgent * hiddenDelta) + hiddenDelta / 2.0f;
                for (int target = 0; target < hiddenPerAgent; target++, coordinates[2] += hiddenDelta) {

                    //Since there are an equal number of input and hidden nodes, we check these everytime
                    network.clearSignals();
                    network.setInputSignals(coordinates);
                    ((FloatFastConcurrentNetwork) network).multipleStepsWithMod(iterations, numberOfAgents);
                    output = network.getOutputSignal(0);
                    if (OUTPUT) {
//                        for (double d : inputs)
//                            sw.print(d + " ");
                        sw.print(output);
                        sw.println();
                    }
                    if (Math.abs(output) > threshold) {
                        float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                        connections.add(new ConnectionGene(connectionCounter++, (agent * inputsPerAgent) + source, (agent * hiddenPerAgent) + target + inputCount + outputCount, weight));
                    }

                    //Since every other hidden node has a corresponding output node, we check every other time
                    if (target % 2 == 0) {
                        network.clearSignals();
                        coordinates[1] = 0;
                        coordinates[3] = 1;
                        network.setInputSignals(coordinates);
                        ((FloatFastConcurrentNetwork) network).multipleStepsWithMod(iterations, numberOfAgents);
                        output = network.getOutputSignal(0);
                        if (OUTPUT) {
//                            for (double d : inputs)
//                                sw.print(d + " ");
                            sw.print(output);
                            sw.println();
                        }
                        if (Math.abs(output) > threshold) {
                            float weight = (float) (((Math.abs(output) - (threshold)) / (1 - threshold)) * weightRange * Math.signum(output));
                            connections.add(new ConnectionGene(connectionCounter++, (agent * hiddenPerAgent) + source + inputCount + outputCount, ((outputsPerAgent * agent) + ((target) / 2)) + inputCount, weight));
                        }
                        coordinates[1] = -1;
                        coordinates[3] = 0;

                    }
                }
            }
        }
        if (OUTPUT) {
            sw.close();
        }
        //Console.WriteLine(count);
        //Console.ReadLine();
        return new NeatGenome(0, neurons, connections, inputCount, outputCount);
    }

}
