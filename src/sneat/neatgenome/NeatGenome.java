package sneat.neatgenome;

import sneat.GenomeDecoder;
import sneat.evolution.*;
import sneat.evolution.neatparameters.ConnectionMutationParameterGroup;
import sneat.evolution.neatparameters.ConnectionSelectionType;
import sneat.maths.RandLib;
import sneat.maths.RouletteWheel;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;
import sneat.utilityclasses.Debug;
import sneat.utilityclasses.Utilities;

import java.util.*;

public class NeatGenome extends AbstractGenome {

    class NeuronConnectionLookup {
        public NeuronGene neuronGene;
        public ConnectionGeneList incomingList = new ConnectionGeneList();
        public ConnectionGeneList outgoingList = new ConnectionGeneList();
    }

    // Ensure that the connectionGenes are sorted by innovation ID at all times.
    NeuronGeneList neuronGeneList;
    ConnectionGeneList connectionGeneList;

    // For efficiency we store the number of input and output neurons. These two quantities do not change
    // throughout the life of a genome. Note that inputNeuronCount does NOT include the bias neuron! use inputAndBiasNeuronCount.
    // We also keep all input(including bias) neurons at the start of the neuronGeneList followed by
    // the output neurons.
    int inputNeuronCount;
    int inputAndBiasNeuronCount;
    int outputNeuronCount;
    int inputBiasOutputNeuronCount;
    int inputBiasOutputNeuronCountMinus2;

    // Build on-demand to represnt all of the ConnectionGene that do not have the FixedWeight bit set,
    // so that MutateConnectionWeights can operate more efficiently.
    ConnectionGeneList mutableConnectionGeneList = null;

    // Temp tables.
    HashMap<Integer, NeuronConnectionLookup> neuronConnectionLookupTable = null;
    Map<Integer, NeuronGene> neuronGeneTable = null;

    /// <summary>
    /// Default constructor.
    /// </summary>
    public NeatGenome(int genomeId,
                      NeuronGeneList neuronGeneList,
                      ConnectionGeneList connectionGeneList,
                      int inputNeuronCount,
                      int outputNeuronCount) {
        this.genomeId = genomeId;

        this.neuronGeneList = neuronGeneList;
        this.connectionGeneList = connectionGeneList;

        this.inputNeuronCount = inputNeuronCount;
        this.inputAndBiasNeuronCount = inputNeuronCount + 1;
        this.outputNeuronCount = outputNeuronCount;
        this.inputBiasOutputNeuronCount = inputAndBiasNeuronCount + outputNeuronCount;
        this.inputBiasOutputNeuronCountMinus2 = inputBiasOutputNeuronCount - 2;

        Debug.Assert(connectionGeneList.isSorted(), "ConnectionGeneList is not sorted by innovation ID");
    }

    /// <summary>
    /// Copy constructor.
    /// </summary>
    /// <param name="copyFrom"></param>
    public NeatGenome(NeatGenome copyFrom, int genomeId) {
        this.genomeId = genomeId;

        // No need to loop the arrays to clone each element because NeuronGene and ConnectionGene are
        // value data types (structs).
        neuronGeneList = new NeuronGeneList(copyFrom.neuronGeneList);
        connectionGeneList = new ConnectionGeneList(copyFrom.connectionGeneList);

        inputNeuronCount = copyFrom.inputNeuronCount;
        inputAndBiasNeuronCount = copyFrom.inputNeuronCount + 1;
        outputNeuronCount = copyFrom.outputNeuronCount;
        inputBiasOutputNeuronCount = copyFrom.inputBiasOutputNeuronCount;
        inputBiasOutputNeuronCountMinus2 = copyFrom.inputBiasOutputNeuronCountMinus2;

        Debug.Assert(connectionGeneList.isSorted(), "ConnectionGeneList is not sorted by innovation ID");
    }

    public NeuronGeneList getNeuronGeneList() {
        return neuronGeneList;
    }

    public ConnectionGeneList getConnectionGeneList() {
        return connectionGeneList;
    }

    public int getInputNeuronCount() {
        return inputNeuronCount;
    }

    public int getOutputNeuronCount() {
        return outputNeuronCount;
    }

/// <summary>
/// Some(most) types of network have fixed numbers of input and output nodes and will not work correctly or
/// throw an exception if we try and use inputs/outputs that do not exist. This method allows us to check

    /// compatibility before we begin.
    /// </summary>
/// <param name="inputCount"></param>
/// <param name="outputCount"></param>
/// <returns></returns>
    @Override
    public boolean isCompatibleWithNetwork(int inputCount, int outputCount) {
        return (inputCount == inputNeuronCount) && (outputCount == outputNeuronCount);
    }

/// <summary>

    /// Asexual reproduction with built in mutation.
    /// </summary>
/// <returns></returns>
    @Override
    public IGenome createOffspring_Asexual(EvolutionAlgorithm ea) {
        // Make an exact copy this Genome.
        NeatGenome offspring = new NeatGenome(this, ea.getNextGenomeId());

// Mutate the new genome.
        offspring.mutate(ea);
        return offspring;
    }


    private void createOffspring_Sexual_AddGene(ConnectionGene connectionGene, boolean overwriteExisting) {
        ConnectionEndpointsStruct connectionKey = new ConnectionEndpointsStruct(
                connectionGene.getSourceNeuronId(),
                connectionGene.getTargetNeuronId());

// Check if a matching gene has already been added.
        Integer oIdx = newConnectionGeneTable.get(connectionKey);
        if (oIdx == null) {    // No matching gene has been added.
            // Register this new gene with the newConnectionGeneTable - store its index within newConnectionGeneList.
            newConnectionGeneTable.put(connectionKey, newConnectionGeneList.size());

// Add the gene to the list.
            newConnectionGeneList.add(connectionGene);
        } else if (overwriteExisting) {
            // Overwrite the existing matching gene with this one. In fact only the weight value differs between two
            // matching connection genes, so just overwrite the existing genes weight value.

            // Remember that we stored the gene's index in newConnectionGeneTable. So use it here.
            newConnectionGeneList.get(oIdx).setWeight(connectionGene.getWeight());
        }
    }


    private void createOffspring_Sexual_ProcessCorrelationItem(CorrelationItem correlationItem, byte fitSwitch, boolean combineDisjointExcessFlag, NeatParameters np) {
        switch (correlationItem.getCorrelationItemType()) {
            // Disjoint and excess genes.
            case DisjointConnectionGene:
            case ExcessConnectionGene: {
                // If the gene is in the fittest parent then override any existing entry in the connectionGeneTable.
                if (fitSwitch == 1 && correlationItem.getConnectionGene1() != null) {
                    createOffspring_Sexual_AddGene(correlationItem.getConnectionGene1(), true);
                    return;
                }

                if (fitSwitch == 2 && correlationItem.getConnectionGene2() != null) {
                    createOffspring_Sexual_AddGene(correlationItem.getConnectionGene2(), true);
                    return;
                }

                // The disjoint/excess gene is on the least fit parent.
                //if(Utilities.nextDouble() < np.pDisjointExcessGenesRecombined)
                if (combineDisjointExcessFlag) {    // Include the gene n% of the time from whichever parent contains it.
                    if (correlationItem.getConnectionGene1() != null) {
                        // Gene is on least fit parent. Only add it to the genome if a matching gene hasn't already been added.
                        createOffspring_Sexual_AddGene(correlationItem.getConnectionGene1(), false);
                        return;
                    }
                    if (correlationItem.getConnectionGene2() != null) {
                        // Gene is on least fit parent. Only add it to the genome if a matching gene hasn't already been added.
                        createOffspring_Sexual_AddGene(correlationItem.getConnectionGene2(), false);
                        return;
                    }
                }
                break;
            }

            case MatchedConnectionGenes: {
                if (RouletteWheel.singleThrow(0.5)) {
                    // Override any existing entries in the table.
                    createOffspring_Sexual_AddGene(correlationItem.getConnectionGene1(), true);
                } else {
                    // Override any existing entries in the table.
                    createOffspring_Sexual_AddGene(correlationItem.getConnectionGene2(), true);
                }
                break;
            }
        }
    }

