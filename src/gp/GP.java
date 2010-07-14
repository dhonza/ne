package gp;

import common.RND;
import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.evolution.EvolutionaryAlgorithm;
import common.evolution.ParallelPopulationEvaluator;
import gp.terminals.Input;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:34:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class GP implements EvolutionaryAlgorithm, Serializable {
    public static double CONSTANT_AMPLITUDE = 5.0;
    public static int MAX_GENERATIONS = 1000;
    public static int MAX_EVALUATIONS = Integer.MAX_VALUE;
    public static int MAX_DEPTH = 3;
    public static double MUTATION_CAUCHY_PROBABILITY = 0.8;
    public static double MUTATION_CAUCHY_POWER = 0.01;
    public static double MUTATION_SUBTREE_PROBABLITY = 0.5;
    public static int POPULATION_SIZE = 10;
    public static double TARGET_FITNESS = Double.MAX_VALUE;

    final private int inputs;
    final private int outputs;
    final protected NodeCollection nodeCollection;
    final private Evaluable<Forest>[] perThreadEvaluators;
    final private ParallelPopulationEvaluator<Forest> populationEvaluator;

    final private TreeInputs treeInputs;

    protected Forest[] population;
    protected Forest[] newPopulation;
    protected int generation;
    private Forest bestOfGeneration;
    private Forest bestSoFar;
    private int lastInnovation;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    public GP(Evaluable<Forest>[] perThreadEvaluators, Node[] functions, Node[] terminals) {
        this.perThreadEvaluators = perThreadEvaluators;
        populationEvaluator = new ParallelPopulationEvaluator<Forest>();
        this.inputs = perThreadEvaluators[0].getNumberOfInputs();
        this.outputs = perThreadEvaluators[0].getNumberOfOutputs();

        Node[] allTerminals = new Node[terminals.length + inputs];
        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        treeInputs = new TreeInputs(inputs);
        for (int i = 0; i < inputs; i++) {
            allTerminals[terminals.length + i] = new Input(i, treeInputs);
        }
        this.nodeCollection = new NodeCollection(functions, allTerminals);

        bestOfGeneration = bestSoFar = Forest.createEmpty();
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
//            distances();
        selectAndReproduce();
        evaluate(newPopulation);
        reduce();
        recomputeBest();
    }

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationEvaluator.evaluateGeneralization(perThreadEvaluators, bestSoFar);
        generalizationGeneration = generation;
    }

    public void finished() {
    }

    public String getConfigString() {
        StringBuilder s = new StringBuilder();
        s.append("CONSTANT_AMPLITUDE = ").append(CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(MAX_EVALUATIONS);
        s.append("\nMAX_DEPTH = ").append(MAX_DEPTH);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(MUTATION_CAUCHY_POWER);
        s.append("\nMUTATION_SUBTREE_PROBABLITY = ").append(MUTATION_SUBTREE_PROBABLITY);
        s.append("\nPOPULATION_SIZE = ").append(POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    private void createInitialGeneration() {
        population = new Forest[POPULATION_SIZE];
        newPopulation = new Forest[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = Forest.createRandom(generation, inputs, outputs, nodeCollection);
        }
    }

    private void evaluate(Forest[] evalPopulation) {
        EvaluationInfo[] evaluationInfos = populationEvaluator.evaluate(perThreadEvaluators, Arrays.asList(evalPopulation));
        int cnt = 0;
        for (Forest forest : evalPopulation) {
            forest.setFitness(evaluationInfos[cnt].getFitness());
            forest.setEvaluationInfo(evaluationInfos[cnt++]);
        }
    }

    private void distances() {
        double sumDistances = 0.0;
        double cnt = 0.0;
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                sumDistances += population[i].distance(population[j]);
                cnt += 1.0;
//                System.out.println(i + ":" + j + " = " + population[i].distance(population[j]));
            }
        }
        System.out.println("D: " + sumDistances / cnt);
    }

    protected void selectAndReproduce() {
        System.arraycopy(population, 0, newPopulation, 0, population.length);
        for (int i = 0; i < newPopulation.length; i++) {
            Forest p1 = population[RND.getInt(0, population.length - 1)];
            Forest p2 = population[RND.getInt(0, population.length - 1)];
            Forest p = p1.getFitness() > p2.getFitness() ? p1 : p2;
            newPopulation[i] = p.mutate(nodeCollection, generation);
        }
    }

    protected void reduce() {
        Forest[] oldAndNewPopulation = new Forest[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }

    private void recomputeBest() {
        bestOfGeneration = population[0];
        for (Forest forest : population) {
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

    public Forest[] getPopulation() {
        return population.clone();
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return getGeneration() * population.length;
    }

    public double getMaxFitnessReached() {
        return getBestSoFar().getFitness();
    }

    public boolean isSolved() {
        for (Evaluable<Forest> evaluator : perThreadEvaluators) {
            if (evaluator.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public Forest getBestOfGeneration() {
        return bestOfGeneration;
    }

    public Forest getBestSoFar() {
        return bestSoFar;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public EvaluationInfo[] getEvaluationInfo() {
        EvaluationInfo[] fv = new EvaluationInfo[population.length];
        for (int i = 0; i < population.length; i++) {
            fv[i] = population[i].getEvaluationInfo();
        }
        return fv;
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }
}
