package sneat.evolution;

import common.evolution.EvaluationInfo;

import java.util.*;

public class Population {
    IdGenerator idGenerator;
    List<IGenome> genomeList;        // The master list of genomes in the population.
    Map<Integer, Species> speciesTable;            // Asecondary structure containing all of the genomes partitioned into their respective species. A Hashtable of GenomeList structures.

    int populationSize;        // The base-line number for the population size. The actual size may vary slightly from this figure as offspring are generated and culled.
    double totalFitness;    // totalled fitness values of all genomes in the population.
    double meanFitness;
    double totalSpeciesMeanFitness;

    // The totalled fitness of the genomes that will be selected from.
    double selectionTotalFitness;

    int totalNeuronCount;
    int totalConnectionCount;
    int totalStructureCount;
    float avgComplexity;

    int nextSpeciesId = 0;

    // Some statistics.
    long generationAtLastImprovement = 0;
    double maxFitnessEver = 0.0;
//		double fitnessAtLastPrunePhaseEnd=0.0;

    float prunePhaseAvgComplexityThreshold = -1;

    public Population(IdGenerator idGenerator, List<IGenome> genomeList) {
        this.idGenerator = idGenerator;
        this.genomeList = genomeList;
        this.populationSize = genomeList.size();
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    /// <summary>
    /// The base-line number for the population size. The actual size may vary slightly from this figure as offspring are generated and culled.
    /// </summary>
    public int getPopulationSize() {
        return populationSize;
    }

    public List<IGenome> getGenomeList() {
        return genomeList;
    }

    public Map<Integer, Species> getSpeciesTable() {
        return Collections.unmodifiableMap(speciesTable);
    }

    public double getTotalFitness() {
        return totalFitness;
    }

    public void setTotalFitness(double value) {
        totalFitness = value;
    }

    public double getMeanFitness() {
        return meanFitness;
    }

    public void setMeanFitness(double value) {
        meanFitness = value;
    }

    /// <summary>
    /// The total of all of the Species.MeanFitness
    /// </summary>
    public double getTotalSpeciesMeanFitness() {
        return totalSpeciesMeanFitness;
    }

    public void setTotalSpeciesMeanFitness(double value) {
        totalSpeciesMeanFitness = value;
    }

    /// <summary>
    /// The total of all of the Species.MeanFitness
    /// </summary>
    public double getSelectionTotalFitness() {
        return selectionTotalFitness;
    }

    public void setSelectionTotalFitness(double value) {
        selectionTotalFitness = value;
    }

    public int getTotalNeuronCount() {
        return totalNeuronCount;
    }

    public void setTotalNeuronCount(int value) {
        totalNeuronCount = value;
    }

    public int getTotalConnectionCount() {
        return totalConnectionCount;
    }

    public void setTotalConnectionCount(int value) {
        totalConnectionCount = value;
    }

    /// <summary>
    /// TotalNeuronCount + TotalConnectionCount
    /// </summary>


    public int getTotalStructureCount() {
        return totalStructureCount;
    }

    public void setTotalStructureCount(int totalStructureCount) {
        this.totalStructureCount = totalStructureCount;
    }

    /// <summary>
    /// Avg Structures Per Genome.
    /// </summary>


    public float getAvgComplexity() {
        return avgComplexity;
    }

    public void setAvgComplexity(float avgComplexity) {
        this.avgComplexity = avgComplexity;
    }


    public long getGenerationAtLastImprovement() {
        return generationAtLastImprovement;
    }

    public void setGenerationAtLastImprovement(long generationAtLastImprovement) {
        this.generationAtLastImprovement = generationAtLastImprovement;
    }

//		public long GenerationAtLastPrunePhaseEnd
//		{
//			get
//			{
//				return generationAtLastPrunePhaseEnd;
//			}
//			set
//			{
//				generationAtLastPrunePhaseEnd = value;
//			}
//		}


    public double getMaxFitnessEver() {
        return maxFitnessEver;
    }

    public void setMaxFitnessEver(double maxFitnessEver) {
        this.maxFitnessEver = maxFitnessEver;
    }

//		public double FitnessAtLastPrunePhaseEnd
//		{
//			get
//			{
//				return fitnessAtLastPrunePhaseEnd;
//			}
//			set
//			{
//				fitnessAtLastPrunePhaseEnd = value;
//			}
//		}


    public float getPrunePhaseAvgComplexityThreshold() {
        return prunePhaseAvgComplexityThreshold;
    }

    public void setPrunePhaseAvgComplexityThreshold(float prunePhaseAvgComplexityThreshold) {
        this.prunePhaseAvgComplexityThreshold = prunePhaseAvgComplexityThreshold;
    }

    public void resetFitnessValues() {
        totalFitness = 0.0;
        meanFitness = 0.0;
        totalSpeciesMeanFitness = 0.0;
        selectionTotalFitness = 0.0;
    }

    public void addGenomeToPopulation(EvolutionAlgorithm ea, IGenome genome) {
        //----- Add genome to the master list of genomes.
        genomeList.add(genome);

        //----- Determine it's species and insert into the speciestable.
        addGenomeToSpeciesTable(ea, genome);
    }

    /// <summary>
    /// Determine the species of each genome in genomeList and build the 'species' Hashtable.
    /// </summary>
    public void buildSpeciesTable(EvolutionAlgorithm ea) {
        //----- Build the table.
        speciesTable = new HashMap<Integer, Species>();

        // First pass. Genomes that already have an assigned species.

        //foreach(IGenome genome in genomeList)
        int genomeIdx;
        int genomeBound = genomeList.size();
        for (genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome genome = genomeList.get(genomeIdx);
            if (genome.getSpeciesId() != -1)
                addGenomeToSpeciesTable(ea, genome);
        }

        // Second pass. New genomes. Performing two passes ensures we preserve the species IDs.
        for (genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome genome = genomeList.get(genomeIdx);
            if (genome.getSpeciesId() == -1)
                addGenomeToSpeciesTable(ea, genome);
        }
    }

    public void redetermineSpeciation(EvolutionAlgorithm ea) {
        // Keep a reference to the old species table.
        Map<Integer, Species> oldSpeciesTable = speciesTable;

        // Remove the gnomes from the old species objects. Note that the genome's can still be
        // accessed via 'genomeList' and that they still contain the old speciesId.
        for (Species species : oldSpeciesTable.values())
            species.getMembers().clear();

        // Create a new species table.
        speciesTable = new HashMap<Integer, Species>();

        // Loop through all of the genomes and place them into the new species table.
        // Use the overload for AddGenomeToSpeciesTable() that re-uses the old species
        // objects instead of creating new species.
        int genomeBound = genomeList.size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome genome = genomeList.get(genomeIdx);
            Species oldSpecies = oldSpeciesTable.get(genome.getSpeciesId());
            addGenomeToSpeciesTable(ea, genome, oldSpecies);
        }


//			speciesTable.Clear();
//			
//			int genomeBound = genomeList.Count;
//			for(int genomeIdx=0; genomeIdx<genomeBound; genomeIdx++)
//			{
//				IGenome genome = genomeList[genomeIdx];
//
//				genome.SpeciesId = -1;
//				genome.ParentSpeciesId1=-1;
//				genome.ParentSpeciesId2=-1;
//				AddGenomeToSpeciesTable(ea, genome);
//			}
    }


//		public void RedetermineSpeciation(EvolutionAlgorithm ea)
//		{
//			speciesTable.Clear();
//			
//			int genomeBound = genomeList.Count;
//			for(int genomeIdx=0; genomeIdx<genomeBound; genomeIdx++)
//			{
//				IGenome genome = genomeList[genomeIdx];
//
//				genome.SpeciesId = -1;
//				genome.ParentSpeciesId1=-1;
//				genome.ParentSpeciesId2=-1;
//				AddGenomeToSpeciesTable(ea, genome);
//			}
//		}

