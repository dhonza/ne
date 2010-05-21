package sneat.evolution;

import sneat.neatgenome.ConnectionGene;
import sneat.neatgenome.NeatGenome;
import sneat.utilityclasses.Debug;
import sneat.utilityclasses.Utilities;

import java.util.*;
import java.util.logging.Logger;

public class EvolutionAlgorithm {
    private static Logger logger = Logger.getLogger("sneat.evolution.EvolutionAlgorithm");
    /// <summary>
    /// Genomes cannot have zero fitness because the fitness sharing logic requires there to be
    /// a non-zero total fitness in the population. Therefore this figure should be substituted
    /// in where zero fitness occurs.
    /// </summary>
    public static double MIN_GENOME_FITNESS = 0.0000001;

    Population pop;
    IPopulationEvaluator populationEvaluator;
    NeatParameters neatParameters;
    NeatParameters neatParameters_Normal;
    NeatParameters neatParameters_PrunePhase;

    boolean pruningModeEnabled = false;
    boolean connectionWeightFixingEnabled = false;
    boolean pruningMode = false;

    /// <summary>
    /// The last generation at which Population.AvgComplexity was reduced. We track this
    /// when simplifications have completed and that therefore the prune phase should end.
    /// </summary>
    long prunePhase_generationAtLastSimplification;
    float prunePhase_MinimumStructuresPerGenome;

    /// <summary>
    /// Population.AvgComplexity when AdjustSpeciationThreshold() was last called. If mean complexity
    /// moves away from this value by a certain amount then it's time to re-apply the speciation threshold
    /// to the whole population by calling pop.RedetermineSpeciation().
    /// </summary>
    double meanComplexityAtLastAdjustSpeciationThreshold;

    // All offspring are temporarily held here before being added to the population proper.
    List<IGenome> offspringList = new ArrayList<IGenome>();

    // Tables of new connections and neurons created during adiitive mutations. These tables
    // are available during the mutations and can be used to check for matching mutations so
    // that two mutations that create the same structure will be allocated the same ID.
    // Currently this matching is only performed within the context of a generation, which
    // is how the original C++ NEAT code operated also.
    Map<ConnectionEndpointsStruct, ConnectionGene> newConnectionGeneTable = new HashMap<ConnectionEndpointsStruct, ConnectionGene>();
    Map<Integer, NewNeuronGeneStruct> newNeuronGeneStructTable = new HashMap<Integer, NewNeuronGeneStruct>();

    // Statistics
    int generation = 0;
    IGenome bestGenome;

    /// <summary>
    /// Default Constructor.
    /// </summary>
    public EvolutionAlgorithm(Population pop, IPopulationEvaluator populationEvaluator) {
        this(pop, populationEvaluator, new NeatParameters());
    }

    /// <summary>
    /// Default Constructor.
    /// </summary>
    public EvolutionAlgorithm(Population pop, IPopulationEvaluator populationEvaluator, NeatParameters neatParameters) {
        this.pop = pop;
        this.populationEvaluator = populationEvaluator;
        this.neatParameters = neatParameters;
        neatParameters_Normal = neatParameters;

        neatParameters_PrunePhase = new NeatParameters(neatParameters);
        neatParameters_PrunePhase.pMutateAddConnection = 0.0;
        neatParameters_PrunePhase.pMutateAddNode = 0.0;
        neatParameters_PrunePhase.pMutateConnectionWeights = 0.33;
        neatParameters_PrunePhase.pMutateDeleteConnection = 0.33;
        neatParameters_PrunePhase.pMutateDeleteSimpleNeuron = 0.33;

        // Disable all crossover as this has a tendency to increase complexity, which is precisely what
        // we don't want during a pruning phase.
        neatParameters_PrunePhase.pOffspringAsexual = 1.0;
        neatParameters_PrunePhase.pOffspringSexual = 0.0;

        initialisePopulation();
    }

    public Population getPopulation() {
        return pop;
    }

    public int getNextGenomeId() {
        return pop.getIdGenerator().getNextGenomeId();
    }


    public int getNextInnovationId() {
        return pop.getIdGenerator().getNextInnovationId();
    }

    public NeatParameters getNeatParameters() {
        return neatParameters;
    }

    public IPopulationEvaluator getPopulationEvaluator() {
        return populationEvaluator;
    }

    public int getGeneration() {
        return generation;
    }

    public IGenome getBestGenome() {
        return bestGenome;
    }

    public Map<ConnectionEndpointsStruct, ConnectionGene> getNewConnectionGeneTable() {
        return newConnectionGeneTable;
    }

    public Map<Integer, NewNeuronGeneStruct> getNewNeuronGeneStructTable() {
        return newNeuronGeneStructTable;
    }

    public boolean isPruningMode() {
        return pruningMode;
    }

    /// <summary>
    /// Get/sets a boolean indicating if the search should use pruning mode.
    /// </summary>
    public boolean isPruningModeEnabled() {
        return pruningModeEnabled;
    }

    public void setPruningModeEnabled(boolean value) {
        pruningModeEnabled = value;
        if (!value) {    // Weight fixing cannot (currently) occur with pruning mode disabled.
            connectionWeightFixingEnabled = false;
        }
    }

    /// <summary>
    /// Get/sets a boolean indicating if connection weight fixing is enabled. Note that this technique
    /// is currently tied to pruning mode, therefore if pruning mode is disabled then weight fixing
    /// will automatically be disabled.
    /// </summary>
    public boolean isConnectionWeightFixingEnabled() {
        return connectionWeightFixingEnabled;
    }

