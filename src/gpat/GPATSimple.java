package gpat;

import common.RND;
import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.PopulationManager;
import gp.GP;
import gp.IGPForest;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 6, 2010
 * Time: 12:10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPATSimple<P> implements IGPAT {
    public static int ELITE_NUMBER = 1;

    final protected int inputs;
    final protected int outputs;
    final protected ATNodeCollection nodeCollection;
    final protected PopulationManager<ATForest, P> populationManager;

    protected ATForest[] population;
    protected ATForest[] newPopulation;
    protected int generation;
    protected ATForest bestOfGeneration;
    protected ATForest bestSoFar;
    private ATInnovationHistory innovationHistory;
    private int lastInnovation;

    private int maxSpecieId = 0;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    protected Map<String, Long> origins;

    public GPATSimple(PopulationManager<ATForest, P> populationManager, ATNodeImpl[] functions, ATNodeImpl[] terminals) {
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
        this.innovationHistory = new ATInnovationHistory(this.inputs + terminals.length + 1);
        bestOfGeneration = bestSoFar = ATForest.createEmpty();
    }

    protected ATNodeCollection createNodeCollection(ATNodeImpl[] functions, ATNodeImpl[] terminals, int numOfInputs) {
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
        population = new ATForest[GP.POPULATION_SIZE];
        newPopulation = new ATForest[GP.POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = ATForest.createRandom(generation, inputs, outputs, nodeCollection, innovationHistory);
        }
    }

    protected void selectAndReproduce() {
//        System.arraycopy(population, 0, newPopulation, 0, population.length);
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
            EvaluationInfo oldInfo = evalPopulation[cnt].getEvaluationInfo();
            EvaluationInfo newInfo = evaluationInfos.get(cnt++);
            EvaluationInfo mixedInfo = EvaluationInfo.mixTwo(oldInfo, newInfo);

            forest.setFitness(mixedInfo.getFitness());
//            System.out.println(cnt + ": " + forest);

            forest.setEvaluationInfo(mixedInfo);
            saveOrigin(forest);
        }
//        System.out.println(innovationHistory);
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
//        reduceGenerational();
        reduceReplaceOld();
    }

    private void reduceGenerational() {
        ATForest[] oldAndNewPopulation = new ATForest[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }

    private void reduceReplaceOld() {
        int elite = ELITE_NUMBER;
        //TODO in first generation the population is unsorted.

        System.arraycopy(newPopulation, 0, population, elite, population.length - elite);
        Arrays.sort(population);
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
        s.append("CONSTANT_AMPLITUDE = ").append(GP.CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(GP.MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(GP.MAX_EVALUATIONS);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(GP.MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(GP.MUTATION_CAUCHY_POWER);
        s.append("\nPOPULATION_SIZE = ").append(GP.POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(GP.TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }

    public ATForest getBestSoFar() {
        return bestSoFar;
    }

    public ATForest getBestOfGeneration() {
        return bestOfGeneration;
    }

    public List<ATForest> getLastGenerationPopulation() {
        return Arrays.asList(population);
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> l = new LinkedList<String>();
        l.add("G_NODE_NUM");
        l.add("G_CONST_NUM");
        l.add("G_MAX_DEPTH");
        return l;
    }
}
