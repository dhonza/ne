package sneat;

import sneat.neatgenome.ConnectionGene;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.NeuronGene;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.concurrentnetwork.ConcurrentNetwork;
import sneat.neuralnetwork.concurrentnetwork.Connection;
import sneat.neuralnetwork.concurrentnetwork.Neuron;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;
import sneat.neuralnetwork.fastconcurrentnetwork.FloatFastConcurrentNetwork;
import sneat.neuralnetwork.fastconcurrentnetwork.FloatFastConnection;
import sneat.utilityclasses.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// <summary>
/// By placing decode routines in a separate class we decouple the Genome and Network classes.
/// Ideally this would be achieved by using intermediate generic data structures, however that

/// approach can cause a performance hit. This is a nice balance that allows decoupling without
/// performance loss. The downside is that we need knowledge of the Network code's 'guts' in order
/// to construct them.
/// </summary>
public class GenomeDecoder {

    static public INetwork decodeToConcurrentNetwork(NeatGenome g, IActivationFunction activationFn) {
        //----- Loop the neuronGenes. Create Neuron for each one.
        // Store a table of neurons keyed by their id.
        HashMap<Integer, Neuron> neuronTable = new HashMap<Integer, Neuron>(g.getNeuronGeneList().getList().size());
        List<Neuron> neuronList = new ArrayList<Neuron>();

        for (NeuronGene neuronGene : g.getNeuronGeneList().getList()) {
            Neuron newNeuron = new Neuron(activationFn, neuronGene.getNeuronType(), neuronGene.getInnovationId());
            neuronTable.put(newNeuron.getId(), newNeuron);
            neuronList.add(newNeuron);
        }

        //----- Loop the connection genes. Create a Connection for each one and bind them to the relevant Neurons.
        for (ConnectionGene connectionGene : g.getConnectionGeneList().getList()) {
            Connection newConnection = new Connection(connectionGene.getSourceNeuronId(), connectionGene.getTargetNeuronId(), connectionGene.getWeight());

            // Bind the connection to it's source neuron.
            newConnection.setSourceNeuron(neuronTable.get(connectionGene.getSourceNeuronId()));

            // Store the new connection against it's target neuron.
            neuronTable.get(connectionGene.getTargetNeuronId()).getConnectionList().add(newConnection);
        }

        return new ConcurrentNetwork(neuronList);
    }


    /// <summary>
    /// Create a single comparer to limit the need to reconstruct for each network.
    /// Not multithread safe!
    /// </summary>
    //static FastConnectionComparer fastConnectionComparer = new FastConnectionComparer();
    static FloatFastConnection[] fastConnectionArray;
    static IActivationFunction[] activationFunctionArray;