    public void setConnectionWeightFixingEnabled(boolean value) {    // Ensure disabled if pruningMode is disabled.
        connectionWeightFixingEnabled = pruningModeEnabled && value;
    }

    /// <summary>
    /// Evaluate all genomes in the population, speciate them and then calculate adjusted fitness
    /// and related stats.
    /// </summary>
    /// <param name="p"></param>
    private void initialisePopulation() {
        // The GenomeFactories normally won't bother to ensure that like connections have the same ID
        // throughout the population (because it's not very easy to do in most cases). Therefore just
        // run this routine to search for like connections and ensure they have the same ID.
        // Note. This could also be done periodically as part of the search, remember though that like
        // connections occuring within a generation are already fixed - using a more efficient scheme.
        matchConnectionIds();

        // Evaluate the whole population.
        populationEvaluator.evaluatePopulation(pop, this);

        // Speciate the population.
        pop.buildSpeciesTable(this);

        // Now we have fitness scores and a speciated population we can calculate fitness stats for the
        // population as a whole and per species.
        updateFitnessStats();

        // Set new threshold 110% of current level or 10 more if current complexity is very low.
        pop.prunePhaseAvgComplexityThreshold = pop.getAvgComplexity() + neatParameters.pruningPhaseBeginComplexityThreshold;

        // Obtain an initial value for this variable that tracks when we should call pp.RedetermineSpeciation().
        meanComplexityAtLastAdjustSpeciationThreshold = pop.getAvgComplexity();

        // Now we have stats we can determine the target size of each species as determined by the
        // fitness sharing logic.
        determineSpeciesTargetSize();

        // Check integrity.
        Debug.Assert(pop.performIntegrityCheck(), "Population integrity check failed.");
    }


    public void performOneGeneration() {
        int speciesCount = pop.getSpeciesTable().size();
//        System.out.println("speciesCount = " + speciesCount);
        logger.info("speciesCount = " + speciesCount);
        //----- Elmininate any poor species before we do anything else. These are species with a zero target
        //		size for this generation and will therefore not have generate any offspring. Here we have to
        //		explicitly eliminate these species, otherwise the species would persist because of elitism.
        //		Also, the species object would persist without any genomes within it, so we have to clean it up.
        //		This code could be executed at the end of this method instead of the start, it doesn't really
        //		matter. Except that If we do it here then the population size will be relatively constant
        //		between generations.
        if (pop.eliminateSpeciesWithZeroTargetSize()) {    // If species were removed then we should recalculate population stats.
            updateFitnessStats();
            determineSpeciesTargetSize();
        }

        //----- Stage 1. Create offspring / cull old genomes / add offspring to population.
        createOffSpring();
        pop.trimAllSpeciesBackToElite();

        // Add offspring to the population.
        int genomeBound = offspringList.size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++)
            pop.addGenomeToPopulation(this, offspringList.get(genomeIdx));

        // Adjust the speciation threshold to try and keep the number of species within defined limits.
        adjustSpeciationThreshold();

        //----- Stage 2. Evaluate genomes / Update stats.
        populationEvaluator.evaluatePopulation(pop, this);
        updateFitnessStats();
        determineSpeciesTargetSize();

        pop.incrementGenomeAges();
        pop.incrementSpeciesAges();
        generation++;