    /// <summary>
    /// A table that keeps a track of which connections have added to the sexually reproduced child genome.
    /// This is cleared on each call to CreateOffspring_Sexual() and is only declared at class level to
    /// prevent having to re-allocate the table and it's associated memory on each invokation.
    /// </summary>
    Map<ConnectionEndpointsStruct, Integer> newConnectionGeneTable;
    Set<Integer> newNeuronGeneTable;
    ConnectionGeneList newConnectionGeneList;

    @Override
    public IGenome createOffspring_Sexual(EvolutionAlgorithm ea, IGenome parent) {
        CorrelationResults correlationResults = CorrelateConnectionGeneLists(connectionGeneList, ((NeatGenome) parent).connectionGeneList);

        Debug.Assert(correlationResults.performIntegrityCheck(), "CorrelationResults failed integrity check.");

//----- Connection Genes.
// We will temporarily store the offspring's genes in newConnectionGeneList and keeping track of which genes
// exist with newConnectionGeneTable. Here we ensure these objects are created, and if they already existed
// then ensure they are cleared. Clearing existing objects is more efficient that creating new ones because
// allocated memory can be re-used.
        if (newConnectionGeneTable == null) {    // Provide a capacity figure to the new Hashtable. The offspring will be the same length (or thereabouts).
            newConnectionGeneTable = new HashMap<ConnectionEndpointsStruct, Integer>(connectionGeneList.size());
        } else {
            newConnectionGeneTable.clear();
        }
        //TODO: No 'capacity' constructor on CollectionBase. Create modified/custom CollectionBase.
        // newConnectionGeneList must be constructed on each call because it is passed to a new NeatGenome
        // at construction time and a permanent reference to the list is kept.
        newConnectionGeneList = new ConnectionGeneList(connectionGeneList.size());

// A switch that stores which parent is fittest 1 or 2. 0 if both are equal. More efficient to calculate this just once.
        byte fitSwitch;
        if (getFitness() > parent.getFitness())
            fitSwitch = 1;
        else if (getFitness() < parent.getFitness())
            fitSwitch = 2;
        else {    // Select one of the parents at random to be the 'master' genome during crossover.
            if (Utilities.nextDouble() < 0.5)
                fitSwitch = 1;
            else
                fitSwitch = 2;
        }

        boolean combineDisjointExcessFlag = Utilities.nextDouble() < ea.getNeatParameters().pDisjointExcessGenesRecombined ? true : false;

// Loop through the correlationResults, building a table of ConnectionGenes from the parent's that will make it into our
// new [single] offspring. We use a table keyed on connection end points to prevent passing connections to the offspring
// that may have the same end points but a different innovation number - effectively we filter out duplicate connections.
        int idxBound = correlationResults.getCorrelationItemList().size();
        for (int i = 0; i < idxBound; i++) {
            createOffspring_Sexual_ProcessCorrelationItem(correlationResults.getCorrelationItemList().get(i), fitSwitch, combineDisjointExcessFlag, ea.getNeatParameters());
        }

        //----- Neuron Genes.
        // Build a neuronGeneList by analysing each connection's neuron end-point IDs.
        // This strategy has the benefit of eliminating neurons that are no longer connected too.
        // Remember to always keep all input, output and bias neurons though!
        NeuronGeneList newNeuronGeneList = new NeuronGeneList(neuronGeneList.size());

// Keep a table of the NeuronGene ID's keyed by ID so that we can keep track of which ones have been added.
        if (newNeuronGeneTable == null)
            newNeuronGeneTable = new HashSet<Integer>(neuronGeneList.size());
        else
            newNeuronGeneTable.clear();

// Get the input/output neurons from this parent. All Genomes share these neurons, they do not change during a run.
        idxBound = neuronGeneList.size();
        for (int i = 0; i < idxBound; i++) {
            if (neuronGeneList.get(i).getNeuronType() != NeuronType.HIDDEN) {
                newNeuronGeneList.add(new NeuronGene(neuronGeneList.get(i)));
                newNeuronGeneTable.add(neuronGeneList.get(i).getInnovationId());
            } else {    // No more bias, input or output nodes. break the loop.
                break;
            }
        }

        // Now analyse the connections to determine which NeuronGenes are required in the offspring.
        idxBound = newConnectionGeneList.size();
        for (int i = 0; i < idxBound; i++) {
            NeuronGene neuronGene;
            ConnectionGene connectionGene = newConnectionGeneList.get(i);
            if (!newNeuronGeneTable.contains(connectionGene.getSourceNeuronId())) {
                //TODO: DAVID proper activation function
                // We can safely assume that any missing NeuronGenes at this point are hidden heurons.
                neuronGene = this.neuronGeneList.getNeuronById(connectionGene.getSourceNeuronId());
                if (neuronGene != null)
                    newNeuronGeneList.add(new NeuronGene(neuronGene));
                else
                    newNeuronGeneList.add(new NeuronGene(((NeatGenome) parent).getNeuronGeneList().getNeuronById(connectionGene.getSourceNeuronId())));
//newNeuronGeneList.Add(new NeuronGene(connectionGene.SourceNeuronId, NeuronType.Hidden, ActivationFunctionFactory.getActivationFunction("SteepenedSigmoid")));
                newNeuronGeneTable.add(connectionGene.getSourceNeuronId());
            }

            if (!newNeuronGeneTable.contains(connectionGene.getTargetNeuronId())) {
                //TODO: DAVID proper activation function
                // We can safely assume that any missing NeuronGenes at this point are hidden heurons.
                neuronGene = this.neuronGeneList.getNeuronById(connectionGene.getTargetNeuronId());
                if (neuronGene != null)
                    newNeuronGeneList.add(new NeuronGene(neuronGene));
                else
                    newNeuronGeneList.add(new NeuronGene(((NeatGenome) parent).getNeuronGeneList().getNeuronById(connectionGene.getTargetNeuronId())));
//newNeuronGeneList.Add(new NeuronGene(connectionGene.TargetNeuronId, NeuronType.Hidden, ActivationFunctionFactory.getActivationFunction("SteepenedSigmoid")));
                newNeuronGeneTable.add(connectionGene.getTargetNeuronId());
            }
        }

        // TODO: Inefficient code?
        newNeuronGeneList.sortByInnovationId();

// newConnectionGeneList is already sorted because it was generated by passing over the list returned by
// CorrelateConnectionGeneLists() - which is always in order.
        return new NeatGenome(ea.getNextGenomeId(), newNeuronGeneList, newConnectionGeneList, inputNeuronCount, outputNeuronCount);
    }


/// <summary>
/// Decode the genome's 'DNA' into a working network.
    /// </summary>
/// <returns></returns>

    @Override
    public INetwork decode(IActivationFunction activationFn) {
        if (network == null) {
//            network = GenomeDecoder.decodeToConcurrentNetwork(this, activationFn);
            network = GenomeDecoder.decodeToFloatFastConcurrentNetwork(this, activationFn);
//network = GenomeDecoder.DecodeToIntegerFastConcurrentNetwork(this);
//network = GenomeDecoder.DecodeToFastConcurrentMultiplicativeNetwork(this, activationFn);
        }

        return network;
    }

/// <summary>