    static public FloatFastConcurrentNetwork decodeToFloatFastConcurrentNetwork(NeatGenome g, IActivationFunction activationFn) {
        int outputNeuronCount = g.getOutputNeuronCount();
        int neuronGeneCount = g.getNeuronGeneList().size();

        // Slightly inefficient - determine the number of bias nodes. Fortunately there is not actually
        // any reason to ever have more than one bias node - although there may be 0.

        activationFunctionArray = new IActivationFunction[neuronGeneCount];

        int neuronGeneIdx = 0;
        for (; neuronGeneIdx < neuronGeneCount; neuronGeneIdx++) {
            activationFunctionArray[neuronGeneIdx] = g.getNeuronGeneList().get(neuronGeneIdx).getActivationFunction();
            if (g.getNeuronGeneList().get(neuronGeneIdx).getNeuronType() != NeuronType.BIAS)
                break;
        }
        int biasNodeCount = neuronGeneIdx;
        int inputNeuronCount = g.getInputNeuronCount();
        for (; neuronGeneIdx < neuronGeneCount; neuronGeneIdx++) {
            activationFunctionArray[neuronGeneIdx] = g.getNeuronGeneList().get(neuronGeneIdx).getActivationFunction();
        }

        // ConnectionGenes point to a neuron ID. We need to map this ID to a 0 based index for
        // efficiency.

        // Use a quick heuristic to determine which will be the fastest technique for mapping the connection end points
        // to neuron indexes. This is heuristic is not 100% perfect but has been found to be very good in in real word
        // tests. Feel free to perform your own calculation and create a more intelligent heuristic!
        int connectionCount = g.getConnectionGeneList().size();
        if (neuronGeneCount * connectionCount < 45000) {
            fastConnectionArray = new FloatFastConnection[connectionCount];
            int connectionIdx = 0;
            for (int connectionGeneIdx = 0; connectionGeneIdx < connectionCount; connectionGeneIdx++) {
                //fastConnectionArray[connectionIdx] = new FloatFastConnection();

                //TODO A nebylo tu
                fastConnectionArray[connectionIdx] = new FloatFastConnection();

                //Note. Binary search algorithm assume that neurons are ordered by their innovation Id.
                ConnectionGene connectionGene = g.getConnectionGeneList().get(connectionIdx);
                fastConnectionArray[connectionIdx].sourceNeuronIdx = g.getNeuronGeneList().binarySearch(connectionGene.getSourceNeuronId());
                fastConnectionArray[connectionIdx].targetNeuronIdx = g.getNeuronGeneList().binarySearch(connectionGene.getTargetNeuronId());

                Debug.Assert(fastConnectionArray[connectionIdx].sourceNeuronIdx >= 0 && fastConnectionArray[connectionIdx].targetNeuronIdx >= 0, "invalid idx");

                fastConnectionArray[connectionIdx].weight = (float) connectionGene.getWeight();
                connectionIdx++;
            }
        } else {
            // Build a table of indexes (ints) keyed on neuron ID. This approach is faster when dealing with large numbers
            // of lookups.
            Map<Integer, Integer> neuronIndexTable = new HashMap<Integer, Integer>(neuronGeneCount);
            for (int i = 0; i < neuronGeneCount; i++)
                neuronIndexTable.put(g.getNeuronGeneList().get(i).getInnovationId(), i);

            // Now we can build the connection array(s).
            //int connectionCount=g.ConnectionGeneList.Count;
            //FastConnection[] connectionArray = new FastConnection[connectionCount];
            fastConnectionArray = new FloatFastConnection[connectionCount];
            int connectionIdx = 0;
            for (int connectionGeneIdx = 0; connectionGeneIdx < connectionCount; connectionGeneIdx++) {
                ConnectionGene connectionGene = g.getConnectionGeneList().get(connectionIdx);
                fastConnectionArray[connectionIdx].sourceNeuronIdx = neuronIndexTable.get(connectionGene.getSourceNeuronId());
                fastConnectionArray[connectionIdx].targetNeuronIdx = neuronIndexTable.get(connectionGene.getTargetNeuronId());
                fastConnectionArray[connectionIdx].weight = (float) connectionGene.getWeight();
                connectionIdx++;
            }
        }
        // Now sort the connection array on sourceNeuronIdx, secondary sort on targetNeuronIdx.
        // Using Array.Sort is 10 times slower than the hand-coded sorting routine. See notes on that routine for more
        // information. Also note that in tests that this sorting did no t actually improve the speed of the network!
        // However, it may have a benefit for CPUs with small caches or when networks are very large, and since the new
        // sort takes up hardly any time for even large networks, it seems reasonable to leave in the sort.
        //Array.Sort(fastConnectionArray, fastConnectionComparer);
        //if(fastConnectionArray.Length>1)
        //	QuickSortFastConnections(0, fastConnectionArray.Length-1);

        return new FloatFastConcurrentNetwork(biasNodeCount, inputNeuronCount,
                outputNeuronCount, neuronGeneCount,
                fastConnectionArray, activationFunctionArray);
    }
    //TODO X other ANN implementations
    /*
    static public FastConcurrentMultiplicativeNetwork DecodeToFastConcurrentMultiplicativeNetwork(NeatGenome.NeatGenome g, IActivationFunction activationFn) {

        int outputNeuronCount = g.OutputNeuronCount;
        int neuronGeneCount = g.NeuronGeneList.Count;

        // Slightly inefficient - determine the number of bias nodes. Fortunately there is not actually
        // any reason to ever have more than one bias node - although there may be 0.
        int neuronGeneIdx = 0;
        for (; neuronGeneIdx < neuronGeneCount; neuronGeneIdx++) {
            if (g.NeuronGeneList[neuronGeneIdx].NeuronType != NeuronType.Bias)
                break;
        }
        int biasNodeCount = neuronGeneIdx;
        int inputNeuronCount = g.InputNeuronCount;

        // ConnectionGenes point to a neuron ID. We need to map this ID to a 0 based index for
        // efficiency. To do this we build a table of indexes (ints) keyed on neuron ID.
        // TODO: An alternative here would be to forgo the building of a table and do a binary
        // search directly on the NeuronGeneList - probably a good idea to use a heuristic based upon
        // neuroncount*connectioncount that decides on which technique to use. Small networks will
        // likely be faster to decode using the binary search.

        // Actually we can partly achieve the above optimzation by using HybridDictionary instead of Hashtable.
        // Although creating a table is a bit expensive.
        HybridDictionary neuronIndexTable = new HybridDictionary(neuronGeneCount);
        for (int i = 0; i < neuronGeneCount; i++)
            neuronIndexTable.Add(g.NeuronGeneList[i].InnovationId, i);

        // Count how many of the connections are actually enabled. TODO: make faster - store disable count?
        int connectionGeneCount = g.ConnectionGeneList.Count;
        int connectionCount = connectionGeneCount;
        //			for(int i=0; i<connectionGeneCount; i++)
        //			{
        //				if(g.ConnectionGeneList[i].Enabled)
        //					connectionCount++;
        //			}

        // Now we can build the connection array(s).
        FloatFastConnection[] connectionArray = new FloatFastConnection[connectionCount];
        int connectionIdx = 0;
        for (int connectionGeneIdx = 0; connectionGeneIdx < connectionCount; connectionGeneIdx++) {
            ConnectionGene connectionGene = g.ConnectionGeneList[connectionIdx];
            connectionArray[connectionIdx].sourceNeuronIdx = (int) neuronIndexTable[connectionGene.SourceNeuronId];
            connectionArray[connectionIdx].targetNeuronIdx = (int) neuronIndexTable[connectionGene.TargetNeuronId];
            connectionArray[connectionIdx].weight = (float) connectionGene.Weight;
            connectionIdx++;
        }

        // Now sort the connection array on sourceNeuronIdx, secondary sort on targetNeuronIdx.
        // TODO: custom sort routine to prevent boxing/unboxing required by Array.Sort(ValueType[])
        //Array.Sort(connectionArray, fastConnectionComparer);
        QuickSortFastConnections(0, fastConnectionArray.Length - 1);

        return new FastConcurrentMultiplicativeNetwork(
                biasNodeCount, inputNeuronCount,
                outputNeuronCount, neuronGeneCount,
                connectionArray, activationFn);
    }

    /// <summary>
    /// Create a single comparer to limit the need to reconstruct for each network.
    /// Not multithread safe!
    /// </summary>
    //static FastConnectionComparer fastConnectionComparer = new FastConnectionComparer();
    static IntegerFastConnection[] intFastConnectionArray;

    static public IntegerFastConcurrentNetwork DecodeToIntegerFastConcurrentNetwork(NeatGenome.NeatGenome g) {
        int outputNeuronCount = g.OutputNeuronCount;
        int neuronGeneCount = g.NeuronGeneList.Count;

        // Slightly inefficient - determine the number of bias nodes. Fortunately there is not actually
        // any reason to ever have more than one bias node - although there may be 0.
        int neuronGeneIdx = 0;
        for (; neuronGeneIdx < neuronGeneCount; neuronGeneIdx++) {
            if (g.NeuronGeneList[neuronGeneIdx].NeuronType != NeuronType.Bias)
                break;
        }
        int biasNodeCount = neuronGeneIdx;
        int inputNeuronCount = g.InputNeuronCount;

        // ConnectionGenes point to a neuron ID. We need to map this ID to a 0 based index for
        // efficiency.

        // Use a quick heuristic to determine which will be the fastest technique for mapping the connection end points
        // to neuron indexes. This is heuristic is not 100% perfect but has been found to be very good in in real word
        // tests. Feel free to perform your own calculation and create a more intelligent heuristic!
        int connectionCount = g.ConnectionGeneList.Count;
        if (neuronGeneCount * connectionCount < 45000) {
            intFastConnectionArray = new IntegerFastConnection[connectionCount];
            int connectionIdx = 0;
            for (int connectionGeneIdx = 0; connectionGeneIdx < connectionCount; connectionGeneIdx++) {
                //Note. Binary search algorithm assume that neurons are ordered by their innovation Id.
                ConnectionGene connectionGene = g.ConnectionGeneList[connectionIdx];
                intFastConnectionArray[connectionIdx].sourceNeuronIdx = (int) g.NeuronGeneList.BinarySearch(connectionGene.SourceNeuronId);
                intFastConnectionArray[connectionIdx].targetNeuronIdx = (int) g.NeuronGeneList.BinarySearch(connectionGene.TargetNeuronId);

                System.Diagnostics.Debug.Assert(intFastConnectionArray[connectionIdx].sourceNeuronIdx >= 0 && intFastConnectionArray[connectionIdx].targetNeuronIdx >= 0, "invalid idx");

                // Scale weight to range expected by the integer network class.
                // +-5 -> +-0x1000
                intFastConnectionArray[connectionIdx].weight = (int) (connectionGene.Weight * 0x333D);
                connectionIdx++;
            }
        } else {
            // Build a table of indexes (ints) keyed on neuron ID. This approach is faster when dealing with large numbers
            // of lookups.
            Hashtable neuronIndexTable = new Hashtable(neuronGeneCount);
            for (int i = 0; i < neuronGeneCount; i++)
                neuronIndexTable.Add(g.NeuronGeneList[i].InnovationId, i);

            // Now we can build the connection array(s).
            //int connectionCount=g.ConnectionGeneList.Count;
            //FastConnection[] connectionArray = new FastConnection[connectionCount];
            intFastConnectionArray = new IntegerFastConnection[connectionCount];
            int connectionIdx = 0;
            for (int connectionGeneIdx = 0; connectionGeneIdx < connectionCount; connectionGeneIdx++) {
                ConnectionGene connectionGene = g.ConnectionGeneList[connectionIdx];
                intFastConnectionArray[connectionIdx].sourceNeuronIdx = (int) neuronIndexTable[connectionGene.SourceNeuronId];
                intFastConnectionArray[connectionIdx].targetNeuronIdx = (int) neuronIndexTable[connectionGene.TargetNeuronId];

                // Scale weight to range expected by the integer network class.
                // +-5 -> +-0x1000
                intFastConnectionArray[connectionIdx].weight = (int) (connectionGene.Weight * 0x333D);
                connectionIdx++;
            }
        }

        // Now sort the connection array on sourceNeuronIdx, secondary sort on targetNeuronIdx.
        // Using Array.Sort is 10 times slower than the hand-coded sorting routine. See notes on that routine for more
        // information. Also note that in tests that this sorting did no t actually improve the speed of the network!
        // However, it may have a benefit for CPUs with small caches or when networks are very large, and since the new
        // sort takes up hardly any time for even large networks, it seems reasonable to leave in the sort.
        //Array.Sort(fastConnectionArray, fastConnectionComparer);
        if (intFastConnectionArray.Length > 1)
            QuickSortIntFastConnections(0, intFastConnectionArray.Length - 1);

        return new IntegerFastConcurrentNetwork(biasNodeCount, inputNeuronCount,
                outputNeuronCount, neuronGeneCount,
                intFastConnectionArray);
    }
    */