        //----- Stage 3. Pruning phase tracking / Pruning phase entry & exit.
        if (pruningModeEnabled) {
            if (pruningMode) {
                // Track the falling population complexity.
                if (pop.getAvgComplexity() < prunePhase_MinimumStructuresPerGenome) {
                    prunePhase_MinimumStructuresPerGenome = pop.getAvgComplexity();
                    prunePhase_generationAtLastSimplification = generation;
                }

                if (testForPruningPhaseEnd())
                    endPruningPhase();
            } else {
                if (testForPruningPhaseBegin())
                    beginPruningPhase();
            }
        }
    }

    /// <summary>
    /// Indicates that the # of species is outside of the desired bounds and that AdjustSpeciationThreshold()
    /// is attempting to adjust the speciation threshold at each generation to remedy the situation.
    /// </summary>
    private boolean speciationThresholdAdjustInProgress = false;

    /// <summary>
    /// If speciationThresholdAdjustInProgress is true then the amount by which we are adjustinf the speciation
    /// threshol dper generation. This value is modified in order to try and find the correct threshold as quickly
    /// as possibly.
    /// </summary>
    private double compatibilityThresholdDelta;

    private static double compatibilityThresholdDeltaAcceleration = 1.05;


    private void adjustSpeciationThreshold() {
        boolean redetermineSpeciationFlag = false;
        int speciesCount = pop.getSpeciesTable().size();

        if (speciesCount < neatParameters.targetSpeciesCountMin) {
            // Too few species. Reduce the speciation threshold.
            if (speciationThresholdAdjustInProgress) {    // Adjustment is already in progress.
                if (compatibilityThresholdDelta < 0.0) {    // Negative delta. Correct direction, so just increase the delta to try and find the correct value as quickly as possible.
                    compatibilityThresholdDelta *= compatibilityThresholdDeltaAcceleration;
                } else {    // Positive delta. Incorrect direction. This means we have overshot the correct value.
                    // Reduce the delta and flip its sign.
                    compatibilityThresholdDelta *= -0.5;
                }
            } else {    // Start new adjustment 'phase'.
                speciationThresholdAdjustInProgress = true;
                compatibilityThresholdDelta = -Math.max(0.1, neatParameters.compatibilityThreshold * 0.01);
            }

            // Adjust speciation threshold by compatibilityThresholdDelta.
            neatParameters.compatibilityThreshold += compatibilityThresholdDelta;
            neatParameters.compatibilityThreshold = Math.max(0.01, neatParameters.compatibilityThreshold);

            redetermineSpeciationFlag = true;
        } else if (speciesCount > neatParameters.targetSpeciesCountMax) {
            // Too many species. Increase the species threshold.
            if (speciationThresholdAdjustInProgress) {    // Adjustment is already in progress.
                if (compatibilityThresholdDelta < 0.0) {    // Negative delta. Incorrect direction. This means we have overshot the correct value.
                    // Reduce the delta and flip its sign.
                    compatibilityThresholdDelta *= -0.5;
                } else {    // Positive delta. Correct direction, so just increase the delta to try and find the correct value as quickly as possible.
                    compatibilityThresholdDelta *= compatibilityThresholdDeltaAcceleration;
                }
            } else {    // Start new adjustment 'phase'.
                speciationThresholdAdjustInProgress = true;
                compatibilityThresholdDelta = Math.max(0.1, neatParameters.compatibilityThreshold * 0.01);
            }

            // Adjust speciation threshold by compatibilityThresholdDelta.
            neatParameters.compatibilityThreshold += compatibilityThresholdDelta;

            redetermineSpeciationFlag = true;
        } else {    // Correct # of species. Ensure flag is reset.
            speciationThresholdAdjustInProgress = false;
        }

        if (!redetermineSpeciationFlag) {
            double complexityDeltaProportion = Math.abs(pop.getAvgComplexity() - meanComplexityAtLastAdjustSpeciationThreshold) / meanComplexityAtLastAdjustSpeciationThreshold;

            if (complexityDeltaProportion > 0.05) {    // If the population's complexity has changed by more than some proportion then force a
                // call to RedetermineSpeciation().
                redetermineSpeciationFlag = true;

                // Update the tracking variable.
                meanComplexityAtLastAdjustSpeciationThreshold = pop.getAvgComplexity();
            }
        }

        if (redetermineSpeciationFlag) {
            // If the speciation threshold was adjusted then we must disregard all previous speciation
            // and rebuild the species table.
            pop.redetermineSpeciation(this);

            // If we are in a pruning phase then we should reset the pruning phase tracking variables.
            // We are effectively re-starting the pruning phase.
            prunePhase_generationAtLastSimplification = generation;
            prunePhase_MinimumStructuresPerGenome = pop.getAvgComplexity();

//            Debug.WriteLine("ad hoc RedetermineSpeciation()");
            logger.info("ad hoc RedetermineSpeciation()");
        }
    }

//		/// <summary>
//		/// Returns true if the speciation threshold was adjusted.
//		/// </summary>
//		/// <returns></returns>
//		private bool AdjustSpeciationThreshold()
//		{
//			int speciesCount = pop.SpeciesTable.Count;
//
//			if(speciesCount < neatParameters.targetSpeciesCountMin)
//			{	
//				// Too few species. Reduce the speciation threshold.
//				if(speciationThresholdAdjustInProgress)
//				{	// Adjustment is already in progress.
//					if(compatibilityThresholdDelta<0.0)
//					{	// Negative delta. Correct direction, so just increase the delta to try and find the correct value as quickly as possible.
//						compatibilityThresholdDelta*=compatibilityThresholdDeltaAcceleration;
//					}
//					else
//					{	// Positive delta. Incorrect direction. This means we have overshot the correct value.
//						// Reduce the delta and flip its sign.
//						compatibilityThresholdDelta*=-0.5;
//					}
//				}
//				else
//				{	// Start new adjustment 'phase'.
//					speciationThresholdAdjustInProgress = true;
//					compatibilityThresholdDelta = -Math.Max(0.1, neatParameters.compatibilityThreshold * 0.01);
//				}
//
//				// Adjust speciation threshold by compatibilityThresholdDelta.
//				neatParameters.compatibilityThreshold += compatibilityThresholdDelta;
//				neatParameters.compatibilityThreshold = Math.Max(0.01, neatParameters.compatibilityThreshold);
//
//				Debug.WriteLine("delta=" + compatibilityThresholdDelta);
//
//				return true;
//			}
//			else if(speciesCount > neatParameters.targetSpeciesCountMax)
//			{	
//				// Too many species. Increase the species threshold.
//				if(speciationThresholdAdjustInProgress)
//				{	// Adjustment is already in progress.
//					if(compatibilityThresholdDelta<0.0)
//					{	// Negative delta. Incorrect direction. This means we have overshot the correct value.
//						// Reduce the delta and flip its sign.
//						compatibilityThresholdDelta*=-0.5;
//					}
//					else
//					{	// Positive delta. Correct direction, so just increase the delta to try and find the correct value as quickly as possible.
//						compatibilityThresholdDelta*=compatibilityThresholdDeltaAcceleration;
//					}
//				}
//				else
//				{	// Start new adjustment 'phase'.
//					speciationThresholdAdjustInProgress = true;
//					compatibilityThresholdDelta = Math.Max(0.1, neatParameters.compatibilityThreshold * 0.01);
//				}
//
//				// Adjust speciation threshold by compatibilityThresholdDelta.
//				neatParameters.compatibilityThreshold += compatibilityThresholdDelta;
//
//				Debug.WriteLine("delta=" + compatibilityThresholdDelta);
//
//				return true;
//			}
//			else
//			{	// Correct # of species. Ensure flag is reset.
//				speciationThresholdAdjustInProgress=false;
//				return false;
//			}
//		}

