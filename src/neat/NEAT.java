package neat;

import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;

import java.util.LinkedList;
import java.util.List;

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
public class NEAT implements IEvolutionaryAlgorithm {
    private static NEATConfig config = new NEATConfig();

    /**
     * The reference to Population
     */
    private Population population;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    /**
     * Constructs new NE object. It initializes what is needed and assigns
     * the population. Typically it would be !!!!!!!!!!!!!!!!!!!!!!!
     */
    public NEAT() {
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
        generalizationGeneration = -1;
        population.incrementGeneration();
        population.evaluate(); //obtain the fitness of all Genomes
        population.speciate(); //assign Genomes to Species, if needed create new Species
        //+ compute sharedFitness of all Genomes, penalize unimproved Species etc...
        // + estimate the amount of offspring for all Species
//        population.storeDistanceMatrix();
    }

    public void nextGeneration() {
        population.incrementGeneration();
        population.select(); //eliminate inferior genomes
        population.reproduce(); //reproduce the Population - the current Population is completely replaced by its offspring

        if (population.getGeneration() % config.clearHistoryGenerations == 0) {
            population.getGlobalInnovation().cleanHistory(); //? clear memory of innovations? after one generation ?
        }

        population.evaluate();
        population.speciate();
    }

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = population.evaluateGeneralization();
        generalizationGeneration = population.getGeneration();
    }

    public void finished() {
//        population.storeDistanceMatrix();
        population.shutdown();
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

    public List<EvaluationInfo> getEvaluationInfo() {
        return population.getEvaluationInfo();
    }

    public BasicInfo getPopulationInfo() {
        return population.getPopulationInfo();
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (population.getGeneration() != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public boolean isSolved() {
        return population.isSolved();
    }

    public List<String> getEvaluationInfoItemNames() {
        return new LinkedList<String>();
    }
}