    /// Clone this genome.
    /// </summary>
/// <returns></returns>
    @Override
    public IGenome clone(EvolutionAlgorithm ea) {
        // Utilise the copy constructor for cloning.
        return new NeatGenome(this, ea.getNextGenomeId());
    }

    @Override
    public boolean isCompatibleWithGenome(IGenome comparisonGenome, NeatParameters neatParameters) {
        /* A very simple way of implementing this routine is to call CorrelateConnectionGeneLists and to then loop
               * through the correlation items, calculating a compatibility score as we go. However, this routine
               * is heavily used and in performance tests was shown consume 40% of the CPU time for the core NEAT code.
               * Therefore this new routine has been rewritten with it's own version of the logic within
               * CorrelateConnectionGeneLists. This allows us to only keep comparing genes up to the point where the
               * threshold is passed. This also eliminates the need to build the correlation results list, this difference
               * alone is responsible for a 200x performance improvement when testing with a 1664 length genome!!
               *
               * A further optimisation is achieved by comparing the genes starting at the end of the genomes which is
               * where most disparities are located - new novel genes are always attached to the end of genomes. This
               * has the result of complicating the routine because we must now invoke additional logic to determine
               * which genes are excess and when the first disjoint gene is found. This is done with an extra integer:
               *
               * int excessGenesSwitch=0; // indicates to the loop that it is handling the first gene.
               *						=1;	// Indicates that the first gene was excess and on genome 1.
               *						=2;	// Indicates that the first gene was excess and on genome 2.
               *						=3;	// Indicates that there are no more excess genes.
               *
               * This extra logic has a slight performance hit, but this is minor especially in comparison to the savings that
               * are expected to be achieved overall during a NEAT search.
               *
               * If you have trouble understanding this logic then it might be best to work through the previous version of
               * this routine (below) that scans through the genomes from start to end, and which is a lot simpler.
               *
               */
        ConnectionGeneList list1 = this.connectionGeneList;
        ConnectionGeneList list2 = ((NeatGenome) comparisonGenome).connectionGeneList;
        int excessGenesSwitch = 0;

// Store these heavily used values locally.
        int list1Count = list1.size();
        int list2Count = list2.size();

//----- Test for special cases.
        if (list1Count == 0 && list2Count == 0) {    // Both lists are empty! No disparities, therefore the genomes are compatible!
            return true;
        }

        if (list1Count == 0) {    // All list2 genes are excess.
            return ((list2.size() * neatParameters.compatibilityExcessCoeff) < neatParameters.compatibilityThreshold);
        }

        if (list2Count == 0) {
            // All list1 genes are excess.
            return ((list1Count * neatParameters.compatibilityExcessCoeff) < neatParameters.compatibilityThreshold);
        }

        //----- Both ConnectionGeneLists contain genes - compare the contents.
        double compatibility = 0;
        int list1Idx = list1Count - 1;
        int list2Idx = list2Count - 1;
        ConnectionGene connectionGene1 = list1.get(list1Idx);
        ConnectionGene connectionGene2 = list2.get(list2Idx);
        for (; ;) {
            if (connectionGene2.getInnovationId() > connectionGene1.getInnovationId()) {
                // Most common test case(s) at top for efficiency.
                if (excessGenesSwitch == 3) {    // No more excess genes. Therefore this mismatch is disjoint.
                    compatibility += neatParameters.compatibilityDisjointCoeff;
                } else if (excessGenesSwitch == 2) {    // Another excess gene on genome 2.
                    compatibility += neatParameters.compatibilityExcessCoeff;
                } else if (excessGenesSwitch == 1) {    // We have found the first non-excess gene.
                    excessGenesSwitch = 3;
                    compatibility += neatParameters.compatibilityDisjointCoeff;
                } else //if(excessGenesSwitch==0)
                {    // First gene is excess, and is on genome 2.
                    excessGenesSwitch = 2;
                    compatibility += neatParameters.compatibilityExcessCoeff;
                }

                // Move to the next gene in list2.
                list2Idx--;
            } else if (connectionGene1.getInnovationId() == connectionGene2.getInnovationId()) {
                // No more excess genes. It's quicker to set this every time than to test if is not yet 3.
                excessGenesSwitch = 3;

// Matching genes. Increase compatibility by weight difference * coeff.
                compatibility += Math.abs(connectionGene1.getWeight() - connectionGene2.getWeight()) * neatParameters.compatibilityWeightDeltaCoeff;

// Move to the next gene in both lists.
                list1Idx--;
                list2Idx--;
            } else // (connectionGene2.InnovationId < connectionGene1.InnovationId)
            {
                // Most common test case(s) at top for efficiency.
                if (excessGenesSwitch == 3) {    // No more excess genes. Therefore this mismatch is disjoint.
                    compatibility += neatParameters.compatibilityDisjointCoeff;
                } else if (excessGenesSwitch == 1) {    // Another excess gene on genome 1.
                    compatibility += neatParameters.compatibilityExcessCoeff;
                } else if (excessGenesSwitch == 2) {    // We have found the first non-excess gene.
                    excessGenesSwitch = 3;
                    compatibility += neatParameters.compatibilityDisjointCoeff;
                } else //if(excessGenesSwitch==0)
                {    // First gene is excess, and is on genome 1.
                    excessGenesSwitch = 1;
                    compatibility += neatParameters.compatibilityExcessCoeff;
                }

                // Move to the next gene in list1.
                list1Idx--;
            }

            if (compatibility >= neatParameters.compatibilityThreshold)
                return false;

// Check if we have reached the end of one (or both) of the lists. If we have reached the end of both then
// we execute the first 'if' block - but it doesn't matter since the loop is not entered if both lists have
// been exhausted.
            if (list1Idx < 0) {
                // All remaining list2 genes are disjoint.
                compatibility += (list2Idx + 1) * neatParameters.compatibilityDisjointCoeff;
                return (compatibility < neatParameters.compatibilityThreshold);
            }

            if (list2Idx < 0) {
                // All remaining list1 genes are disjoint.
                compatibility += (list1Idx + 1) * neatParameters.compatibilityDisjointCoeff;
                return (compatibility < neatParameters.compatibilityThreshold);
            }

            connectionGene1 = list1.get(list1Idx);
            connectionGene2 = list2.get(list2Idx);
        }
    }

/* The first version of the optimised IsCompatibleWithGenome(). This version scans forward through the genomes,
 * keeping a running total of the compatibility figure as it goes. This version has been superceded by the one above!
 */
//		public override bool IsCompatibleWithGenome(IGenome comparisonGenome, NeatParameters neatParameters)
//		{
//		/* A very simple way of implementing this routine is to call CorrelateConnectionGeneLists and to then loop 
//			* through the correlation items, calculating a compatibility score as we go. However, this routine
//			* is heavily used and in performance tests was shown consume 40% of the CPU time for the core NEAT code.
//			* Therefore this new routine has been rewritten with it's own version of the logic within  
//			* CorrelateConnectionGeneLists. This allows us to only keep comparing genes up to the point where the
//			* threshold is passed.
//			*/
//			ConnectionGeneList list1 = this.connectionGeneList;
//			ConnectionGeneList list2 = ((NeatGenome)comparisonGenome).connectionGeneList;
//		
//			// Store these heavily used values locally.
//			int list1Count = list1.Count;
//			int list2Count = list2.Count;
//		
//			//----- Test for special cases.
//			if(list1Count==0 && list2Count==0)
//			{	// Both lists are empty! No disparities, therefore the genomes are compatible!
//				return true;
//			}
//		
//			if(list1Count==0)
//			{	// All list2 genes are excess.
//				return ((list2Count * neatParameters.compatibilityExcessCoeff) < neatParameters.compatibilityThreshold);
//			}
//		
//			if(list2Count==0)
//			{	
//				// All list1 genes are excess.
//				return ((list1Count * neatParameters.compatibilityExcessCoeff) < neatParameters.compatibilityThreshold);
//			}
//		
//			//----- Both ConnectionGeneLists contain genes - compare the contents.
//			double compatibility=0;
//			int list1Idx=0;
//			int list2Idx=0;
//			ConnectionGene connectionGene1 = list1[list1Idx];
//			ConnectionGene connectionGene2 = list2[list2Idx];
//			for(;;)
//			{
//				if(connectionGene2.InnovationId < connectionGene1.InnovationId)
//				{	
//					// connectionGene2 is disjoint.
//					compatibility += neatParameters.compatibilityDisjointCoeff;
//		
//					// Move to the next gene in list2.
//					list2Idx++;
//				}
//				else if(connectionGene1.InnovationId == connectionGene2.InnovationId)
//				{
//					// Matching genes. Increase compatibility by weight difference * coeff.
//					compatibility += Math.Abs(connectionGene1.Weight-connectionGene2.Weight) * neatParameters.compatibilityWeightDeltaCoeff;
//		
//					// Move to the next gene in both lists.
//					list1Idx++;
//					list2Idx++;
//				}
//				else // (connectionGene2.InnovationId > connectionGene1.InnovationId)
//				{	
//					// connectionGene1 is disjoint.
//					compatibility += neatParameters.compatibilityDisjointCoeff;
//					
//					// Move to the next gene in list1.
//					list1Idx++;
//				}
//				
//				if(compatibility >= neatParameters.compatibilityThreshold)
//					return false;
//		
//				// Check if we have reached the end of one (or both) of the lists. If we have reached the end of both then 
//				// we execute the first 'if' block - but it doesn't matter since the loop is not entered if both lists have 
//				// been exhausted.
//				if(list1Idx >= list1Count)
//				{	
//					// All remaining list2 genes are excess.
//					compatibility += (list2Count - list2Idx) * neatParameters.compatibilityExcessCoeff;
//					return (compatibility < neatParameters.compatibilityThreshold);
//				}
//		
//				if(list2Idx >= list2Count)
//				{
//					// All remaining list1 genes are excess.
//					compatibility += (list1Count - list1Idx) * neatParameters.compatibilityExcessCoeff;
//					return (compatibility < neatParameters.compatibilityThreshold);
//				}
//		
//				connectionGene1 = list1[list1Idx];
//				connectionGene2 = list2[list2Idx];
//			}
//		}


/* The original CalculateCompatibility function coverted to IsCompatibleWithGenome(). This calls CorrelateConnectionGeneLists() and then calculates
* a compatibility score from the results. If the score is over the threshold then the genomes are incompatible.
* This routine is superceded by the far more efficient IsCompatibleWithGenome() method.
*/
//		/// <summary>
//		/// Compare this IGenome with the provided one. This routine is utilized by the speciation logic.
//		/// </summary>
//		/// <param name="comparisonGenome"></param>
//		/// <returns></returns>
//		public override bool IsCompatibleWithGenome(IGenome comparisonGenome, NeatParameters neatParameters)
//		{
//			CorrelationResults correlationResults = CorrelateConnectionGeneLists(connectionGeneList, ((NeatGenome)comparisonGenome).connectionGeneList);
//				
//			double compatibilityVal =	neatParameters.compatibilityDisjointCoeff * correlationResults.CorrelationStatistics.DisjointConnectionGeneCount +
//										neatParameters.compatibilityExcessCoeff * correlationResults.CorrelationStatistics.ExcessConnectionGeneCount;
//				
//			if(correlationResults.CorrelationStatistics.MatchingGeneCount > 0)
//			{
//				compatibilityVal +=	neatParameters.compatibilityWeightDeltaCoeff * correlationResults.CorrelationStatistics.ConnectionWeightDelta;
//			}
//		
//			return compatibilityVal < neatParameters.compatibilityThreshold;		
//		}