//		private const double compatibilityThresholdDeltaBaseline = 0.1;
//		private const double compatibilityThresholdDeltaAcceleration = 1.5;
//
//		private double compatibilityThresholdDelta = compatibilityThresholdDeltaBaseline;
//		private bool compatibilityThresholdDeltaDirection=true;
//		
//		/// <summary>
//		/// This routine adjusts the speciation threshold so that the number of species remains between the specified upper 
//		/// and lower limits. This routine implements a momentum approach so that the rate of change in the threshold increases
//		/// if the number of species remains incorrect for consecutive invocations.
//		/// </summary>
//		private void AdjustSpeciationThreshold()
//		{
//			double newThreshold;
//
//			if(pop.SpeciesTable.Count < neatParameters.targetSpeciesCountMin)
//			{
//				newThreshold = Math.Max(compatibilityThresholdDeltaBaseline, neatParameters.compatibilityThreshold - compatibilityThresholdDelta);
//
//				// Delta acceleration.
//				if(compatibilityThresholdDeltaDirection)
//				{	// Wrong direction - Direction change. Also reset compatibilityThresholdDelta.
//					compatibilityThresholdDelta = compatibilityThresholdDeltaBaseline;
//					compatibilityThresholdDeltaDirection=false;
//				}
//				else
//				{	// Already going in the right direction. 
//					compatibilityThresholdDelta *= compatibilityThresholdDeltaAcceleration;
//				}				
//			}
//			else if(pop.SpeciesTable.Count > neatParameters.targetSpeciesCountMax)
//			{
//				newThreshold = neatParameters.compatibilityThreshold + compatibilityThresholdDelta;
//
//				// Delta acceleration.
//				if(compatibilityThresholdDeltaDirection)
//				{	// Already going in the right direction. 
//					compatibilityThresholdDelta *= compatibilityThresholdDeltaAcceleration;
//				}
//				else
//				{	// Wrong direction - Direction change. Also reset compatibilityThresholdDelta.
//					compatibilityThresholdDelta = compatibilityThresholdDeltaBaseline;
//					compatibilityThresholdDeltaDirection=true;
//				}
//			}
//			else
//			{	// Current threshold is OK. Reset compatibilityThresholdDelta in case it has 'accelerated' to a large value.
//				// This would be a bad value to start with when the threshold next needs adjustment.
//				compatibilityThresholdDelta = compatibilityThresholdDeltaBaseline;
//				return;
//			}
//
//			neatParameters.compatibilityThreshold = newThreshold;
//
//			// If the speciation threshold was adjusted then we must disregard all previous speciation 
//			// and rebuild the species table.
//			pop.RedetermineSpeciation(this);
//		}

    private void createOffSpring() {
        offspringList.clear();
        createOffSpring_Asexual();
        createOffSpring_Sexual();
    }

    private void createOffSpring_Asexual() {
        // Create a new lists so that we can track which connections/neurons have been added during this routine.
        newConnectionGeneTable.clear();
        newNeuronGeneStructTable.clear();

        //----- Repeat the reproduction per species to give each species a fair chance at reproducion.
        //		Note that for this to work for small numbers of genomes in a species we need a reproduction
        //		rate of 100% or more. This is analagous to the strategy used in NEAT.
        for (Species species : pop.getSpeciesTable().values()) {
            // Determine how many asexual offspring to create.
            // Minimum of 1. Any species with TargetSize of 0 are eliminated at the top of PerformOneGeneration(). This copes with the
            // special case where every species may calculate offspringCount to be zero and therefor we loose the entire population!
            // This can happen e.g. when each genome is allocated it's own species with TargetSize of 1.
            int offspringCount = Math.max(1, (int) Math.round((species.targetSize - species.elitistSize) * neatParameters.pOffspringAsexual));
            for (int i = 0; i < offspringCount; i++) {    // Add offspring to a seperate genomeList. We will add the offspring later to prevent corruption of the enumeration loop.
                IGenome parent = rouletteWheelSelect(species);
                IGenome offspring = parent.createOffspring_Asexual(this);
                offspring.setParentSpeciesId1(parent.getSpeciesId());
                offspringList.add(offspring);
            }
        }
//			AmalgamateInnovations();
    }

