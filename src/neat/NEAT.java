package neat;

import common.evolution.EvolutionaryAlgorithm;

/**
 * <p>Title: ne</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class is the main neuro-evolution class.
 * It includes:</br>
 * - method for running the main evolution loop,</br>
 * - method for computation of the termination-condition,</br>
 * - all parameters affecting neuro-evolution.
 */
public class NEAT implements EvolutionaryAlgorithm {
    private static NEATConfig config = new NEATConfig();
    private boolean evaluateAll;

    /**
     * The reference to Population
     */
    private Population population;

    /**
     * Constructs new NE object. It initializes what is needed and assigns
     * the population. Typically it would be !!!!!!!!!!!!!!!!!!!!!!!
     */
    public NEAT() {
        this(false);
    }

//  Population can be evaluated in two possible ways: 1. (false) All Genomes separately, which is usual.
//  2. (true) The whole population of Genomes together, which is usefull for co-evolutionary tasks.

    public NEAT(boolean evaluateAll) {
        this.evaluateAll = evaluateAll;
    }

    public static NEATConfig getConfig() {
        return config;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public void initialGeneration() {
        population.incrementGeneration();
        population.evaluate(evaluateAll); //obtain the fitness of all Genomes
        population.speciate(); //assign Genomes to Species, if needed create new Species
        //+ compute sharedFitness of all Genomes, penalize unimproved Species etc...
        // + estimate the amount of offspring for all Species
//        population.storeDistanceMatrix();
    }

    public void nextGeneration() {
        population.incrementGeneration();
        population.select(); //eliminate inferior genomes
        population.reproduce(evaluateAll); //reproduce the Population - the current Population is completely replaced by its offspring

        if (population.getGeneration() % config.clearHistoryGenerations == 0) {
            population.getGlobalInnovation().cleanHistory(); //? clear memory of innovations? after one generation ?
        }

        population.evaluate(evaluateAll);
        population.speciate();
    }

    public void finished() {
//        population.storeDistanceMatrix();
    }

    public String getConfigString() {
        return getConfig().toString();
    }

    public boolean hasImproved() {
        return population.getLastInnovation() == 0;
    }

    public int getGeneration() {
        return population.getGeneration();
    }

    public int getEvaluations() {
        return population.getEvaluations();
    }

    public int getLastInnovation() {
        return population.getLastInnovation();
    }

    public double getMaxFitnessReached() {
        return population.getBestSoFar().getFitness();
    }

    public double[] getFitnessVector() {
        return population.getFitnessVector();
    }

    public boolean isSolved() {
        return population.evaluator.isSolved();
    }
}