    //TODO X XML
    /*
    @Override
    public void Write(XmlNode parentNode) {
        XmlGenomeWriterStatic.Write(parentNode, this);
    }
    */

/// <summary>

    /// For debug purposes only.
    /// </summary>
/// <returns>Returns true if genome integrity checks out OK.</returns>
    @Override
    public boolean performIntegrityCheck() {
        return connectionGeneList.isSorted();
    }

    public void fixConnectionWeights() {
        int bound = connectionGeneList.size();
        for (int i = 0; i < bound; i++)
            connectionGeneList.get(i).setFixedWeight(true);

// This will now be out of date. Although it should not need to be used after calling FixConnectionWeights.
        mutableConnectionGeneList = null;
    }

    private void mutate(EvolutionAlgorithm ea) {
        // Determine the type of mutation to perform.
        double[] probabilities = new double[]
                {
                        ea.getNeatParameters().pMutateAddNode,
                        ea.getNeatParameters().pMutateAddConnection,
                        ea.getNeatParameters().pMutateDeleteConnection,
                        ea.getNeatParameters().pMutateDeleteSimpleNeuron,
                        ea.getNeatParameters().pMutateConnectionWeights
                };

        int outcome = RouletteWheel.singleThrow(probabilities);

        switch (outcome) {
            case 0:
                mutate_AddNode(ea);
                break;
            case 1:
                mutate_AddConnection(ea);
                break;
            case 2:
                mutate_DeleteConnection();
                break;
            case 3:
                mutate_DeleteSimpleNeuronStructure(ea);
                break;
            case 4:
                mutate_ConnectionWeights(ea);
                break;
        }
    }

/// <summary>
/// Add a new node to the Genome. We do this by removing a connection at random and inserting
/// a new node and two new connections that make the same circuit as the original connection.

    ///
/// This way the new node is properly integrated into the network from the outset.
/// </summary>
/// <param name="ea"></param>
    private void mutate_AddNode(EvolutionAlgorithm ea) {
        if (connectionGeneList.size() == 0)
            return;

// Select a connection at random.
        int connectionToReplaceIdx = (int) Math.floor(Utilities.nextDouble() * connectionGeneList.size());
        ConnectionGene connectionToReplace = connectionGeneList.get(connectionToReplaceIdx);

// Delete the existing connection.
        connectionGeneList.remove(connectionToReplaceIdx);

// Check if this connection has already been split on another genome. If so then we should re-use the
// neuron ID and two connection ID's so that matching structures within the population maintain the same ID.
        NewNeuronGeneStruct existingNeuronGeneStruct = ea.getNewNeuronGeneStructTable().get(connectionToReplace.getInnovationId());

        NeuronGene newNeuronGene;
        ConnectionGene newConnectionGene1;
        ConnectionGene newConnectionGene2;
        IActivationFunction actFunct;
        if (existingNeuronGeneStruct == null) {    // No existing matching structure, so generate some new ID's.

            //TODO: DAVID proper random activation function
            // Replace connectionToReplace with two new connections and a neuron.
            actFunct = ActivationFunctionFactory.getRandomActivationFunction(ea.getNeatParameters());
            newNeuronGene = new NeuronGene(ea.getNextInnovationId(), NeuronType.HIDDEN, actFunct);
            newConnectionGene1 = new ConnectionGene(ea.getNextInnovationId(), connectionToReplace.getSourceNeuronId(), newNeuronGene.getInnovationId(), 1.0);
            newConnectionGene2 = new ConnectionGene(ea.getNextInnovationId(), newNeuronGene.getInnovationId(), connectionToReplace.getTargetNeuronId(), connectionToReplace.getWeight());

// Register the new ID's with NewNeuronGeneStructTable.
            ea.getNewNeuronGeneStructTable().put(connectionToReplace.getInnovationId(),
                    new NewNeuronGeneStruct(newNeuronGene, newConnectionGene1, newConnectionGene2));
        } else {    // An existing matching structure has been found. Re-use its ID's

            //TODO: DAVID proper random activation function
            // Replace connectionToReplace with two new connections and a neuron.
            actFunct = ActivationFunctionFactory.getRandomActivationFunction(ea.getNeatParameters());
            NewNeuronGeneStruct tmpStruct = existingNeuronGeneStruct;
            newNeuronGene = new NeuronGene(tmpStruct.NewNeuronGene.getInnovationId(), NeuronType.HIDDEN, actFunct);
            newConnectionGene1 = new ConnectionGene(tmpStruct.NewConnectionGene_Input.getInnovationId(), connectionToReplace.getSourceNeuronId(), newNeuronGene.getInnovationId(), 1.0);
            newConnectionGene2 = new ConnectionGene(tmpStruct.NewConnectionGene_Output.getInnovationId(), newNeuronGene.getInnovationId(), connectionToReplace.getTargetNeuronId(), connectionToReplace.getWeight());
        }

        // Add the new genes to the genome.
        neuronGeneList.add(newNeuronGene);
        connectionGeneList.insertIntoPosition(newConnectionGene1);
        connectionGeneList.insertIntoPosition(newConnectionGene2);
    }