//		/// <summary>
//		/// Mutations can sometime create the same innovation more than once within a population.
//		/// If this occurs then we ensure like innovations are allocated the same innovation ID.
//		/// This is for this generation only - if the innovation occurs in a later generation we
//		/// leave it as it is.
//		/// </summary>
//		private void AmalgamateInnovations()
//		{
//			// TODO: Inefficient routine. Revise.
//			// Indicates that at least one list's order has been invalidated.
//			bool bOrderInvalidated=false;
//
//			// Check through the new NeuronGenes - and their associated connections.
//			int neuronListBound = newNeuronGeneStructList.Count;
//			for(int i=0; i<neuronListBound-1; i++)
//			{
//				for(int j=i+1; j<neuronListBound; j++)
//				{
//					NewNeuronGeneStruct neuronGeneStruct1 = (NewNeuronGeneStruct)newNeuronGeneStructList[i];
//					NewNeuronGeneStruct neuronGeneStruct2 = (NewNeuronGeneStruct)newNeuronGeneStructList[j];
//
//					if(neuronGeneStruct1.NewConnectionGene_Input.SourceNeuronId == neuronGeneStruct2.NewConnectionGene_Input.SourceNeuronId &&
//						neuronGeneStruct1.NewConnectionGene_Output.TargetNeuronId == neuronGeneStruct2.NewConnectionGene_Output.TargetNeuronId)
//					{
//						neuronGeneStruct2.NewNeuronGene.InnovationId = neuronGeneStruct1.NewNeuronGene.InnovationId;
//						neuronGeneStruct2.NewConnectionGene_Input.InnovationId = neuronGeneStruct1.NewConnectionGene_Input.InnovationId;
//						neuronGeneStruct2.NewConnectionGene_Input.TargetNeuronId = neuronGeneStruct2.NewNeuronGene.InnovationId;
//
//						neuronGeneStruct2.NewConnectionGene_Output.InnovationId = neuronGeneStruct1.NewConnectionGene_Output.InnovationId;
//						neuronGeneStruct2.NewConnectionGene_Output.SourceNeuronId = neuronGeneStruct2.NewNeuronGene.InnovationId;
//
//						// Switching innovation numbers over can cause the genes to be out of order with respect
//						// to their innovation id. This order should be maintained at all times, so we set a flag here
//						// and re-order all effected lists at the end of this method.
//						neuronGeneStruct2.OwningGenome.NeuronGeneList.OrderInvalidated = true;
//						neuronGeneStruct2.OwningGenome.ConnectionGeneList.OrderInvalidated = true;
//						bOrderInvalidated = true;
//					}
//				}
//			}
//
//			// Check through the new connections.
//			int connectionListBound = newConnectionGeneStructList.Count;
//			for(int i=0; i<connectionListBound-1; i++)
//			{
//				for(int j=i+1; j<connectionListBound; j++)
//				{
//					NewConnectionGeneStruct connectionGeneStruct1 = (NewConnectionGeneStruct)newConnectionGeneStructList[i];
//					NewConnectionGeneStruct connectionGeneStruct2 = (NewConnectionGeneStruct)newConnectionGeneStructList[j];
//
//					if(connectionGeneStruct1.NewConnectionGene.SourceNeuronId == connectionGeneStruct2.NewConnectionGene.SourceNeuronId && 
//						connectionGeneStruct1.NewConnectionGene.TargetNeuronId == connectionGeneStruct2.NewConnectionGene.TargetNeuronId)
//					{
//						connectionGeneStruct2.NewConnectionGene.InnovationId = connectionGeneStruct1.NewConnectionGene.InnovationId;
//						connectionGeneStruct2.OwningGenome.ConnectionGeneList.OrderInvalidated = true;
//						bOrderInvalidated = true;
//					}
//				}
//			}
//
//			if(bOrderInvalidated)
//			{	// Re-order all invalidated lists within the population.
//				foreach(NeatGenome.NeatGenome genome in offspringList)
//				{
//					if(genome.NeuronGeneList.OrderInvalidated)
//						genome.NeuronGeneList.SortByInnovationId();
//
//					if(genome.ConnectionGeneList.OrderInvalidated)
//						genome.ConnectionGeneList.SortByInnovationId();
//				}
//			}
//		}

    //TODO: review this routine. parent could be null?
    private void createOffSpring_Sexual() {
        //----- Repeat the reproduction per species to give each species a fair chance at reproducion.
        //		Note that for this to work for small numbers of genomes in a species we need a reproduction
        //		rate of 100% or more. This is analagous to the strategy used in NEAT.
        for (Species species : pop.getSpeciesTable().values()) {
            boolean oneMember = false;
            boolean twoMembers = false;

            if (species.getMembers().size() == 1) {
                // We can't perform sexual reproduction. To give the species a fair chance we call the asexual routine instead.
                // This keeps the proportions of genomes per species steady.
                oneMember = true;
            } else if (species.getMembers().size() == 2)
                twoMembers = true;

            // Determine how many sexual offspring to create.
            int matingCount = (int) Math.round((species.targetSize - species.elitistSize) * neatParameters.pOffspringSexual);
            for (int i = 0; i < matingCount; i++) {
                IGenome parent1;
                IGenome parent2 = null;
                IGenome offspring;

                if (Utilities.nextDouble() < neatParameters.pInterspeciesMating) {    // Inter-species mating!
                    //System.Diagnostics.Debug.WriteLine("Inter-species mating!");
                    if (oneMember)
                        parent1 = species.getMembers().get(0);
                    else
                        parent1 = rouletteWheelSelect(species);

                    // Select the 2nd parent from the whole popualtion (there is a chance that this will be an genome
                    // from this species, but that's OK).

                    int j = 0;
                    do {
                        parent2 = rouletteWheelSelect(pop);
                    }
                    while (parent1 == parent2 && j++ < 4);    // Slightly wasteful but not too bad. Limited by j.
                } else {    // Mating within the current species.
                    //System.Diagnostics.Debug.WriteLine("Mating within the current species.");
                    if (oneMember) {    // Use asexual reproduction instead.
                        offspring = species.getMembers().get(0).createOffspring_Asexual(this);
                        offspring.setParentSpeciesId1(species.speciesId);
                        offspringList.add(offspring);
                        continue;
                    }

                    if (twoMembers) {
                        offspring = species.getMembers().get(0).createOffspring_Sexual(this, species.getMembers().get(1));
                        offspring.setParentSpeciesId1(species.speciesId);
                        offspring.setParentSpeciesId2(species.speciesId);
                        offspringList.add(offspring);
                        continue;
                    }

                    parent1 = rouletteWheelSelect(species);

                    int j = 0;
                    do {
                        parent2 = rouletteWheelSelect(species);
                    }
                    while (parent1 == parent2 && j++ < 4);    // Slightly wasteful but not too bad. Limited by j.
                }

                if (parent1 != parent2) {
                    offspring = parent1.createOffspring_Sexual(this, parent2);
                    offspring.setParentSpeciesId1(parent1.getSpeciesId());
                    offspring.setParentSpeciesId2(parent2.getSpeciesId());
                    offspringList.add(offspring);
                } else {    // No mating pair could be found. Fallback to asexual reproduction to keep the population size constant.
                    offspring = parent1.createOffspring_Asexual(this);
                    offspring.setParentSpeciesId1(parent1.getSpeciesId());
                    offspringList.add(offspring);
                }
            }
        }
    }

    /// <summary>
    /// Biased select.
    /// </summary>
    /// <param name="species">Species to select from.</param>
    /// <returns></returns>
    private IGenome rouletteWheelSelect(Species species) {
        double selectValue = (Utilities.nextDouble() * species.selectionCountTotalFitness);
        double accumulator = 0.0;

        int genomeBound = species.getMembers().size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome genome = species.getMembers().get(genomeIdx);

            accumulator += genome.getFitness();
            if (selectValue <= accumulator)
                return genome;
        }
        // Should never reach here.
        return null;
    }

