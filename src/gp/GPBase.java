package gp;

import common.evolution.*;
import common.pmatrix.ParameterCombination;

import java.io.Serializable;
import java.util.*;

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

    public static boolean FULL_INIT = false;

    final protected int inputs;
    final protected int outputs;
    final protected NodeCollection nodeCollection;
    final protected ParameterCombination parameters;
    final protected PopulationManager<T, P> populationManager;

    final protected String initialGenome;

    protected T[] population;
    protected T[] newPopulation;
    protected int generation;
    protected T bestOfGeneration;
    protected T bestSoFar;
    private int lastInnovation;
    private int generationOfBSF;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    protected Map<String, Long> origins;

    public GPBase(ParameterCombination parameters, PopulationManager<T, P> populationManager, INode[] functions, INode[] terminals, String initialGenome) {
        this.parameters = parameters;
        this.populationManager = populationManager;
        this.initialGenome = initialGenome;
        if (populationManager == null) {//for debuging only
            this.inputs = 0;
            this.outputs = 0;
        } else {
            this.inputs = populationManager.getNumberOfInputs();
            this.outputs = populationManager.getNumberOfOutputs();
        }

        init(functions);

        this.nodeCollection = createNodeCollection(functions, terminals, populationManager.getNumberOfInputs());
        this.origins = new HashMap<String, Long>();
    }

    abstract protected void init(INode[] functions);

    protected NodeCollection createNodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        return new NodeCollection(functions, terminals, numOfInputs);
    }

    public void initialGeneration() {
        generation = 1;
        generalizationGeneration = -1;
        lastInnovation = 0;
        generationOfBSF = generation;

        GenomeCounter.INSTANCE.reset();
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

    protected void evaluate(T[] evalPopulation) {
        populationManager.loadGenotypes(Arrays.asList(evalPopulation));
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;

        origins.clear();
        for (T forest : evalPopulation) {
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

    protected abstract void reduce();

    protected void recomputeBest() {
        bestOfGeneration = population[0];
        for (T forest : population) {
            if (forest.getFitness() > bestOfGeneration.getFitness()) {
                bestOfGeneration = forest;
            }
        }
        if (bestSoFar.getFitness() < bestOfGeneration.getFitness()) {
            bestSoFar = bestOfGeneration;
            lastInnovation = 0;
            generationOfBSF = generation;
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

    public int getGenerationOfBSF() {
        return generationOfBSF;
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

    abstract public String getConfigString();

    public T getBestSoFar() {
        return bestSoFar;
    }

    public T getBestOfGeneration() {
        return bestOfGeneration;
    }

    public List<String> getEvaluationInfoItemNames() {
        return new LinkedList<String>();
    }

    public List<T> getLastGenerationPopulation() {
        return Arrays.asList(population);
    }

    public void showBestSoFar() {
        populationManager.showBSF(getBestSoFar());
    }
}
