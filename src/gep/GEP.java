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

    public static double INVERSION_RATE = 0.1;

    public static double IS_TRANSPOSITION_RATE = 0.1;

    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_IS_TRANSPOSITION_LENGTH = 3;

    public static double RIS_TRANSPOSITION_RATE = 0.1;

    public static double GENE_TRANSPOSITION_RATE = 0.0;

    public static double ONE_POINT_RECOMBINATION_RATE = 0.0;

    public static double TWO_POINT_RECOMBINATION_RATE = 0.0;

    public static double GENE_RECOMBINATION_RATE = 0.0;

    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_RIS_TRANSPOSITION_LENGTH = 2;

    //the length of transposed sequences are from 1 to this number (inclusive)
    public static int MAX_DC_TRANSPOSITION_LENGTH = 3;

    protected static double MUTATION_DC_RATE;

    public static double INVERSION_DC_RATE = 0.1;

    public static double DC_TRANSPOSITION_RATE = 0.1;

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

    private void reproduce(int startIdx) {
        int size = population.length - startIdx;
        for (int i = startIdx; i < newPopulation.length; i++) {
            newPopulation[i] = newPopulation[i].mutate(nodeCollection, generation);

            if (RND.getDouble() < INVERSION_RATE) {
                newPopulation[i] = newPopulation[i].invert(generation);
            }

            if (RND.getDouble() < INVERSION_DC_RATE) {
                newPopulation[i] = newPopulation[i].invertDC(generation);
            }

            if (RND.getDouble() < IS_TRANSPOSITION_RATE) {
                newPopulation[i] = newPopulation[i].transposeIS(generation);
            }

            if (RND.getDouble() < RIS_TRANSPOSITION_RATE) {
                newPopulation[i] = newPopulation[i].transposeRIS(generation);
            }

            if (RND.getDouble() < GENE_TRANSPOSITION_RATE) {
                newPopulation[i] = newPopulation[i].transposeGene(generation);
            }

            if (RND.getDouble() < DC_TRANSPOSITION_RATE) {
                newPopulation[i] = newPopulation[i].transposeDC(generation);
            }

            //recombination expected numbers
            int onePointRecombinations = ((int) Math.round(size * ONE_POINT_RECOMBINATION_RATE)) / 2;
            int[] indices = new int[2 * onePointRecombinations];//pairs of parents
            RND.sampleRangeWithoutReplacement(startIdx, population.length - 1, indices);
            for (int j = 0; j < onePointRecombinations; j += 2) {
                GEPChromosome[] children =
                        newPopulation[indices[j]].crossoverOnePoint(newPopulation[indices[j + 1]], generation);
                newPopulation[indices[j]] = children[0];
                newPopulation[indices[j + 1]] = children[1];
            }

            int twoPointRecombinations = ((int) Math.round(size * TWO_POINT_RECOMBINATION_RATE)) / 2;
            indices = new int[2 * twoPointRecombinations];//pairs of parents
            RND.sampleRangeWithoutReplacement(startIdx, population.length, indices);
            for (int j = 0; j < onePointRecombinations; j += 2) {
                GEPChromosome[] children =
                        newPopulation[indices[j]].crossoverTwoPoint(newPopulation[indices[j + 1]], generation);
                newPopulation[indices[j]] = children[0];
                newPopulation[indices[j + 1]] = children[1];
            }

            int geneRecombinations = ((int) Math.round(size * GENE_RECOMBINATION_RATE)) / 2;
            indices = new int[2 * geneRecombinations];//pairs of parents
            RND.sampleRangeWithoutReplacement(startIdx, population.length, indices);
            for (int j = 0; j < onePointRecombinations; j += 2) {
                GEPChromosome[] children =
                        newPopulation[indices[j]].crossoverGene(newPopulation[indices[j + 1]], generation);
                newPopulation[indices[j]] = children[0];
                newPopulation[indices[j + 1]] = children[1];
            }
        }

    }

//    /*

    protected void selectAndReproduce() {
        System.arraycopy(population, 0, newPopulation, 0, population.length);
        for (int i = 0; i < newPopulation.length; i++) {
            GEPChromosome p1 = population[RND.getInt(0, population.length - 1)];
            GEPChromosome p2 = population[RND.getInt(0, population.length - 1)];
            GEPChromosome p = p1.getFitness() > p2.getFitness() ? p1 : p2;
            newPopulation[i] = p;
        }
        reproduce(0);
    }

    protected void reduce() {
        GEPChromosome[] oldAndNewPopulation = new GEPChromosome[population.length + newPopulation.length];
        System.arraycopy(population, 0, oldAndNewPopulation, 0, population.length);
        System.arraycopy(newPopulation, 0, oldAndNewPopulation, population.length, newPopulation.length);
        Arrays.sort(oldAndNewPopulation);
        System.arraycopy(oldAndNewPopulation, 0, population, 0, population.length);
    }
//*/
/*
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
            reproduce(1);
        }

        protected void reduce() {
            Arrays.sort(newPopulation);
            System.arraycopy(newPopulation, 0, population, 0, population.length);
        }
*/

    public String getConfigString() {
        return "IMPLEMENT CONFIG STRING!!!!";
    }
}
