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
    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_IS_TRANSPOSITION_LENGTH = 3;

    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_RIS_TRANSPOSITION_LENGTH = 2;

    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_DC_TRANSPOSITION_LENGTH = 3;

    protected static double MUTATION_HEADTAIL_RATE;
    protected static double MUTATION_DC_RATE;

    public static int HEAD = 5;
    protected static int TAIL;
    //constant domain
    protected static int DC;
    protected static int HEAD_TAIL;
    //number of directly evolved constants
    public static int C_SIZE = 15;

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
        //find best one (elite)
        //according to the book we get the last best
        double best = population[0].getFitness();
        int bestI = 0;
        for (int i = 1; i < population.length; i++) {
            if (population[i].getFitness() >= best) {
                best = population[i].getFitness();
                bestI = i;
            }
        }
        //swap it to the position 0
        GEPChromosome tc = population[bestI];
        population[bestI] = population[0];
        population[0] = tc;

        //roulette
        double[] fitness = new double[population.length];
        double sum = 0.0;
        for (int i = 0; i < population.length; i++) {
            fitness[i] = population[i].getFitness();
            sum += fitness[i];
        }
        for (int i = 0; i < population.length; i++) {
            fitness[i] /= sum;
        }
        for (int i = 1; i < population.length; i++) {
            fitness[i] += fitness[i - 1];
        }
        fitness[population.length - 1] = 1.0;//rounding errors

        newPopulation[0] = population[0];//copy elite

        for (int i = 1; i < newPopulation.length; i++) {
            double roulette = RND.getDouble();
            //TODO turn the wheel... binary search would be better
            int chosen = 0;
            while (roulette > fitness[chosen]) {
                chosen++;
            }
            newPopulation[i] = population[chosen];
        }

        for (int i = 1; i < newPopulation.length; i++) {
            newPopulation[i] = newPopulation[i].mutate(nodeCollection, generation);
            newPopulation[i] = newPopulation[i].mutateDC(generation);
            newPopulation[i] = newPopulation[i].mutateRNC(generation);
        }
    }

    protected void reduce() {
        Arrays.sort(newPopulation);
        System.arraycopy(newPopulation, 0, population, 0, population.length);
    }

    public String getConfigString() {
        return "IMPLEMENT CONFIG STRING!!!!";
    }
}
