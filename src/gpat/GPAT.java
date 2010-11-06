package gpat;

import common.RND;
import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;
import common.evolution.PopulationManager;
import gp.IGP;
import gp.IGPForest;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 6, 2010
 * Time: 12:10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPAT<P> implements IEvolutionaryAlgorithm, IGP<ATForest> {
    public static double CONSTANT_AMPLITUDE = 5.0;
    public static int MAX_GENERATIONS = 1000;
    public static int MAX_EVALUATIONS = Integer.MAX_VALUE;
    public static int POPULATION_SIZE = 100;
    public static double TARGET_FITNESS = Double.MAX_VALUE;

    public static double MUTATION_CAUCHY_PROBABILITY = 0.8;
    public static double MUTATION_CAUCHY_POWER = 0.01;
    public static double MUTATION_ADD_LINK = 0.01;
    public static double MUTATION_ADD_NODE = 0.01;
    public static double MUTATION_SWITCH_CONSTANT_LOCK = 0.01;

    final protected int inputs;
    final protected int outputs;
    final protected ATNodeCollection nodeCollection;
    final protected PopulationManager<ATForest, P> populationManager;

    protected ATForest[] population;
    protected ATForest[] newPopulation;
    protected int generation;
    protected ATForest bestOfGeneration;
    protected ATForest bestSoFar;
    private int lastInnovation;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    protected Map<String, Long> origins;

    public GPAT(PopulationManager<ATForest, P> populationManager, ATNode[] functions, ATNode[] terminals) {
        this.populationManager = populationManager;
        if (populationManager == null) {//for debuging only
            this.inputs = 0;
            this.outputs = 0;
        } else {
            this.inputs = populationManager.getNumberOfInputs();
            this.outputs = populationManager.getNumberOfOutputs();
        }

        this.nodeCollection = createNodeCollection(functions, terminals, populationManager.getNumberOfInputs());
        this.origins = new HashMap<String, Long>();
        bestOfGeneration = bestSoFar = ATForest.createEmpty();
    }

    protected ATNodeCollection createNodeCollection(ATNode[] functions, ATNode[] terminals, int numOfInputs) {
        return new ATNodeCollection(functions, terminals, numOfInputs);
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

    protected void createInitialGeneration() {
        population = new ATForest[POPULATION_SIZE];
        newPopulation = new ATForest[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = ATForest.createRandom(generation, inputs, outputs, nodeCollection);
        }
    }

    protected void selectAndReproduce() {
        System.arraycopy(population, 0, newPopulation, 0, population.length);
        for (int i = 0; i < newPopulation.length; i++) {
            ATForest p1 = population[RND.getInt(0, population.length - 1)];
            ATForest p2 = population[RND.getInt(0, population.length - 1)];
            ATForest p = p1.getFitness() > p2.getFitness() ? p1 : p2;
            newPopulation[i] = p.mutate(generation);
        }
    }

    private void evaluate(ATForest[] evalPopulation) {
        populationManager.loadGenotypes(Arrays.asList(evalPopulation));
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;

        origins.clear();
        for (ATForest forest : evalPopulation) {
            forest.setFitness(evaluationInfos.get(cnt).getFitness());
            forest.setEvaluationInfo(evaluationInfos.get(cnt++));
            saveOrigin(forest);
        }
    }

    protected void saveOrigin(IGPForest forest) {
        for (String origin : forest.getOrigins()) {
            if (!origins.containsKey(origin)) {
                origins.put(origin, 1L);
            } else {
                origins.put(origin, origins.get(origin) + 1L);
            }
        }
    }

    protected void reduce() {
        ATForest[] oldAndNewPopulation = new ATForest[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }

    private void recomputeBest() {
        bestOfGeneration = population[0];
        for (ATForest forest : population) {
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
        BasicInfo infoMap = populationManager.getPopulationInfo();
        for (String origin : origins.keySet()) {
            infoMap.put("O_" + origin, origins.get(origin));
        }
        return infoMap;
    }

    public boolean isSolved() {
        return populationManager.isSolved();
    }

    public String getConfigString() {
        StringBuilder s = new StringBuilder();
        s.append("CONSTANT_AMPLITUDE = ").append(CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(MAX_EVALUATIONS);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(MUTATION_CAUCHY_POWER);
        s.append("\nPOPULATION_SIZE = ").append(POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }

    public ATForest getBestSoFar() {
        return bestSoFar;
    }

    public ATForest getBestOfGeneration() {
        return bestOfGeneration;
    }
}
