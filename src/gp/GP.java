package gp;

import common.RND;
import common.evolution.EvolutionaryAlgorithm;
import gp.terminals.Input;

import java.util.Arrays;


/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:34:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class GP implements EvolutionaryAlgorithm {
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
    final private Evaluable evaluator;
    final private TreeInputs treeInputs;

    protected Forest[] population;
    protected Forest[] newPopulation;
    protected int generation;
    private Forest bestOfGeneration;
    private Forest bestSoFar;
    private int lastInnovation;

    public GP(Evaluable evaluator, Node[] functions, Node[] terminals) {
        this.evaluator = evaluator;
        this.inputs = evaluator.getNumberOfInputs();
        this.outputs = evaluator.getNumberOfOutputs();

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
            population[i] = Forest.createRandom(generation, treeInputs, outputs, nodeCollection);
        }
    }

    private void evaluate(Forest[] evalPopulation) {
        for (Forest forest : evalPopulation) {
            forest.setFitness(evaluator.evaluate(forest));
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

    public Forest getBestOfGeneration() {
        return bestOfGeneration;
    }

    public Forest getBestSoFar() {
        return bestSoFar;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }


}