    private void mutate_AddConnection(EvolutionAlgorithm ea) {
        // We are always guaranteed to have enough neurons to form connections - because the input/output neurons are
        // fixed. Any domain that doesn't require input/outputs is a bit nonsensical!

        // Make a fixed number of attempts at finding a suitable connection to add.

        if (neuronGeneList.size() > 1) {    // At least 2 neurons, so we have a chance at creating a connection.

            for (int attempts = 0; attempts < 5; attempts++) {
                // Select candidate source and target neurons. Any neuron can be used as the source. Input neurons
                // should not be used as a target
                int srcNeuronIdx;
                int tgtNeuronIdx;

/* Here's some code for adding connections that attempts to avoid any recursive conenctions
                     * within a network by only linking to neurons with innovation id's greater than the source neuron.
                     * Unfortunately this doesn't work because new neurons with large innovations ID's are inserted
                     * randomly through a network's topology! Hence this code remains here in readyness to be resurrected
                     * as part of some future work to support feedforward nets.
//					if(ea.getNeatParameters().feedForwardOnly)
//					{
//						/* We can ensure that all networks are feedforward only by only adding feedforward connections here.
//						 * Feed forward connections fall into one of the following categories.  All references to indexes 
//						 * are indexes within this genome's neuronGeneList:
//						 * 1) Source neuron is an input or hidden node, target is an output node.
//						 * 2) Source is an input or hidden node, target is a hidden node with an index greater than the source node's index.
//						 * 3) Source is an output node, target is an output node with an index greater than the source node's index.
//						 * 
//						 * These rules are easier to understand if you understand how the different types if neuron are arranged within
//						 * the neuronGeneList array. Neurons are arranged in the following order:
//						 * 
//						 * 1) A single bias neuron is always first.
//						 * 2) Experiment specific input neurons.
//						 * 3) Output neurons.
//						 * 4) Hidden neurons.
//						 * 
//						 * The quantity and innovationID of all neurons within the first 3 categories remains fixed throughout the life
//						 * of an experiment, hence we always know where to find the bias, input and output nodes. The number of hidden nodes
//						 * can vary as ne nodes are created, pruned away or perhaps dropped during crossover, however they are always arranged
//						 * newest to oldest, or in other words sorted by innovation idea, lowest ID first. 
//						 * 
//						 * If output neurons were at the end of the list with hidden nodes in the middle then generating feedforward 
//						 * connections would be as easy as selecting a target neuron with a higher index than the source neuron. However, that
//						 * type of arrangement is not conducive to the operation of other routines, hence this routine is a little bit more
//						 * complicated as a result.
//						 */
//					
//						// Ok, for a source neuron we can pick any neuron except the last output neuron.
//						int neuronIdxCount = neuronGeneList.Count;
//						int neuronIdxBound = neuronIdxCount-1;
//
//						// Generate count-1 possibilities and avoid the last output neuron's idx.
//						srcNeuronIdx = (int)Math.Floor(Utilities.nextDouble() * neuronIdxBound);
//						if(srcNeuronIdx>inputBiasOutputNeuronCountMinus2) srcNeuronIdx++;
//						
//
//						// Now generate a target idx depending on what type of neuron srcNeuronIdx is pointing to.
//						if(srcNeuronIdx<inputAndBiasNeuronCount)
//						{	// Source is a bias or input neuron. Target can be any output or hidden neuron.
//							tgtNeuronIdx = inputAndBiasNeuronCount + (int)Math.Floor(Utilities.nextDouble() * (neuronIdxCount-inputAndBiasNeuronCount));
//						}
//						else if(srcNeuronIdx<inputBiasOutputNeuronCount)
//						{	// Source is an output neuron, but not the last output neuron. Target can be any output neuron with an index
//							// greater than srcNeuronIdx.
//							tgtNeuronIdx = (inputAndBiasNeuronCount+1) + (int)Math.Floor(Utilities.nextDouble() * ((inputBiasOutputNeuronCount-1)-srcNeuronIdx));
//						}
//						else 
//						{	// Source is a hidden neuron. Target can be any hidden neuron after srcNeuronIdx or any output neuron.
//							tgtNeuronIdx = (int)Math.Floor(Utilities.nextDouble() * ((neuronIdxBound-srcNeuronIdx)+outputNeuronCount));
//
//							if(tgtNeuronIdx<outputNeuronCount)
//							{	// Map to an output neuron idx.
//								tgtNeuronIdx += inputAndBiasNeuronCount;
//							}
//							else
//							{
//								// Map to one of the hidden neurons after srcNeuronIdx.
//								tgtNeuronIdx = (tgtNeuronIdx-outputNeuronCount)+srcNeuronIdx+1;
//							}
//						}
//					}

// Source neuron can by any neuron. Target neuron is any neuron except input neurons.
                srcNeuronIdx = (int) Math.floor(Utilities.nextDouble() * neuronGeneList.size());
                tgtNeuronIdx = inputAndBiasNeuronCount + (int) Math.floor(Utilities.nextDouble() * (neuronGeneList.size() - inputAndBiasNeuronCount));

                NeuronGene sourceNeuron = neuronGeneList.get(srcNeuronIdx);
                NeuronGene targetNeuron = neuronGeneList.get(tgtNeuronIdx);

// Check if a connection already exists between these two neurons.
                int sourceId = sourceNeuron.getInnovationId();
                int targetId = targetNeuron.getInnovationId();

                if (!testForExistingConnection(sourceId, targetId)) {
                    // Check if a matching mutation has already occured on another genome.
                    // If so then re-use the connection ID.
                    ConnectionEndpointsStruct connectionKey = new ConnectionEndpointsStruct(sourceId, targetId);
                    ConnectionGene existingConnection = ea.getNewConnectionGeneTable().get(connectionKey);
                    ConnectionGene newConnectionGene;
                    if (existingConnection == null) {    // Create a new connection with a new ID and add it to the Genome.
                        newConnectionGene = new ConnectionGene(ea.getNextInnovationId(), sourceId, targetId,
                                (Utilities.nextDouble() * ea.getNeatParameters().connectionWeightRange) - ea.getNeatParameters().connectionWeightRange / 2.0);

// Register the new connection with NewConnectionGeneTable.
                        ea.getNewConnectionGeneTable().put(connectionKey, newConnectionGene);

// Add the new gene to this genome. We have a new ID so we can safely append the gene to the end
// of the list without risk of breaking the innovation ID order.
                        connectionGeneList.add(newConnectionGene);
                    } else {    // Create a new connection, re-using the ID from existingConnection, and add it to the Genome.
                        newConnectionGene = new ConnectionGene(existingConnection.getInnovationId(), sourceId, targetId,
                                (Utilities.nextDouble() * ea.getNeatParameters().connectionWeightRange) - ea.getNeatParameters().connectionWeightRange / 2.0);

// Add the new gene to this genome. We are re-using an ID so we must ensure the connection gene is
// inserted into the correct position (sorted by innovation ID).
                        connectionGeneList.insertIntoPosition(newConnectionGene);
                    }

                    return;
                }
            }
        }

        // We couldn't find a valid connection to create. Instead of doing nothing lets perform connection
        // weight mutation.
        mutate_ConnectionWeights(ea);
    }

