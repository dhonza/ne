package gp;

import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;
import common.evolution.PopulationManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 9:59:06 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class GPBase<P, T extends IGPForest> implements IEvolutionaryAlgorithm, IGP<T>, Serializable {
    public static double CONSTANT_AMPLITUDE = 5.0;
    public static int MAX_GENERATIONS = 1000;
    public static int MAX_EVALUATIONS = Integer.MAX_VALUE;
    public static int POPULATION_SIZE = 100;
    public static double TARGET_FITNESS = Double.MAX_VALUE;

    public static double MUTATION_CAUCHY_PROBABILITY = 0.8;
    public static double MUTATION_CAUCHY_POWER = 0.01;

    final protected int inputs;
    final protected int outputs;
    final protected NodeCollection nodeCollection;
    final protected PopulationManager<T, P> populationManager;

    protected T[] population;
    protected T[] newPopulation;
    protected int generation;
    protected T bestOfGeneration;
    protected T bestSoFar;
    private int lastInnovation;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    public GPBase(PopulationManager<T, P> populationManager, INode[] functions, INode[] terminals) {
        this.populationManager = populationManager;
        if (populationManager == null) {//for debuging only
            this.inputs = 0;
            this.outputs = 0;
        } else {
            this.inputs = populationManager.getNumberOfInputs();
            this.outputs = populationManager.getNumberOfOutputs();
        }

        init(functions);

        this.nodeCollection = createNodeCollection(functions, terminals, populationManager.getNumberOfInputs());
    }

    abstract protected void init(INode[] functions);

    protected NodeCollection createNodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        return new NodeCollection(functions, terminals, numOfInputs);
    }

    public void initialGeneration() {
        generation = 1;
        generalizationGeneration = -1;
        lastInnovation = 0;

        createInitialGeneration();
        evaluate(population);
        recomputeBest();
    }

    public void nextGeneration() {
        generation++;
        selectAndReproduce();
        evaluate(newPopulation);
        reduce();
        recomputeBest();
    }

    abstract protected void createInitialGeneration();

    abstract protected void selectAndReproduce();

    private void evaluate(T[] evalPopulation) {
        populationManager.loadGenotypes(Arrays.asList(evalPopulation));
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;
        for (T forest : evalPopulation) {
            forest.setFitness(evaluationInfos.get(cnt).getFitness());
            forest.setEvaluationInfo(evaluationInfos.get(cnt++));
        }
    }

    protected abstract void reduce();

    private void recomputeBest() {
        bestOfGeneration = population[0];
        for (T forest : population) {
            if (forest.getFitness() > bestOfGeneration.getFitness()) {
                bestOfGeneration = forest;
            }
        }
        if (bestSoFar.getFitness() < bestOfGeneration.getFitness()) {
            bestSoFar = bestOfGeneration;
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }
    }

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationManager.evaluateGeneralization(bestSoFar);
        generalizationGeneration = generation;
    }

    public void finished() {
        populationManager.shutdown();
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return getGeneration() * population.length;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public double getMaxFitnessReached() {
        return getBestSoFar().getFitness();
    }

    public List<EvaluationInfo> getEvaluationInfo() {
        List<EvaluationInfo> infoList = new ArrayList<EvaluationInfo>(population.length);
        for (IGPForest chromosome : population) {
            infoList.add(chromosome.getEvaluationInfo());
        }
        return infoList;
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public BasicInfo getPopulationInfo() {
        return populationManager.getPopulationInfo();
    }

    public boolean isSolved() {
        return populationManager.isSolved();
    }

    abstract public String getConfigString();

    public T getBestSoFar() {
        return bestSoFar;
    }

    public T getBestOfGeneration() {
        return bestOfGeneration;
    }
}
