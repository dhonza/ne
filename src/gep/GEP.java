package gep;

import common.RND;
import common.evolution.PopulationManager;
import gp.GPBase;
import gp.Node;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 4:19:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEP<P> extends GPBase<P, GEPChromosome> {
    protected static double MUTATION_HEADTAIL_RATE;
    protected static double MUTATION_DC_RATE;

    public static int HEAD = 5;
    protected static int TAIL;
    //constant domain
    protected static int DC;
    protected static int HEAD_TAIL;
    //number of directly evolved constants
    public static int C_SIZE = 10;

    public GEP(PopulationManager<GEPChromosome, P> populationManager, Node[] functions, Node[] terminals) {
        super(populationManager, functions, terminals);
        bestOfGeneration = bestSoFar = GEPChromosome.createEmpty();
    }

    protected void init(Node[] functions) {
        int maxArity = 0;
        for (Node function : functions) {
            if (function.getArity() > maxArity) {
                maxArity = function.getArity();
            }
        }
        TAIL = HEAD * (maxArity - 1) + 1;
        DC = TAIL;
        HEAD_TAIL = HEAD + TAIL;
        MUTATION_HEADTAIL_RATE = 2.0 / HEAD_TAIL;
        MUTATION_DC_RATE = 2.0 / DC;
    }

    protected void createInitialGeneration() {
        population = new GEPChromosome[POPULATION_SIZE];
        newPopulation = new GEPChromosome[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = GEPChromosome.createRandom(generation, inputs, outputs, nodeCollection);
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

    public String getConfigString() {
        return "IMPLEMENT CONFIG STRING!!!!";
    }
}