    private void mutate_DeleteConnection() {
        if (connectionGeneList.size() == 0)
            return;

// Select a connection at random.
        int connectionToDeleteIdx = (int) Math.floor(Utilities.nextDouble() * connectionGeneList.size());
        ConnectionGene connectionToDelete = connectionGeneList.get(connectionToDeleteIdx);

// Delete the connection.
        connectionGeneList.remove(connectionToDeleteIdx);

// Remove any neurons that may have been left floating.
        if (isNeuronRedundant(connectionToDelete.getSourceNeuronId()))
            neuronGeneList.remove(connectionToDelete.getSourceNeuronId());

// Recurrent connection has both end points at the same neuron!
        if (connectionToDelete.getSourceNeuronId() != connectionToDelete.getTargetNeuronId())
            if (isNeuronRedundant(connectionToDelete.getTargetNeuronId()))
                neuronGeneList.remove(connectionToDelete.getTargetNeuronId());
    }


/// <summary>
/// We define a simple neuron structure as a neuron that has a single outgoing or single incoming connection.
/// With such a structure we can easily eliminate the neuron and shift it's connections to an adjacent neuron.

    /// If the neuron's non-linearity was not being used then such a mutation is a simplification of the network
/// structure that shouldn't adversly affect its functionality.
/// </summary>
    private void mutate_DeleteSimpleNeuronStructure(EvolutionAlgorithm ea) {
        // We will use the NeuronConnectionLookupTable to find the simple structures.
        ensureNeuronConnectionLookupTable();

// Build a list of candidate simple neurons to choose from.
        ArrayList<Integer> simpleNeuronIdList = new ArrayList<Integer>();

        for (NeuronConnectionLookup lookup : neuronConnectionLookupTable.values()) {
            // If we test the connection count with <=1 then we also pick up neurons that are in dead-end circuits,
            // RemoveSimpleNeuron is then able to delete these neurons from the network structure along with any
            // associated connections.
            if (lookup.neuronGene.getNeuronType() == NeuronType.HIDDEN) {
                if ((lookup.incomingList.size() <= 1) || (lookup.outgoingList.size() <= 1))
                    simpleNeuronIdList.add(lookup.neuronGene.getInnovationId());
            }
        }

        // Are there any candiate simple neurons?
        if (simpleNeuronIdList.size() == 0) {    // No candidate neurons. As a fallback lets delete a connection.
            mutate_DeleteConnection();
            return;
        }

        // Pick a simple neuron at random.
        int idx = (int) Math.floor(Utilities.nextDouble() * simpleNeuronIdList.size());
        int neuronId = simpleNeuronIdList.get(idx);
        removeSimpleNeuron(neuronId, ea);
    }

/// <summary>
/// The routine also

    /// </summary>
/// <param name="neuronId"></param>
/// <param name="ea"></param>
    private void removeSimpleNeuron(int neuronId, EvolutionAlgorithm ea) {
        // Create new connections that connect all of the incoming and outgoing neurons
        // that currently exist for the simple neuron.
        NeuronConnectionLookup lookup = neuronConnectionLookupTable.get(neuronId);
        for (ConnectionGene incomingConnection : lookup.incomingList.getList()) {
            for (ConnectionGene outgoingConnection : lookup.outgoingList.getList()) {
                if (testForExistingConnection(incomingConnection.getSourceNeuronId(), outgoingConnection.getTargetNeuronId())) {    // Connection already exists.
                    continue;
                }

                // Test for matching connection within NewConnectionGeneTable.
                ConnectionEndpointsStruct connectionKey = new ConnectionEndpointsStruct(incomingConnection.getSourceNeuronId(),
                        outgoingConnection.getTargetNeuronId());
                ConnectionGene existingConnection = ea.getNewConnectionGeneTable().get(connectionKey);
                ConnectionGene newConnectionGene;
                if (existingConnection == null) {    // No matching connection found. Create a connection with a new ID.
                    newConnectionGene = new ConnectionGene(ea.getNextInnovationId(),
                            incomingConnection.getSourceNeuronId(),
                            outgoingConnection.getTargetNeuronId(),
                            (Utilities.nextDouble() * ea.getNeatParameters().connectionWeightRange) - ea.getNeatParameters().connectionWeightRange / 2.0);

// Register the new ID with NewConnectionGeneTable.
                    ea.getNewConnectionGeneTable().put(connectionKey, newConnectionGene);

// Add the new gene to the genome.
                    connectionGeneList.add(newConnectionGene);
                } else {    // Matching connection found. Re-use its ID.
                    newConnectionGene = new ConnectionGene(existingConnection.getInnovationId(),
                            incomingConnection.getSourceNeuronId(),
                            outgoingConnection.getTargetNeuronId(),
                            (Utilities.nextDouble() * ea.getNeatParameters().connectionWeightRange) - ea.getNeatParameters().connectionWeightRange / 2.0);

// Add the new gene to the genome. Use InsertIntoPosition() to ensure we don't break the sort
// order of the connection genes.
                    connectionGeneList.insertIntoPosition(newConnectionGene);
                }


            }
        }

        // Delete the old connections.
        for (ConnectionGene incomingConnection : lookup.incomingList.getList())
            connectionGeneList.remove(incomingConnection);

        for (ConnectionGene outgoingConnection : lookup.outgoingList.getList()) {
            // Filter out recurrent connections - they will have already been
            // deleted in the loop through 'lookup.incomingList'.
            if (outgoingConnection.getTargetNeuronId() != neuronId)
                connectionGeneList.remove(outgoingConnection);
        }

        // Delete the simple neuron - it no longer has any connections to or from it.
        neuronGeneList.remove(neuronId);
    }


    private void mutateConnectionWeight(ConnectionGene connectionGene, NeatParameters np, ConnectionMutationParameterGroup paramGroup) {
        double w = connectionGene.getWeight();
        switch (paramGroup.perturbationType) {
            case JIGGLE_EVEN: {
                connectionGene.setWeight(w + (Utilities.nextDouble() * 2 - 1.0) * paramGroup.PerturbationFactor);

// Cap the connection weight. Large connections weights reduce the effectiveness of the search.
                connectionGene.setWeight(Math.max(w, -np.connectionWeightRange / 2.00));
                connectionGene.setWeight(Math.min(w, np.connectionWeightRange / 2.0));
                break;
            }
            case JIGGLE_ND: {
                connectionGene.setWeight(w + RandLib.gennor(0, paramGroup.Sigma));

// Cap the connection weight. Large connections weights reduce the effectiveness of the search.
                connectionGene.setWeight(Math.max(w, -np.connectionWeightRange / 2.0));
                connectionGene.setWeight(Math.min(w, np.connectionWeightRange / 2.0));
                break;
            }
            case RESET: {
                // TODO: Precalculate connectionWeightRange / 2.
                connectionGene.setWeight((Utilities.nextDouble() * np.connectionWeightRange) - np.connectionWeightRange / 2.0);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected ConnectionPerturbationType");
            }
        }
    }

