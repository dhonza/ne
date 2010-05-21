package sneat.neatgenome;

import sneat.evolution.IGenome;
import sneat.evolution.IdGenerator;
import sneat.evolution.NeatParameters;
import sneat.evolution.Population;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;
import sneat.utilityclasses.Utilities;

import java.util.ArrayList;
import java.util.List;

public class GenomeFactory {
    /// <summary>
    /// Create a default minimal genome that describes a NN with the given number of inputs and outputs.
    /// </summary>
    /// <returns></returns>
    public static IGenome createGenome(NeatParameters neatParameters, IdGenerator idGenerator, int inputNeuronCount, int outputNeuronCount, float connectionProportion) {
        IActivationFunction actFunct;
        NeuronGene neuronGene; // temp variable.
        NeuronGeneList inputNeuronGeneList = new NeuronGeneList(); // includes bias neuron.
        NeuronGeneList outputNeuronGeneList = new NeuronGeneList();
        NeuronGeneList neuronGeneList = new NeuronGeneList();
        ConnectionGeneList connectionGeneList = new ConnectionGeneList();

        // IMPORTANT NOTE: The neurons must all be created prior to any connections. That way all of the genomes
        // will obtain the same innovation ID's for the bias,input and output nodes in the initial population.
        // Create a single bias neuron.
        //TODO: DAVID proper activation function change to NULL?
        actFunct = ActivationFunctionFactory.getActivationFunction("NullFn");
        neuronGene = new NeuronGene(idGenerator.getNextInnovationId(), NeuronType.BIAS, actFunct);
        inputNeuronGeneList.add(neuronGene);
        neuronGeneList.add(neuronGene);

        // Create input neuron genes.
        actFunct = ActivationFunctionFactory.getActivationFunction("NullFn");
        for (int i = 0; i < inputNeuronCount; i++) {
            //TODO: DAVID proper activation function change to NULL?
            neuronGene = new NeuronGene(idGenerator.getNextInnovationId(), NeuronType.INPUT, actFunct);
            inputNeuronGeneList.add(neuronGene);
            neuronGeneList.add(neuronGene);
        }

        // Create output neuron genes.
        //actFunct = ActivationFunctionFactory.getActivationFunction("NullFn");
        for (int i = 0; i < outputNeuronCount; i++) {
//            actFunct = ActivationFunctionFactory.getActivationFunction("BipolarSigmoid");
            actFunct = ActivationFunctionFactory.getActivationFunction("SteepenedSigmoid");
            //actFunct = ActivationFunctionFactory.getRandomActivationFunction(neatParameters);
            //TODO: DAVID proper activation function
            neuronGene = new NeuronGene(idGenerator.getNextInnovationId(), NeuronType.OUTPUT, actFunct);
            outputNeuronGeneList.add(neuronGene);
            neuronGeneList.add(neuronGene);
        }

        // Loop over all possible connections from input to output nodes and create a number of connections based upon
        // connectionProportion.
        for (NeuronGene targetNeuronGene : outputNeuronGeneList.getList()) {
            for (NeuronGene sourceNeuronGene : inputNeuronGeneList.getList()) {
                // Always generate an ID even if we aren't going to use it. This is necessary to ensure connections
                // between the same neurons always have the same ID throughout the generated population.
                int connectionInnovationId = idGenerator.getNextInnovationId();

                if (Utilities.nextDouble() < connectionProportion) {    // Ok lets create a connection.
                    connectionGeneList.add(new ConnectionGene(connectionInnovationId,
                            sourceNeuronGene.getInnovationId(),
                            targetNeuronGene.getInnovationId(),
                            (Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0));  // Weight 0 +-5
                }
            }
        }

        // Don't create any hidden nodes at this point. Fundamental to the NEAT way is to start minimally!
        return new NeatGenome(idGenerator.getNextGenomeId(), neuronGeneList, connectionGeneList, inputNeuronCount, outputNeuronCount);
    }

    /// <summary>
    /// Construct a GenomeList. This can be used to construct a new Population object.
    /// </summary>
    /// <param name="evolutionAlgorithm"></param>
    /// <param name="inputNeuronCount"></param>
    /// <param name="outputNeuronCount"></param>
    /// <param name="length"></param>
    /// <returns></returns>
    public static List<IGenome> CreateGenomeList(NeatParameters neatParameters, IdGenerator idGenerator, int inputNeuronCount, int outputNeuronCount, float connectionProportion, int length) {
        List<IGenome> genomeList = new ArrayList<IGenome>();

        for (int i = 0; i < length; i++) {
            idGenerator.resetNextInnovationNumber();
            genomeList.add(createGenome(neatParameters, idGenerator, inputNeuronCount, outputNeuronCount, connectionProportion));
        }

        return genomeList;
    }

    public static List<IGenome> CreateGenomeList(NeatGenome seedGenome, int length, NeatParameters neatParameters, IdGenerator idGenerator) {
        //Build the list.
        List<IGenome> genomeList = new ArrayList<IGenome>();

        // Use the seed directly just once.
        NeatGenome newGenome = new NeatGenome(seedGenome, idGenerator.getNextGenomeId());
        genomeList.add(newGenome);

        // For the remainder we alter the weights.
        for (int i = 1; i < length; i++) {
            newGenome = new NeatGenome(seedGenome, idGenerator.getNextGenomeId());

            // Reset the connection weights
            for (ConnectionGene connectionGene : newGenome.getConnectionGeneList().getList())
                connectionGene.setWeight((Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0);
            //newGenome.ConnectionGeneList.Add(new ConnectionGene(idGenerator.NextInnovationId,5,newGenome.NeuronGeneList[Utilities.next(newGenome.NeuronGeneList.Count-7)+7].InnovationId ,(Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange/2.0));
            //newGenome.ConnectionGeneList.Add(new ConnectionGene(idGenerator.NextInnovationId, 6, newGenome.NeuronGeneList[Utilities.next(newGenome.NeuronGeneList.Count - 7) + 7].InnovationId, (Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0));
            genomeList.add(newGenome);
        }

        //

        return genomeList;
    }

    public static List<IGenome> CreateGenomeListAddedInputs(NeatGenome seedGenome, int length, NeatParameters neatParameters, IdGenerator idGenerator) {
        //Build the list.
        List<IGenome> genomeList = new ArrayList<IGenome>();

        // Use the seed directly just once.
        NeatGenome newGenome = new NeatGenome(seedGenome, idGenerator.getNextGenomeId());
        //genomeList.Add(newGenome);

        // For the remainder we alter the weights.
        for (int i = 0; i < length; i++) {
            newGenome = new NeatGenome(seedGenome, idGenerator.getNextGenomeId());

            // Reset the connection weights
            for (ConnectionGene connectionGene : newGenome.getConnectionGeneList().getList())
                connectionGene.setWeight((Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0);
            newGenome.getConnectionGeneList().add(new ConnectionGene(idGenerator.getNextInnovationId(), 5, newGenome.getNeuronGeneList().get(Utilities.next(newGenome.getNeuronGeneList().size() - 7) + 7).getInnovationId(), (Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0));
            newGenome.getConnectionGeneList().add(new ConnectionGene(idGenerator.getNextInnovationId(), 6, newGenome.getNeuronGeneList().get(Utilities.next(newGenome.getNeuronGeneList().size() - 7) + 7).getInnovationId(), (Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0));
            genomeList.add(newGenome);
        }

        //

        return genomeList;
    }


    public static List<IGenome> CreateGenomeList(Population seedPopulation, int length, NeatParameters neatParameters, IdGenerator idGenerator) {
        //Build the list.
        List<IGenome> genomeList = new ArrayList<IGenome>();
        int seedIdx = 0;

        for (int i = 0; i < length; i++) {
            NeatGenome newGenome = new NeatGenome((NeatGenome) seedPopulation.getGenomeList().get(seedIdx), idGenerator.getNextGenomeId());

            // Reset the connection weights
            for (ConnectionGene connectionGene : newGenome.getConnectionGeneList().getList())
                connectionGene.setWeight((Utilities.nextDouble() * neatParameters.connectionWeightRange) - neatParameters.connectionWeightRange / 2.0);

            genomeList.add(newGenome);

            if (++seedIdx >= seedPopulation.getGenomeList().size()) {    // Back to first genome.
                seedIdx = 0;
            }
        }
        return genomeList;
    }
}