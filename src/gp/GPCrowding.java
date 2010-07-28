package gp;

import common.RND;
import common.evolution.PopulationManager;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 23, 2009
 * Time: 8:52:54 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Deterministic/Probabilistic crowding: Mengshoel, Goldberg: The Crowding Approach to Niching in Genetic Algorithms
 * TODO create a single common abstract predecessor class (do not use newPopulation from GP)
 */
public class GPCrowding<P> extends GP<P> {
    public GPCrowding(PopulationManager<Forest, P> populationManager, Node[] functions, Node[] terminals) {
        super(populationManager, functions, terminals);
        if (GP.POPULATION_SIZE % 2 != 0) {
            throw new IllegalStateException("Population size must be even.");
        }
    }

    @Override
    protected void selectAndReproduce() {
        // shuffle population
        // http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
        int n = population.length;            // The number of items left to shuffle (loop invariant).
        while (n > 1) {
            n--;                         // n is now the last pertinent index
            int k = RND.getInt(0, n);  // 0 <= k <= n.
            Forest tmp = population[k];
            population[k] = population[n];
            population[n] = tmp;
        }

        // create mutated children
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = population[i].mutate(nodeCollection, generation);
//            double d1 = newPopulation[i].distance(population[i]);
//            double d2 = population[i].distance(newPopulation[i]);
//            System.out.println(population[i] + " -> " + newPopulation[i]);
//            System.out.println(population[i].innovationToString() + " -> " + newPopulation[i].innovationToString());
//            System.out.println("d = " + d1 + " " + d2);
//            if(d1 != d2) {
//                System.exit(1);
//            }
        }
    }

    @Override
    protected void reduce() {
        for (int i = 0; i < population.length; i += 2) {
            Forest p1 = population[i];
            Forest p2 = population[i + 1];
            Forest c1 = newPopulation[i];
            Forest c2 = newPopulation[i + 1];
            //matching parents to children
            double d1 = p1.distance(c1) + p2.distance(c2);
            double d2 = p1.distance(c2) + p2.distance(c1);
            if (d1 > d2) {
                Forest t = c1;
                c1 = c2;
                c2 = t;
            }
            //parent-child tournaments
            // TODO encapsulate replacement rules
//            if(c1.getFitness()/(c1.getFitness() + p1.getFitness()) > RND.getDouble()) {
//                population[i] = c1;
//            }
//            if(c2.getFitness()/(c2.getFitness() + p2.getFitness()) > RND.getDouble()) {
//                population[i+1] = c2;
//            }

            if (c1.getFitness() >= p1.getFitness()) {
                population[i] = c1;
            }
            if (c2.getFitness() >= p2.getFitness()) {
                population[i + 1] = c2;
            }
        }
    }
}