    private void mutate_ConnectionWeights(EvolutionAlgorithm ea) {
        // Determine the type of weight mutation to perform.
        int groupCount = ea.getNeatParameters().connectionMutationParameterGroupList.getGroupList().size();
        double[] probabilties = new double[groupCount];
        for (int i = 0; i < groupCount; i++) {
            probabilties[i] = ea.getNeatParameters().connectionMutationParameterGroupList.getGroupList().get(i).activationProportion;
        }

        // Get a reference to the group we will be using.
        ConnectionMutationParameterGroup paramGroup = ea.getNeatParameters().connectionMutationParameterGroupList.getGroupList().get(RouletteWheel.singleThrow(probabilties));

// Perform mutations of the required type.
        if (paramGroup.selectionType == ConnectionSelectionType.PROPORTIONAL) {
            boolean mutationOccured = false;
            int connectionCount = connectionGeneList.size();
            for (int i = 0; i < connectionCount; i++) {
                if (Utilities.nextDouble() < paramGroup.Proportion) {
                    mutateConnectionWeight(connectionGeneList.get(i), ea.getNeatParameters(), paramGroup);
                    mutationOccured = true;
                }
            }
            if (!mutationOccured && connectionCount > 0) {    // Perform at least one mutation. Pick a gene at random.
                mutateConnectionWeight(connectionGeneList.get((int) (Utilities.nextDouble() * connectionCount)),
                        ea.getNeatParameters(),
                        paramGroup);
            }
        } else // if(paramGroup.SelectionType==ConnectionSelectionType.FixedQuantity)
        {
            // Determine how many mutations to perform. At least one - if there are any genes.
            int connectionCount = connectionGeneList.size();
            int mutations = Math.min(connectionCount, Math.max(1, paramGroup.Quantity));
            if (mutations == 0) return;

// The mutation loop. Here we pick an index at random and scan forward from that point
// for the first non-mutated gene. This prevents any gene from being mutated more than once without
// too much overhead. In fact it's optimal for small numbers of mutations where clashes are unlikely
// to occur.
            for (int i = 0; i < mutations; i++) {
                // Pick an index at random.
                int index = (int) (Utilities.nextDouble() * connectionCount);
                ConnectionGene connectionGene = connectionGeneList.get(index);

// Scan forward and find the first non-mutated gene.
                while (connectionGene.isMutated()) {    // Increment index. Wrap around back to the start if we go off the end.
                    if (++index == connectionCount)
                        index = 0;
                }

                // Mutate the gene at 'index'.
                mutateConnectionWeight(connectionGene, ea.getNeatParameters(), paramGroup);
                connectionGene.setMutated(true);
            }
        }
    }

//		private void Mutate_ConnectionWeights(EvolutionAlgorithm ea)
//		{
//			float pColdGaussian, pMutation;
//			bool bMutateAllMutableConnections=false;
//			ConnectionGeneList tmpConnectionGeneList=null;
//
//			if(connectionGeneList.Count==0)
//				return;
//
//			// n% of the time perform more severe connection weight mutation (cold gaussian).
//			if(RouletteWheel.singleThrow(0.5))
//			{
//				// TODO: Migrate mutation proportion values to the NeatParameters structure?
//				pMutation = 0.1F;
//				pColdGaussian = 1.0F;
//			}
//			else
//			{
//				pMutation = 0.8F;		// mutate 80% of weights.	
//				pColdGaussian = 0.0F;	// 0% of those are cold resets.
//			}
//
//
//			// Determine what type of mutation scheme to use.
//			if(ea.IsConnectionWeightFixingEnabled)
//			{
//				EnsureMutableConnectionGeneList();
//				if(mutableConnectionGeneList.Count==0)
//					return;
//
//				// Only mutate pMutation connections at most. If mutable connections make up a lesser proportion
//				// of total connections then just mutate all of the mutable connections.
//				float pMutableConnections = (float)mutableConnectionGeneList.Count / (float)connectionGeneList.Count;
//				if(pMutableConnections <= pMutation)
//					bMutateAllMutableConnections=true;
//				else
//					tmpConnectionGeneList = mutableConnectionGeneList;
//			}
//			else
//			{
//				tmpConnectionGeneList = connectionGeneList;
//			}
//
//			NeatParameters np = ea.getNeatParameters();
//			if(bMutateAllMutableConnections)
//			{
//				// Mutate all connections in mutableConnectionGeneList.
//				int bound = mutableConnectionGeneList.Count;
//				for(int i=0; i<bound; i++)
//				{
//					ConnectionGene connectionGene = mutableConnectionGeneList[i];
//
//					if(Utilities.nextDouble() < pColdGaussian)
//					{	// Cold Normal dist.
//						connectionGene.Weight = (Utilities.nextDouble()* np.connectionWeightRange) - np.connectionWeightRange/2.0;
//					}
//					else
//					{	// Normal distribution..
//						connectionGene.Weight = ValueMutation.Mutate(connectionGene.Weight, np.connectionMutationSigma);
//					}
//					// Cap the connection weight. Large connections weights reduce the effectiveness of the search.
//					connectionGene.Weight = Math.Max(connectionGene.Weight, -np.connectionWeightRange/2.0);
//					connectionGene.Weight = Math.Min(connectionGene.Weight, np.connectionWeightRange/2.0);
//				}
//			}
//			else
//			{
//				// Determine how many connections to mutate (minimum of 1)
//				int mutateCount = (int)Math.Ceiling(connectionGeneList.Count * pMutation);
//				for(int i=0; i<mutateCount; i++)
//				{
//					// Pick a connection at random.
//					ConnectionGene connectionGene = tmpConnectionGeneList[(int)(Utilities.nextDouble() * tmpConnectionGeneList.Count)];
//
//					if(Utilities.nextDouble() < pColdGaussian)
//					{	// Cold Normal dist.
//						connectionGene.Weight = (Utilities.nextDouble()*np.connectionWeightRange) - np.connectionWeightRange/2.0;
//					}
//					else
//					{	// Normal distribution..
//						connectionGene.Weight = ValueMutation.Mutate(connectionGene.Weight, np.connectionMutationSigma);
//					}
//					// Cap the connection weight. Large connections weights reduce the effectiveness of the search.
//					connectionGene.Weight = Math.Max(connectionGene.Weight, - np.connectionWeightRange/2.0);
//					connectionGene.Weight = Math.Min(connectionGene.Weight, np.connectionWeightRange/2.0);
//				}
//			}
//		}

//		private void MutateWeight(ConnectionGene connectionGene, NeatParameters np)
//		{
//			if(Utilities.nextDouble() < 0.2)
//			{
//				connectionGene.Weight = (Utilities.nextDouble()*np.connectionWeightRange) - np.connectionWeightRange/2.0;
//			}
//			else
//			{
//				connectionGene.Weight += (Utilities.nextDouble()*2-1.0) * 0.1;
//
//				// Cap the connection weight. Large connections weights reduce the effectiveness of the search.
//				connectionGene.Weight = Math.Max(connectionGene.Weight, - np.connectionWeightRange/2.0);
//				connectionGene.Weight = Math.Min(connectionGene.Weight, np.connectionWeightRange/2.0);
//			}
//		}