    ArrayList<Integer> speciesToRemove = new ArrayList<Integer>();

    public boolean eliminateSpeciesWithZeroTargetSize() {
        // Ensure helper table is empty before we start.
        speciesToRemove.clear();

        // Store a reference to all species that we need to remove. We cannot remove right
        // away as we would be modifying the structrue we are looping through.
        for (Species species : speciesTable.values()) {
            if (species.targetSize == 0)
                speciesToRemove.add(species.speciesId);
        }

        // Remove the poor species.
        int bound = speciesToRemove.size();
        //foreach(int speciesId in speciesToRemove)
        for (int i = 0; i < bound; i++)
            speciesTable.remove(speciesToRemove.get(i));

        // If species were removed then rebuild the master GenomeList.
        boolean bSpeciesRemoved;
        if (speciesToRemove.size() > 0) {
            bSpeciesRemoved = true;
            rebuildGenomeList();
        } else {
            bSpeciesRemoved = false;
        }

        if (bSpeciesRemoved)
            speciesToRemove.clear();

        return bSpeciesRemoved;
    }

    public void trimAllSpeciesBackToElite() {
        speciesToRemove.clear();
        for (Species species : speciesTable.values()) {
            if (species.elitistSize == 0) {    // Remove the entire species.
                speciesToRemove.add(species.speciesId);
            } else {    // Remove genomes from the species.
                int delta = species.getMembers().size() - species.elitistSize;
//                species.getMembers().RemoveRange(species.elitistSize, delta);
                for (int i = 0; i < delta; i++) {
                    species.getMembers().remove(species.elitistSize);
                }
            }
        }
        //foreach(int speciesId in speciesToRemove)
        int speciesBound = speciesToRemove.size();
        for (int speciesIdx = 0; speciesIdx < speciesBound; speciesIdx++)
            speciesTable.remove(speciesToRemove.get(speciesIdx));

        rebuildGenomeList();
    }

