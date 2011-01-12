package gp;

import common.RND;
import common.evolution.PopulationManager;
import hyper.evaluate.storage.GenomeStorage;

import java.util.Arrays;


/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:34:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class GP<P> extends GPBase<P, Forest> {
    public static int MAX_DEPTH = 3;
    public static double MUTATION_SUBTREE_PROBABLITY = 0.5;

    public GP(PopulationManager<Forest, P> populationManager, INode[] functions, INode[] terminals, String initialGenome) {
        super(populationManager, functions, terminals, initialGenome);
        bestOfGeneration = bestSoFar = Forest.createEmpty();
    }

    @Override
    protected void init(INode[] functions) {
    }

    protected void createInitialGeneration() {
        population = new Forest[POPULATION_SIZE];
        newPopulation = new Forest[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            if (initialGenome == null) {
                population[i] = Forest.createRandom(generation, inputs, outputs, nodeCollection);
            } else {
                population[i] = ForestStorage.load(initialGenome);
            }
        }
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
}