    /// <summary>
/// Correlate the ConnectionGenes within the two ConnectionGeneLists - based upon innovation number.
/// Return an ArrayList of ConnectionGene[2] structures - pairs of matching ConnectionGenes.
/// </summary>
/// <param name="list1"></param>
/// <param name="list2"></param>
/// <returns></returns>
    private CorrelationResults CorrelateConnectionGeneLists(ConnectionGeneList list1, ConnectionGeneList list2) {
        CorrelationResults correlationResults = new CorrelationResults();

//----- Test for special cases.
        if (list1.size() == 0 && list2.size() == 0) {    // Both lists are empty!
            return correlationResults;
        }

        if (list1.size() == 0) {    // All list2 genes are excess.
            correlationResults.getCorrelationStatistics().excessConnectionGeneCount = list2.size();
            for (ConnectionGene connectionGene : list2.getList())
                correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.ExcessConnectionGene, null, connectionGene));

            return correlationResults;
        }

        if (list2.size() == 0) {    // All list1 genes are excess.
            correlationResults.getCorrelationStatistics().excessConnectionGeneCount = list1.size();
            for (ConnectionGene connectionGene : list1.getList())
                correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.ExcessConnectionGene, null, connectionGene));

            return correlationResults;
        }

        //----- Both ConnectionGeneLists contain genes - compare the contents.
        int list1Idx = 0;
        int list2Idx = 0;
        ConnectionGene connectionGene1 = list1.get(list1Idx);
        ConnectionGene connectionGene2 = list2.get(list2Idx);
        for (; ;) {
            if (connectionGene2.getInnovationId() < connectionGene1.getInnovationId()) {
                // connectionGene2 is disjoint.
                correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.DisjointConnectionGene, null, connectionGene2));
                correlationResults.getCorrelationStatistics().disjointConnectionGeneCount++;

// Move to the next gene in list2.
                list2Idx++;
            } else if (connectionGene1.getInnovationId() == connectionGene2.getInnovationId()) {
                correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.MatchedConnectionGenes, connectionGene1, connectionGene2));
                correlationResults.getCorrelationStatistics().connectionWeightDelta += Math.abs(connectionGene1.getWeight() - connectionGene2.getWeight());
                correlationResults.getCorrelationStatistics().matchingGeneCount++;

// Move to the next gene in both lists.
                list1Idx++;
                list2Idx++;
            } else // (connectionGene2.InnovationId > connectionGene1.InnovationId)
            {
                // connectionGene1 is disjoint.
                correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.DisjointConnectionGene, connectionGene1, null));
                correlationResults.getCorrelationStatistics().disjointConnectionGeneCount++;

// Move to the next gene in list1.
                list1Idx++;
            }

            // Check if we have reached the end of one (or both) of the lists. If we have reached the end of both then
            // we execute the first if block - but it doesn't matter since the loop is not entered if both lists have
            // been exhausted.
            if (list1Idx >= list1.size()) {
                // All remaining list2 genes are excess.
                for (; list2Idx < list2.size(); list2Idx++) {
                    correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.ExcessConnectionGene, null, list2.get(list2Idx)));
                    correlationResults.getCorrelationStatistics().excessConnectionGeneCount++;
                }
                return correlationResults;
            }

            if (list2Idx >= list2.size()) {
                // All remaining list1 genes are excess.
                for (; list1Idx < list1.size(); list1Idx++) {
                    correlationResults.getCorrelationItemList().add(new CorrelationItem(CorrelationItemType.ExcessConnectionGene, list1.get(list1Idx), null));
                    correlationResults.getCorrelationStatistics().excessConnectionGeneCount++;
                }
                return correlationResults;
            }

            connectionGene1 = list1.get(list1Idx);
            connectionGene2 = list2.get(list2Idx);
        }
    }


/// <summary>

    /// If the neuron is a hidden neuron and no connections connect to it then it is redundant.
/// </summary>
    private boolean isNeuronRedundant(int neuronId) {
        NeuronGene neuronGene = neuronGeneList.getNeuronById(neuronId);
        if (neuronGene.getNeuronType() != NeuronType.HIDDEN)
            return false;

        return !isNeuronConnected(neuronId);
    }


    private boolean isNeuronConnected(int neuronId) {
        int bound = connectionGeneList.size();
        for (int i = 0; i < bound; i++) {
            ConnectionGene connectionGene = connectionGeneList.get(i);
            if (connectionGene.getSourceNeuronId() == neuronId)
                return true;

            if (connectionGene.getTargetNeuronId() == neuronId)
                return true;
        }

        return false;
    }

    private void ensureMutableConnectionGeneList() {
        if (mutableConnectionGeneList != null)
            return;

        mutableConnectionGeneList = new ConnectionGeneList();

        int bound = connectionGeneList.size();
        for (int i = 0; i < bound; i++) {
            ConnectionGene connectionGene = connectionGeneList.get(i);
            if (!connectionGene.isFixedWeight())
                mutableConnectionGeneList.add(connectionGene);
        }
    }

    private void ensureNeuronTable() {
        if (neuronGeneTable == null)
            buildNeuronTable();
    }

    private void buildNeuronTable() {
        neuronGeneTable = new HashMap<Integer, NeuronGene>();

        for (NeuronGene neuronGene : neuronGeneList.getList())
            neuronGeneTable.put(neuronGene.getInnovationId(), neuronGene);
    }

    private void ensureNeuronConnectionLookupTable() {
        if (neuronConnectionLookupTable == null)
            buildNeuronConnectionLookupTable();
    }

    private void buildNeuronConnectionLookupTable() {
        ensureNeuronTable();

        neuronConnectionLookupTable = new HashMap<Integer, NeuronConnectionLookup>();
        for (ConnectionGene connectionGene : connectionGeneList.getList()) {
            buildNeuronConnectionLookupTable_NewIncomingConnection(connectionGene.getTargetNeuronId(), connectionGene);
            buildNeuronConnectionLookupTable_NewOutgoingConnection(connectionGene.getSourceNeuronId(), connectionGene);
        }
    }

    private void buildNeuronConnectionLookupTable_NewIncomingConnection(int neuronId, ConnectionGene connectionGene) {
        // Is this neuron already known to the lookup table?
        NeuronConnectionLookup lookup = neuronConnectionLookupTable.get(neuronId);
        if (lookup == null) {    // Creae a new lookup entry for this neuron Id.
            lookup = new NeuronConnectionLookup();
            lookup.neuronGene = neuronGeneTable.get(neuronId);
            neuronConnectionLookupTable.put(neuronId, lookup);
        }

        // Register the connection with the NeuronConnectionLookup object.
        lookup.incomingList.add(connectionGene);
    }

    private void buildNeuronConnectionLookupTable_NewOutgoingConnection(int neuronId, ConnectionGene connectionGene) {
        // Is this neuron already known to the lookup table?
        NeuronConnectionLookup lookup = neuronConnectionLookupTable.get(neuronId);
        if (lookup == null) {    // Creae a new lookup entry for this neuron Id.
            lookup = new NeuronConnectionLookup();
            lookup.neuronGene = neuronGeneTable.get(neuronId);
            neuronConnectionLookupTable.put(neuronId, lookup);
        }

        // Register the connection with the NeuronConnectionLookup object.
        lookup.outgoingList.add(connectionGene);
    }

    private boolean testForExistingConnection(int sourceId, int targetId) {
        for (int connectionIdx = 0; connectionIdx < connectionGeneList.size(); connectionIdx++) {
            ConnectionGene connectionGene = connectionGeneList.get(connectionIdx);
            if (connectionGene.getSourceNeuronId() == sourceId && connectionGene.getTargetNeuronId() == targetId)
                return true;
        }
        return false;
    }
}
