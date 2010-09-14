package gep;

import common.RND;
import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;
import common.evolution.PopulationManager;
import gp.Node;
import gp.NodeCollection;
import gp.TreeInputs;
import gp.terminals.Input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 4:19:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEP<P> implements IEvolutionaryAlgorithm, Serializable {
    public static double CONSTANT_AMPLITUDE = 5.0;
    public static int MAX_GENERATIONS = 1000;
    public static int MAX_EVALUATIONS = Integer.MAX_VALUE;
    public static int POPULATION_SIZE = 10;
    public static double TARGET_FITNESS = Double.MAX_VALUE;

    public static int HEAD = 5;
    protected static int TAIL;
    //constant domain
    protected static int DC;
    protected static int HEAD_TAIL;
    //number of directly evolved constants
    public static int C_SIZE = 10;

    final private int inputs;
    final private int outputs;
    final protected NodeCollection nodeCollection;

    final protected PopulationManager<GEPChromosome, P> populationManager;

    protected GEPChromosome[] population;
    protected GEPChromosome[] newPopulation;
    protected int generation;
    private GEPChromosome bestOfGeneration;
    private GEPChromosome bestSoFar;
    private int lastInnovation;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    public GEP(PopulationManager<GEPChromosome, P> populationManager, Node[] functions, Node[] terminals) {
        this.populationManager = populationManager;
        this.inputs = populationManager.getNumberOfInputs();
        this.outputs = populationManager.getNumberOfOutputs();

        initGenomes(functions);
        Node[] allTerminals = new Node[terminals.length + inputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        TreeInputs treeInputs = new TreeInputs(inputs);
        for (int i = 0; i < inputs; i++) {
            allTerminals[terminals.length + i] = new Input(i, treeInputs);
        }
        this.nodeCollection = new NodeCollection(functions, allTerminals);

        bestOfGeneration = bestSoFar = GEPChromosome.createEmpty();
    }

    static void initGenomes(Node[] functions) {
        int maxArity = 0;
        for (Node function : functions) {
            if (function.getArity() > maxArity) {
                maxArity = function.getArity();
            }
        }
        TAIL = HEAD * (maxArity - 1) + 1;
        DC = TAIL;
        HEAD_TAIL = HEAD + TAIL;
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

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationManager.evaluateGeneralization(bestSoFar);
        generalizationGeneration = generation;
    }

    public void finished() {
        populationManager.shutdown();
    }

    private void createInitialGeneration() {
        population = new GEPChromosome[POPULATION_SIZE];
        newPopulation = new GEPChromosome[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = GEPChromosome.createRandom(generation, inputs, outputs, nodeCollection);
        }
    }

    private void evaluate(GEPChromosome[] evalPopulation) {
        populationManager.loadGenotypes(Arrays.asList(evalPopulation));
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;
        for (GEPChromosome forest : evalPopulation) {
            forest.setFitness(evaluationInfos.get(cnt).getFitness());
            forest.setEvaluationInfo(evaluationInfos.get(cnt++));
        }
    }

    protected void selectAndReproduce() {
        System.arraycopy(population, 0, newPopulation, 0, population.length);
        for (int i = 0; i < newPopulation.length; i++) {
            GEPChromosome p1 = population[RND.getInt(0, population.length - 1)];
            GEPChromosome p2 = population[RND.getInt(0, population.length - 1)];
            GEPChromosome p = p1.getFitness() > p2.getFitness() ? p1 : p2;
            newPopulation[i] = p.mutate(nodeCollection, generation);
        }
    }

    protected void reduce() {
        GEPChromosome[] oldAndNewPopulation = new GEPChromosome[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }

    private void recomputeBest() {
        bestOfGeneration = population[0];
        for (GEPChromosome forest : population) {
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

    public GEPChromosome getBestOfGeneration() {
        return bestOfGeneration;
    }

    public GEPChromosome getBestSoFar() {
        return bestSoFar;
    }

    public List<EvaluationInfo> getEvaluationInfo() {
        List<EvaluationInfo> infoList = new ArrayList<EvaluationInfo>(population.length);
        for (GEPChromosome chromosome : population) {
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

    public String getConfigString() {
        return "IMPLEMENT CONFIG STRING!!!!";
    }
}