    //TODO X ANN visualization Model
    /*
    static public NetworkModel DecodeToNetworkModel(ConcurrentNetwork network) {
        ModelNeuronList masterNeuronList = new ModelNeuronList();

        // loop all neurons and build a table keyed on id.
        Hashtable neuronTable = new Hashtable(network.MasterNeuronList.Count);
        for (Neuron neuron : network.MasterNeuronList) {
            ModelNeuron modelNeuron = new ModelNeuron(neuron.NeuronType, neuron.Id, ActivationFunctionFactory.GetActivationFunction("NullFn"));
            neuronTable.Add(modelNeuron.Id, modelNeuron);
            masterNeuronList.Add(modelNeuron);
        }

        // Loop through all of the connections (within the neurons)
        // Now we have a neuron table keyed on id we can attach the connections
        // to their source and target neurons.
        for (Neuron neuron : network.MasterNeuronList) {
            for (Connection connection : neuron.ConnectionList) {
                ModelConnection modelConnection = new ModelConnection();
                modelConnection.Weight = connection.Weight;
                modelConnection.SourceNeuron = (ModelNeuron) neuronTable[connection.SourceNeuronId];
                modelConnection.TargetNeuron = (ModelNeuron) neuronTable[connection.TargetNeuronId];

                modelConnection.SourceNeuron.OutConnectionList.Add(modelConnection);
                modelConnection.TargetNeuron.InConnectionList.Add(modelConnection);
            }
        }

        return new NetworkModel(masterNeuronList);
    }

    static public NetworkModel DecodeToNetworkModel(NeatGenome.NeatGenome g) {
        ModelNeuronList masterNeuronList = new ModelNeuronList();

        // loop all neurons and build a table keyed on id.
        HybridDictionary neuronTable = new HybridDictionary(g.NeuronGeneList.Count);
        for (NeuronGene neuronGene : g.NeuronGeneList) {
            ModelNeuron modelNeuron = new ModelNeuron(neuronGene.NeuronType, neuronGene.InnovationId, neuronGene.ActivationFunction);
            neuronTable.Add(modelNeuron.Id, modelNeuron);
            masterNeuronList.Add(modelNeuron);
        }

        // Loop through all of the connections.
        // Now we have a neuron table keyed on id we can attach the connections
        // to their source and target neurons.
        for (ConnectionGene connectionGene : g.ConnectionGeneList) {
            ModelConnection modelConnection = new ModelConnection();
            modelConnection.Weight = connectionGene.Weight;
            modelConnection.SourceNeuron = (ModelNeuron) neuronTable[connectionGene.SourceNeuronId];
            modelConnection.TargetNeuron = (ModelNeuron) neuronTable[connectionGene.TargetNeuronId];

            modelConnection.SourceNeuron.OutConnectionList.Add(modelConnection);
            modelConnection.TargetNeuron.InConnectionList.Add(modelConnection);
        }

        return new NetworkModel(masterNeuronList);
    }

    // This is a quick sort algorithm that manipulates FastConnection structures. Although this
    // is the same sorting technique used internally by Array.Sort this is approximately 10 times
    // faster because it eliminates the need for boxing and unboxing of the structs.
    // So although this code could be replcaed by a single Array.Sort statement, the pay off
    // was though to be worth it.

    private static int CompareKeys(ref FloatFastConnection a, ref FloatFastConnection b) {
        int diff = a.sourceNeuronIdx - b.sourceNeuronIdx;
        if (diff == 0) {
            // Secondary sort on targetNeuronIdx.
            return a.targetNeuronIdx - b.targetNeuronIdx;
        } else {
            return diff;
        }
    }

    /// <summary>
    /// Standard qquicksort algorithm.
    /// </summary>
    /// <param name="left"></param>
    /// <param name="right"></param>
    private static void QuickSortFastConnections(int left, int right) {
        do {
            int i = left;
            int j = right;
            FloatFastConnection x = fastConnectionArray[(i + j) >> 1];
            do {
                while (CompareKeys(ref fastConnectionArray[i], ref x) < 0) i++;
                while (CompareKeys(ref x, ref fastConnectionArray[j]) < 0) j--;

                System.Diagnostics.Debug.Assert(i >= left && j <= right, "(i>=left && j<=right)  Sort failed - Is your IComparer bogus?");
                if (i > j) break;
                if (i < j) {
                    FloatFastConnection key = fastConnectionArray[i];
                    fastConnectionArray[i] = fastConnectionArray[j];
                    fastConnectionArray[j] = key;
                }
                i++;
                j--;
            } while (i <= j);

            if (j - left <= right - i) {
                if (left < j) QuickSortFastConnections(left, j);
                left = i;
            } else {
                if (i < right) QuickSortFastConnections(i, right);
                right = j;
            }
        } while (left < right);
    }

    // This is a quick sort algorithm that manipulates FastConnection structures. Although this
    // is the same sorting technique used internally by Array.Sort this is approximately 10 times
    // faster because it eliminates the need for boxing and unboxing of the structs.
    // So although this code could be replcaed by a single Array.Sort statement, the pay off
    // was though to be worth it.

    private static int CompareKeys(ref IntegerFastConnection a, ref IntegerFastConnection b) {
        int diff = a.sourceNeuronIdx - b.sourceNeuronIdx;
        if (diff == 0) {
            // Secondary sort on targetNeuronIdx.
            return a.targetNeuronIdx - b.targetNeuronIdx;
        } else {
            return diff;
        }
    }

    /// <summary>
    /// Standard qquicksort algorithm.
    /// </summary>
    /// <param name="left"></param>
    /// <param name="right"></param>
    private static void QuickSortIntFastConnections(int left, int right) {
        do {
            int i = left;
            int j = right;
            IntegerFastConnection x = intFastConnectionArray[(i + j) >> 1];
            do {
                while (CompareKeys(ref intFastConnectionArray[i], ref x) < 0) i++;
                while (CompareKeys(ref x, ref intFastConnectionArray[j]) < 0) j--;

                System.Diagnostics.Debug.Assert(i >= left && j <= right, "(i>=left && j<=right)  Sort failed - Is your IComparer bogus?");
                if (i > j) break;
                if (i < j) {
                    IntegerFastConnection key = intFastConnectionArray[i];
                    intFastConnectionArray[i] = intFastConnectionArray[j];
                    intFastConnectionArray[j] = key;
                }
                i++;
                j--;
            } while (i <= j);

            if (j - left <= right - i) {
                if (left < j) QuickSortIntFastConnections(left, j);
                left = i;
            } else {
                if (i < right) QuickSortIntFastConnections(i, right);
                right = j;
            }
        } while (left < right);
    }
    */
}