//		private IGenome EvenDistributionSelect(Species species)
//		{
//			return species.Members[Utilities.next(species.SelectionCount)];
//		}


    /// <summary>
    /// Biased select.
    /// </summary>
    /// <param name="species">Species to select from.</param>
    /// <returns></returns>
    private IGenome rouletteWheelSelect(Population p) {
        double selectValue = (Utilities.nextDouble() * p.getSelectionTotalFitness());
        double accumulator = 0.0;

        int genomeBound = p.getGenomeList().size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome genome = p.getGenomeList().get(genomeIdx);

            accumulator += genome.getFitness();
            if (selectValue <= accumulator)
                return genome;
        }
        // Should never reach here.
        return null;
    }

    private static GenomeComparer genomeComparer = new GenomeComparer();

    private void updateFitnessStats() {
        /// Indicates if the Candidate CullFlag has been set on any of the species in the first loop.
        boolean bCandidateCullFlag = false;
        double bestFitness = -Double.MAX_VALUE;

        //----- Reset the population fitness values
        pop.resetFitnessValues();
        pop.totalNeuronCount = 0;
        pop.totalConnectionCount = 0;

        //----- Loop through the speciesTable so that we can re-calculate fitness totals
        for (Species species : pop.getSpeciesTable().values()) {
            species.resetFitnessValues();
            species.totalNeuronCount = 0;
            species.totalConnectionCount = 0;

            // Members must be sorted so that we can calculate the fitness of the top few genomes
            // for the selection routines.
            Collections.sort(species.getMembers(), genomeComparer);

            // Keep track of the population's best genome and max fitness.
            NeatGenome genome = (NeatGenome) (species.getMembers().get(0));
            if (genome.getFitness() > bestFitness) {
                bestGenome = genome;
                bestFitness = bestGenome.getFitness();
            }

            // Track the generation number when the species improves.
            if (genome.getFitness() > species.maxFitnessEver) {
                species.maxFitnessEver = genome.getFitness();
                species.ageAtLastImprovement = species.speciesAge;
            } else if (!pruningMode && (species.speciesAge - species.ageAtLastImprovement > neatParameters.speciesDropoffAge)) {    // The species is a candidate for culling. It may be given a pardon (later) if it is a champion species.
                species.cullCandidateFlag = true;
                bCandidateCullFlag = true;
            }

            //----- Update species totals in this first loop.
            // Calculate and store the number of genomes that will be selected from.
            species.selectionCount = (int) Math.max(1.0, Math.round((double) species.getMembers().size() * neatParameters.selectionProportion));
            species.selectionCountTotalFitness = 0.0;

            int genomeBound = species.getMembers().size();
            for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
                genome = (NeatGenome) (species.getMembers().get(genomeIdx));
                Debug.Assert(genome.getFitness() >= EvolutionAlgorithm.MIN_GENOME_FITNESS, "Genome fitness must be non-zero. Use EvolutionAlgorithm.MIN_GENOME_FITNESS");
                species.totalFitness += genome.getFitness();

                if (genomeIdx < species.selectionCount)
                    species.selectionCountTotalFitness += genome.getFitness();

                species.totalNeuronCount += genome.getNeuronGeneList().size();
                species.totalConnectionCount += genome.getConnectionGeneList().size();
            }

            species.totalStructureCount = species.totalNeuronCount + species.totalConnectionCount;
        }

        // If any species have had their CullCandidateFlag set then we need to execute some extra logic
        // to ensure we don't cull a champion species if it is the only champion species.
        // If there is more than one champion species and all of them have the CullCandidateFlag set then
        // we unset the flag on one of them. Therefore we always at least one champion species in the
        // population.
        if (bCandidateCullFlag) {
            ArrayList<Species> championSpecies = new ArrayList<Species>();

            //----- 2nd loop through species. Build list of champion species.
            for (Species species : pop.getSpeciesTable().values()) {
                if (species.getMembers().get(0).getFitness() == bestFitness)
                    championSpecies.add(species);
            }
            Debug.Assert(championSpecies.size() > 0, "No champion species! There should be at least one.");

            if (championSpecies.size() == 1) {
                Species species = championSpecies.get(0);
                if (species.cullCandidateFlag == true) {
                    species.cullCandidateFlag = false;

                    // Also reset the species AgeAtLastImprovement so that it doesn't become
                    // a cull candidate every generation, which would inefficiently invoke this
                    // extra logic on every generation.
                    species.ageAtLastImprovement = species.speciesAge;
                }
            } else {    // There are multiple champion species. Check for special case where all champions
                // are cull candidates.
                boolean bAllChampionsAreCullCandidates = true; // default to true.
                for (Species species : championSpecies) {
                    if (species.cullCandidateFlag)
                        continue;

                    bAllChampionsAreCullCandidates = false;
                    break;
                }

                if (bAllChampionsAreCullCandidates) {    // Unset the flag on one of the champions at random.
                    Species champ = championSpecies.get((int) Math.floor(Utilities.nextDouble() * championSpecies.size()));
                    champ.cullCandidateFlag = false;

                    // Also reset the species AgeAtLastImprovement so that it doesn't become
                    // a cull candidate every generation, which would inefficiently invoke this
                    // extra logic on every generation.
                    champ.ageAtLastImprovement = champ.speciesAge;
                }
            }
        }

        //----- 3rd loop through species. Update remaining stats.
        for (Species species : pop.getSpeciesTable().values()) {
            final double MEAN_FITNESS_ADJUSTMENT_FACTOR = 0.01;

            if (species.cullCandidateFlag)
                species.meanFitness = (species.totalFitness / species.getMembers().size()) * MEAN_FITNESS_ADJUSTMENT_FACTOR;
            else
                species.meanFitness = species.totalFitness / species.getMembers().size();

            //----- Update population totals.
            pop.setTotalFitness(pop.getTotalFitness() + species.totalFitness);
            pop.setTotalSpeciesMeanFitness(pop.getTotalSpeciesMeanFitness() + species.meanFitness);
            pop.setSelectionTotalFitness(pop.getSelectionTotalFitness() + species.selectionCountTotalFitness);
            pop.setTotalNeuronCount(pop.getTotalNeuronCount() + species.totalNeuronCount);
            pop.setTotalConnectionCount(pop.getTotalConnectionCount() + species.totalConnectionCount);
        }

        //----- Update some population stats /averages.
        if (bestFitness > pop.getMaxFitnessEver()) {
            logger.info("UpdateStats() - bestFitness=" + bestGenome.getFitness() + ", " + bestFitness);
            pop.setMaxFitnessEver(bestGenome.getFitness());
            pop.setGenerationAtLastImprovement(this.generation);
        }

        pop.setMeanFitness(pop.getTotalFitness() / pop.getGenomeList().size());
        pop.setTotalStructureCount(pop.getTotalNeuronCount() + pop.getTotalConnectionCount());
        pop.setAvgComplexity((float) pop.getTotalStructureCount() / (float) pop.getGenomeList().size());
    }

    /// <summary>
    /// Determine the target size of each species based upon the current fitness stats. The target size
    /// is stored against each Species object.
    /// </summary>
    /// <param name="p"></param>
    private void determineSpeciesTargetSize() {
        for (Species species : pop.getSpeciesTable().values()) {
            species.targetSize = (int) Math.round((species.meanFitness / pop.getTotalSpeciesMeanFitness()) * pop.getPopulationSize());

            // Calculate how many elite genomes to keep in the next round. If this is a large number then we can only
            // keep as many genomes as we have!
            species.elitistSize = Math.min(species.getMembers().size(), (int) Math.floor(species.targetSize * neatParameters.elitismProportion));
            if (species.elitistSize == 0 && species.targetSize > 1) {    // If ElitistSize is calculated to be zero but the TargetSize non-zero then keep just one genome.
                // If the the TargetSize is 1 then we can't really do this since it would mean that no offspring would be generated.
                // So we throw away the one member and hope that the one offspring generated will be OK.
                species.elitistSize = 1;
            }
        }
    }

    /// <summary>
    /// Search for connections with the same end-points throughout the whole population and
    /// ensure that like connections have the same innovation ID.
    /// </summary>
    private void matchConnectionIds() {
        HashMap<ConnectionEndpointsStruct, Integer> connectionIdTable = new HashMap<ConnectionEndpointsStruct, Integer>();

        int genomeBound = pop.getGenomeList().size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            NeatGenome genome = (NeatGenome) pop.getGenomeList().get(genomeIdx);

            int connectionGeneBound = genome.getConnectionGeneList().size();
            for (int connectionGeneIdx = 0; connectionGeneIdx < connectionGeneBound; connectionGeneIdx++) {
                ConnectionGene connectionGene = genome.getConnectionGeneList().get(connectionGeneIdx);

                ConnectionEndpointsStruct ces = new ConnectionEndpointsStruct();
                ces.sourceNeuronId = connectionGene.getSourceNeuronId();
                ces.targetNeuronId = connectionGene.getTargetNeuronId();

                Integer existingConnectionIdObject = connectionIdTable.get(ces);
                if (existingConnectionIdObject == null) {    // No connection withthe same end-points has been registered yet, so
                    // add it to the table.
                    connectionIdTable.put(ces, connectionGene.getInnovationId());
                } else {    // This connection is already registered. Give our latest connection
                    // the same innovation ID as the one in the table.
                    connectionGene.setInnovationId(existingConnectionIdObject);
                }
            }

            // The connection genes in this genome may now be out of order. Therefore we must ensure
            // they are sorted before we continue.
            genome.getConnectionGeneList().sortByInnovationId();
        }
    }

    private boolean testForPruningPhaseBegin() {
        // Enter pruning phase if the complexity has risen beyond the specified threshold AND no gains in fitness have
        // occured for specified number of generations.
        return (pop.getAvgComplexity() > pop.getPrunePhaseAvgComplexityThreshold()) &&
                ((generation - pop.getGenerationAtLastImprovement()) >= neatParameters.pruningPhaseBeginFitnessStagnationThreshold);
    }

    private boolean testForPruningPhaseEnd() {
        // Don't expect simplification on every generation. But if nothing has happened for
        // 'pruningPhaseEndComplexityStagnationThreshold' gens then end the prune phase.
        if (generation - prunePhase_generationAtLastSimplification > neatParameters.pruningPhaseEndComplexityStagnationThreshold)
            return true;

        return false;
    }


    private void beginPruningPhase() {
        // Enter pruning phase.
        pruningMode = true;
        prunePhase_generationAtLastSimplification = generation;
        prunePhase_MinimumStructuresPerGenome = pop.getAvgComplexity();
        neatParameters = neatParameters_PrunePhase;

        // Copy the speciation threshold as this is dynamically altered during a search and we wish to maintain
        // the tracking during pruning.
        neatParameters.compatibilityThreshold = neatParameters_Normal.compatibilityThreshold;

        logger.info(">>Prune Phase<< Complexity=" + pop.getAvgComplexity());
    }

    private void endPruningPhase() {
        // Leave pruning phase.
        pruningMode = false;

        // Set new threshold 110% of current level or 10 more if current complexity is very low.
        pop.setPrunePhaseAvgComplexityThreshold(pop.getAvgComplexity() + neatParameters.pruningPhaseBeginComplexityThreshold);
        logger.info("complexity=" + pop.getAvgComplexity() + ", threshold=" + pop.getPrunePhaseAvgComplexityThreshold());

        neatParameters = neatParameters_Normal;
        neatParameters.compatibilityThreshold = neatParameters_PrunePhase.compatibilityThreshold;

        // Update species.AgaAtLastimprovement. Originally we reset this age to give all of the species
        // a 'clean slate' following the pruning phase. This though has the effect of giving all of the
        // species the same AgeAtLastImprovement - which in turn often results in all of the species
        // reaching the dropoff age simulataneously which results in the species being culled and therefore
        // causes a radical fall in population diversity.
        // Therefore we give the species a new AgeAtLastImprovement which reflects their relative
        // AgeAtLastImprovement, this gives the species a new chance following pruning but does not allocate
        // them all the same AgeAtLastImprovement.
        normalizeSpeciesAges();

        if (connectionWeightFixingEnabled) {
            // Fix all of the connection weights that remain after pruning (proven to be good values).
            for (IGenome genome : pop.getGenomeList())
                ((NeatGenome) genome).fixConnectionWeights();
        }
    }

    private void normalizeSpeciesAges() {
        float quarter_of_dropoffage = (float) neatParameters.speciesDropoffAge / 4.0F;

        // Calculate the spread of AgeAtLastImprovement - first find the min and max values.
        long minAgeAtLastImprovement;
        long maxAgeAtLastImprovement;

        minAgeAtLastImprovement = Long.MAX_VALUE;
        maxAgeAtLastImprovement = 0;

        for (Species species : pop.getSpeciesTable().values()) {
            minAgeAtLastImprovement = minAgeAtLastImprovement < species.ageAtLastImprovement ? minAgeAtLastImprovement : species.ageAtLastImprovement;
            maxAgeAtLastImprovement = maxAgeAtLastImprovement > species.ageAtLastImprovement ? maxAgeAtLastImprovement : species.ageAtLastImprovement;
        }

        long spread = maxAgeAtLastImprovement - minAgeAtLastImprovement;

        // Allocate each species a new AgeAtLastImprovement. Scale the ages so that the oldest is
        // only 25% towards the cutoff age.
        for (Species species : pop.getSpeciesTable().values()) {
            long droppOffAge = species.ageAtLastImprovement - minAgeAtLastImprovement;
            long newDropOffAge = (long) (((float) droppOffAge / (float) spread) * quarter_of_dropoffage);
            species.ageAtLastImprovement = species.speciesAge - newDropOffAge;
        }
    }

//		System.Text.StringBuilder sb = new System.Text.StringBuilder();
//		int tickCountStart;
//		int tickDuration;
//
//		private void StartMonitor()
//		{
//			tickCountStart = System.Environment.TickCount;
//		}
//
//		private void EndMonitor(string msg)
//		{
//			tickDuration =  System.Environment.TickCount - tickCountStart;
//			sb.Append(msg + " : " + tickDuration + " ms\n");
//		}
//
//		private void DumpMessage()
//		{
//			System.Windows.Forms.MessageBox.Show(sb.ToString());
//			sb = new System.Text.StringBuilder();
//		}
}
