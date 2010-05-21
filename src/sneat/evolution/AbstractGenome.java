package sneat.evolution;

import sneat.neuralnetwork.AbstractNetwork;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.utilityclasses.Debug;

abstract public class AbstractGenome implements IGenome {
    // See comments on individual properties for more information on these fields.
    protected int genomeId;
    long genomeAge = 0;
    double fitness = 0;
    long evaluationCount = 0;
    double totalFitness = 0;
    int speciesId = -1;
    int parentSpeciesId1 = -1;
    int parentSpeciesId2 = -1;
    Population owningPopulation;

    // Stores the decoded network. Storing this prevents the need to re-decode genomes during
    // experiments where the same genome may be evaluated multiple times, e.g. re-evaluation
    // per generation because of a non-deterministic evaluation function, or a deterministic
    // function that is changing as the search progresses.
    // If it can be cast to AbstractNetwork then this can also form the basis of constructing a
    // NetworkModel for network visualization.
    protected INetwork network = null;

    /// <summary>
    /// A tag object that can be used by evaluators to store evaluation state information. This isn't
    /// normally used. An example usage is the ParetoCoEv Tic-Tac-Toe evaluator which uses this to store
    /// an integer which gives the index of the last entry in the pareto chain to have been evaluated against.
    /// Thus we only have to evaluate against later entries which elimintates a large number of redundant evaluations.
    /// </summary>
    Object tag;

    /// <summary>
    /// Implemented in contravention of the .net documentation. ArrayList.Sort() will sort into descending order.
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public int compareTo(Object obj) {
        if (((IGenome) obj).getFitness() > fitness)
            return 1;

        if (((IGenome) obj).getFitness() < fitness)
            return -1;

        return 0;
    }

    /// <summary>
    /// Some(most) types of network have fixed numbers of input and output nodes and will not work correctly or
    /// throw an exception if we try and use inputs/outputs that do not exist. This method allows us to check
    /// compatibility before we begin.
    /// </summary>
    /// <param name="inputCount"></param>
    /// <param name="outputCount"></param>
    /// <returns></returns>
    abstract public boolean isCompatibleWithNetwork(int inputCount, int outputCount);

    /// <summary>
    /// Asexual reproduction with built in mutation.
    /// </summary>
    /// <returns></returns>
    abstract public IGenome createOffspring_Asexual(EvolutionAlgorithm ea);

    /// <summary>
    /// Sexual reproduction. No mutation performed.
    /// </summary>
    /// <param name="parent"></param>
    /// <returns></returns>
    abstract public IGenome createOffspring_Sexual(EvolutionAlgorithm ea, IGenome parent);

    /// <summary>
    /// Decode the genome's 'DNA' into a working network.
    /// </summary>
    /// <returns></returns>
    abstract public INetwork decode(IActivationFunction activationFn);

    /// <summary>
    /// Clone this genome.
    /// </summary>
    /// <returns></returns>
    abstract public IGenome clone(EvolutionAlgorithm ea);

    /// <summary>
    /// Compare this IGenome with the provided one. They are compatibile if their calculated difference
    /// is below the current threshold specified by NeatParameters.compatibilityThreshold
    /// </summary>
    /// <param name="comparisonGenome"></param>
    /// <param name="neatParameters"></param>
    /// <returns></returns>
    abstract public boolean isCompatibleWithGenome(IGenome comparisonGenome, NeatParameters neatParameters);

    /// <summary>
    /// Persist to XML.
    /// </summary>
    /// <param name="parentNode"></param>
    //TODO X XML
//    abstract public void Write(XmlNode parentNode);

    /// <summary>
    /// For debug purposes only.
    /// </summary>
    /// <returns>Returns true if genome integrity checks out OK.</returns>
    abstract public boolean performIntegrityCheck();

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getGenomeId() {
        return genomeId;
    }

    public void setGenomeId(int genomeId) {
        this.genomeId = genomeId;
    }

    public long getGenomeAge() {
        return genomeAge;
    }

    public void setGenomeAge(long genomeAge) {
        this.genomeAge = genomeAge;
    }

    /// <summary>
    /// This genome's fitness as calculated by the evaluation environment.
    /// </summary>


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        Debug.Assert(fitness >= EvolutionAlgorithm.MIN_GENOME_FITNESS, "Genome fitness must be non-zero. Use EvolutionAlgorithm.MIN_GENOME_FITNESS");
        this.fitness = fitness;
    }

    /// The number of times this genome has been evaluated.
    /// </summary>
    public long getEvaluationCount() {
        return evaluationCount;
    }/// <summary>

    public void setEvaluationCount(long evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    /// <summary>
    /// Returns the total of all fitness scores if this genome has been evaluated more than once.
    /// Average fitness is therefore this figure divided by GenomeAge.
    /// </summary>

    public double getTotalFitness() {
        return totalFitness;
    }

    public void setTotalFitness(double totalFitness) {
        this.totalFitness = totalFitness;
    }

    /// <summary>
    /// The species this genome is within.
    /// </summary>
    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    /// <summary>
    /// The ID of this genome's first parent.
    /// </summary>

    public int getParentSpeciesId1() {
        return parentSpeciesId1;
    }

    public void setParentSpeciesId1(int parentSpeciesId1) {
        this.parentSpeciesId1 = parentSpeciesId1;
    }

    /// <summary>
    /// The ID of this genome's second parent. -1 if no second parent.
    /// </summary>

    public int getParentSpeciesId2() {
        return parentSpeciesId2;
    }

    public void setParentSpeciesId2(int parentSpeciesId2) {
        this.parentSpeciesId2 = parentSpeciesId2;
    }

    public AbstractNetwork getAbstractNetwork() {
        if (network instanceof AbstractNetwork) {
            return (AbstractNetwork) network;
        } else {
            return null;
        }
    }

    /// <summary>
    /// Used primarily to give this IGenome a hook onto the Population it is within.
    /// </summary>

    public Population getOwningPopulation() {
        return owningPopulation;
    }

    public void setOwningPopulation(Population owningPopulation) {
        this.owningPopulation = owningPopulation;
    }
}
