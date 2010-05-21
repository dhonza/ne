package sneat.evolution;

import sneat.neuralnetwork.AbstractNetwork;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;

/// <summary>
/// An interface for describing a generic genome.
/// IComparable must be implemented in contravention of the docs. So that ArrayList.Sort() will sort into descending order.

/// This interface may be discarded since the development of SharpNEAT has seen the EvolutionAlgorithm become more
/// closely coupled with the NeatGenome, thus making this interfaces abstraction unmaintainable.
/// </summary>
public interface IGenome extends Comparable {
    /// <summary>
    /// Some(most) types of network have fixed numbers of input and output nodes and will not work correctly or
    /// throw an exception if we try and use inputs/outputs that do not exist. This method allows us to check
    /// compatibility before we begin.
    /// </summary>
    /// <param name="inputCount"></param>
    /// <param name="outputCount"></param>
    /// <returns></returns>
    boolean isCompatibleWithNetwork(int inputCount, int outputCount);

    /// <summary>
    /// Asexual reproduction with built in mutation.
    /// </summary>
    /// <returns></returns>
    IGenome createOffspring_Asexual(EvolutionAlgorithm ea);

    /// <summary>
    /// Sexual reproduction. No mutation performed.
    /// </summary>
    /// <param name="parent"></param>
    /// <returns></returns>
    IGenome createOffspring_Sexual(EvolutionAlgorithm ea, IGenome parent);

    /// <summary>
    /// The globally unique ID for this genome (within the context of a search).
    /// </summary>
    int getGenomeId();

    /// <summary>
    /// The number of generations that this genome has existed. Note that to
    /// survive a generation a genome must be one of the elite that are preserved
    /// between generations.
    /// </summary>
    long getGenomeAge();

    void setGenomeAge(long value);

    /// <summary>
    /// This genome's fitness as calculated by the evaluation environment.
    /// </summary>
    double getFitness();

    void setFitness(double fitness);

    /// <summary>
    /// The number of times this genome has been evaluated.
    /// </summary>
    long getEvaluationCount();

    void setEvaluationCount(long count);

    /// <summary>
    /// Returns the total of all fitness scores if this genome has been evaluated more than once.
    /// Average fitness is therefore this figure divided by EvaluationCount.
    /// </summary>
    double getTotalFitness();

    void setTotalFitness(double fitness);

    /// <summary>
    /// The species this genome is within.
    /// </summary>
    int getSpeciesId();

    void setSpeciesId(int id);

    /// <summary>
    /// The ID of this genome's first parent.
    /// </summary>
    int getParentSpeciesId1();

    void setParentSpeciesId1(int id);

    /// <summary>
    /// The ID of this genome's second parent. -1 if no second parent.
    /// </summary>
    int getParentSpeciesId2();

    void setParentSpeciesId2(int id);

    AbstractNetwork getAbstractNetwork();

    /// <summary>
    /// An object reference that can be used by IPopulationEvaluator objects to
    /// store evaluation state information against a genome. E.g. If we have a growing
    /// list of test cases as evolution progresses then we could store the index of the
    /// last test case to be evaluated against. We can then skip over these test cases
    /// in subsequent evaluations of this genome.
    /// </summary>
    Object getTag();

    void setTag(Object tag);

    /// <summary>
    /// Decode the genome's 'DNA' into a working network.
    /// </summary>
    /// <returns></returns>
    INetwork decode(IActivationFunction activationFn);

    /// <summary>
    /// Clone this genome.
    /// </summary>
    /// <returns></returns>
    IGenome clone(EvolutionAlgorithm ea);

    /// <summary>
    /// Compare this IGenome with the provided one. They are compatible (determined to be in
    /// the same species) if their calculated difference is below the current threshold specified
    /// by NeatParameters.compatibilityThreshold
    /// </summary>
    /// <param name="comparisonGenome"></param>
    /// <param name="neatParameters"></param>
    /// <returns></returns>
    boolean isCompatibleWithGenome(IGenome comparisonGenome, NeatParameters neatParameters);

    /// <summary>
    /// Used primarily to give this IGenome a hook onto the Population it is within.
    /// </summary>
    Population getOwningPopulation();

    void setOwningPopulation(Population pop);

    /// <summary>
    /// Persist to XML.
    /// </summary>
    /// <param name="parentNode"></param>
    //TODO X XML
//    void Write(XmlNode parentNode);


    /// <summary>
    /// For debug purposes only.
    /// </summary>
    /// <returns>Returns true if genome integrity checks out OK.</returns>
    boolean performIntegrityCheck();
}
