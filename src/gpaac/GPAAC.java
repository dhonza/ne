package gpaac;

import common.RND;
import common.evolution.PopulationManager;
import gp.GPBase;
import gp.IGPForest;
import gp.INode;
import gp.NodeCollection;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 6:09:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPAAC<P> extends GPBase<P, AACForest> {
    public static int MAX_DEPTH = 3;
    public static double MUTATION_SUBTREE_PROBABLITY = 0.1;
    public static double MUTATION_NODE_PROBABLITY = 0.1;
    public static double MUTATION_REPLACE_CONSTANTS = 0.2;
    public static double MUTATION_SWITCH_CONSTANT_LOCK = 0.01;
    public static double MUTATION_ADD_CHILD = 0.1;

    public GPAAC(PopulationManager<AACForest, P> populationManager, INode[] functions, INode[] terminals, String initialGenome) {
        super(populationManager, functions, terminals, initialGenome);
        bestOfGeneration = bestSoFar = AACForest.createEmpty();
    }

    @Override
    protected void init(INode[] functions) {
    }

    @Override
    protected NodeCollection createNodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        return new AACNodeCollection(functions, terminals, numOfInputs);
    }

    @Override
    protected void createInitialGeneration() {
        population = new AACForest[POPULATION_SIZE];
        newPopulation = new AACForest[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = AACForest.createRandom(generation, inputs, outputs, nodeCollection);
        }
    }

    @Override
    protected void selectAndReproduce() {
        System.arraycopy(population, 0, newPopulation, 0, population.length);
        for (int i = 0; i < newPopulation.length; i++) {
            AACForest p1 = population[RND.getInt(0, population.length - 1)];
            AACForest p2 = population[RND.getInt(0, population.length - 1)];
            AACForest p = p1.getFitness() > p2.getFitness() ? p1 : p2;
            newPopulation[i] = p.mutate(nodeCollection, generation);
        }
    }

    @Override
    protected void reduce() {
        AACForest[] oldAndNewPopulation = new AACForest[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }

    @Override
    public String getConfigString() {
        StringBuilder s = new StringBuilder();
        s.append("CONSTANT_AMPLITUDE = ").append(CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(MAX_EVALUATIONS);
        s.append("\nMAX_DEPTH = ").append(MAX_DEPTH);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(MUTATION_CAUCHY_POWER);
        s.append("\nPOPULATION_SIZE = ").append(POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }
}
