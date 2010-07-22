package sneat.evolution;

import common.evolution.EvaluationInfo;

public interface IPopulationEvaluator {
    /// <summary>
    /// Evaluate the genomes within the Population argument. Implementors can choose how to evaluate
    /// the genomes and which ones to evaluate, e.g. only evaluate new genomes (EvaluationCount>0).
    /// </summary>
    /// <param name="pop"></param>
    /// <param name="ea">Some evaluators may wish to interogate the current EvolutionAlgorithm to
    /// obtain statistical information. Most experiments though do not require this parameter.</param>
    void evaluatePopulation(Population pop, EvolutionAlgorithm ea);

    EvaluationInfo evaluateGeneralization(IGenome individual);

    //added by dhonza
    boolean isSolved();

    /// <summary>
    /// The total number of evaluations performed.
    /// </summary>
    long getEvaluationCount();

    /// <summary>
    /// Indicates that the current best genome is a champion at the current level of difficulty.
    /// If there is only one difficulty level then the 'SearchCompleted' flag should also be set.
    /// </summary>
    boolean getBestIsIntermediateChampion();

    /// <summary>
    /// Indicates that the best solution meets the evaluator's end criteria.
    /// </summary>
    boolean getSearchCompleted();

    void shutdown();
}