    /// <summary>
    /// Rebuild GenomeList from the genomes held in the speciesTable.
    /// Quite useful to keep the list up-to-date after a species has been deleted.
    /// </summary>
    public void rebuildGenomeList() {
        genomeList.clear();
        for (Species species : speciesTable.values()) {
            //foreach(IGenome genome in species.Members)
            int genomeBound = species.getMembers().size();
            for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++)
                genomeList.add(species.getMembers().get(genomeIdx));
        }
    }

    /// <summary>
    /// Some(most) types of network have fixed numbers of input and output nodes and will not work correctly or
    /// throw an exception if we try and use inputs/outputs that do not exist. This method allows us to check
    /// compatibility of the current populations genomes before we try to use them.
    /// </summary>
    /// <param name="inputCount"></param>
    /// <param name="outputCount"></param>
    /// <returns></returns>
    public boolean isCompatibleWithNetwork(int inputCount, int outputCount) {
        for (IGenome genome : genomeList) {
            if (!genome.isCompatibleWithNetwork(inputCount, outputCount))
                return false;
        }
        return true;
    }

    public void incrementGenomeAges() {
        int genomeBound = genomeList.size();
        for (int genomeIdx = 0; genomeIdx < genomeBound; genomeIdx++) {
            IGenome g = genomeList.get(genomeIdx);
            g.setGenomeAge(g.getGenomeAge() + 1);
        }
    }

    public void incrementSpeciesAges() {
        for (Species species : speciesTable.values()) {
            species.speciesAge++;
        }
    }

    /// <summary>
    /// For debug purposes only.
    /// </summary>
    /// <returns>Returns true if population integrity checks out OK.</returns>
    public boolean performIntegrityCheck() {
        for (IGenome genome : genomeList) {
            if (!genome.performIntegrityCheck())
                return false;
        }
        return true;
    }

    private void addGenomeToSpeciesTable(EvolutionAlgorithm ea, IGenome genome) {
        Species species = determineSpecies(ea, genome);
        if (species == null) {
            species = new Species();

            // Completely new species. Generate a speciesID.
            species.speciesId = nextSpeciesId++;
            speciesTable.put(species.speciesId, species);
        }

        //----- The genome is a member of an existing species.
        genome.setSpeciesId(species.speciesId);
        species.getMembers().add(genome);
    }

    /// <summary>
    /// This version of AddGenomeToSpeciesTable is used by RedetermineSpeciation(). It allows us to
    /// pass in the genome's original species object, which we can then re-use if the genome does not
    /// match any of our existing species and needs to be placed into a new species of it's own.
    /// The old species object can be used directly because it should already have already had all of
    /// its genome sremoved by RedetermineSpeciation() before being passed in here.
    /// </summary>
    /// <param name="ea"></param>
    /// <param name="genome"></param>
    /// <param name="originalSpecies"></param>
    private void addGenomeToSpeciesTable(EvolutionAlgorithm ea, IGenome genome, Species originalSpecies) {
        Species species = determineSpecies(ea, genome);
        if (species == null) {
            // The genome is not in one of the existing (new) species. Is this genome's old
            // species already in the new table?
            species = speciesTable.get(genome.getSpeciesId());
            if (species != null) {
                // The genomes old species is already in the table but the genome no longer fits into that
                // species. Therefore we need to create an entirely new species.
                species = new Species();
                species.speciesId = nextSpeciesId++;
            } else {
                // We can re-use the oldSpecies object.
                species = originalSpecies;
            }
            speciesTable.put(species.speciesId, species);
        }

        //----- The genome is a member of an existing species.
        genome.setSpeciesId(species.speciesId);
        species.getMembers().add(genome);
    }


    /// <summary>
    /// Determine the given genome's species and return that species. If the genome does not
    /// match one of the existing species then we return null to indicate a new species.
    /// </summary>
    /// <param name="genome"></param>
    /// <returns></returns>
    private Species determineSpecies(EvolutionAlgorithm ea, IGenome genome) {
        //----- Performance optimization. Check against parent species IDs first.
        Species parentSpecies1 = null;
        Species parentSpecies2 = null;

        // Parent1. Not set in initial population.
        if (genome.getParentSpeciesId1() != -1) {
            parentSpecies1 = speciesTable.get(genome.getParentSpeciesId1());
            if (parentSpecies1 != null) {
                if (IsGenomeInSpecies(genome, parentSpecies1, ea))
                    return parentSpecies1;
            }
        }

        // Parent2. Not set if result of asexual reproduction.
        if (genome.getParentSpeciesId2() != -1) {
            parentSpecies2 = speciesTable.get(genome.getParentSpeciesId2());
            if (parentSpecies2 != null) {
                if (IsGenomeInSpecies(genome, parentSpecies2, ea))
                    return parentSpecies2;
            }
        }

        //----- Not in parent species. Systematically compare against all species.
        for (Species compareWithSpecies : speciesTable.values()) {
            // Don't compare against the parent species again.
            if (compareWithSpecies == parentSpecies1 || compareWithSpecies == parentSpecies2)
                continue;

            if (IsGenomeInSpecies(genome, compareWithSpecies, ea)) {    // We have found matching species.
                return compareWithSpecies;
            }
        }

        //----- The genome is not a member of any existing species.
        return null;
    }

    private boolean IsGenomeInSpecies(IGenome genome, Species compareWithSpecies, EvolutionAlgorithm ea) {
//			// Pick a member of the species at random.
//			IGenome compareWithGenome = compareWithSpecies.Members[(int)Math.Floor(compareWithSpecies.Members.Count * Utilities.nextDouble())];
//			return (genome.CalculateCompatibility(compareWithGenome, ea.NeatParameters) < ea.NeatParameters.compatibilityThreshold);

        // Compare against the species champ. The species champ is the exemplar that represents the species.
        IGenome compareWithGenome = compareWithSpecies.getMembers().get(0);
        //IGenome compareWithGenome = compareWithSpecies.Members[(int)Math.Floor(compareWithSpecies.Members.Count * Utilities.nextDouble())];
        return genome.isCompatibleWithGenome(compareWithGenome, ea.getNeatParameters());
    }

    public List<EvaluationInfo> getEvaluationInfo() {
        List<EvaluationInfo> infoList = new ArrayList<EvaluationInfo>(genomeList.size());
        for (IGenome genome : genomeList) {
            infoList.add(genome.getEvaluationInfo());
        }
        return infoList;
    }
}
