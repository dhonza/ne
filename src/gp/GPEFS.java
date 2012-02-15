package gp;

import common.RND;
import common.evolution.GenomeCounter;
import common.evolution.PopulationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 9/12/11
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPEFS<P> extends GP<P> {
    public static double DISTANCE_DELTA = 0.04;
    public static double SPECIES_SIZE_MEAN = 5.0;
    public static double SPECIES_SIZE_RANGE = 3.0;
    public static double SPECIES_REPRODUCTION_RATIO = 0.1;
    public static double ELITIST_PROPORTION_SIZE = 0.2;

    private int lastInnovation;
    private int generationOfBSF;

    final private List<Species> species;
    private int maxSpecieId = 0;

    private int generalizationGeneration;

    public GPEFS(PopulationManager<Forest, P> forestPPopulationManager, INode[] functions, INode[] terminals, String initialGenome) {
        super(forestPPopulationManager, functions, terminals, initialGenome);
        this.species = new ArrayList<Species>();
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
        assignSpecies();
//        optimizeConstantsStage();
        estimateOffspring();
        printSpeciesInfo();
    }

    public void nextGeneration() {
        generation++;
        selectAndReproduce();
        evaluate(newPopulation);
        reduce();
        recomputeBest();
        assignSpecies();
//        optimizeConstantsStage();
        estimateOffspring();
        printSpeciesInfo();
    }

    protected void selectAndReproduce() {
        int cnt = 0;
        for (Species spec : species) {
            spec.markForReproduction();

            for (int i = 0; i < spec.getElitistSize(); i++) {
                newPopulation[cnt++] = spec.getMember(i).eliteCopy(generation);
            }

            for (int i = spec.getElitistSize(); i < spec.getEstimatedOffspring(); i++) {
                Forest mutated;

                mutated = spec.getRandomMember().mutate(nodeCollection, generation);

                newPopulation[cnt++] = mutated;
            }
        }
        if (cnt != population.length) {
            throw new IllegalStateException("selectAndReproduce() error");
        }
    }

    protected void reduce() {
        System.arraycopy(newPopulation, 0, population, 0, population.length);
        Arrays.sort(population);
    }

    private void assignSpecies() {
        for (Iterator<Species> iterator = species.iterator(); iterator.hasNext(); ) {
            Species spec = iterator.next();
            if (spec.getSize() > 0) {
                //CHECK THIS WITH SNEAT
                spec.resetSpecies();
            } else {
                iterator.remove();
            }
        }
        for (Forest forest : population) {
            boolean found = false;
            for (Species spec : species) {
                if (populationManager.getGenomeDistance(forest, spec.getRepresentative()) < GPEFS.DISTANCE_DELTA) {
                    spec.addMember(forest);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Species spec = new Species(++maxSpecieId);
                species.add(spec);
                spec.setRepresentative(forest);
                spec.addMember(forest);
            }
        }
        for (Species spec : species) {
            spec.sort();
        }
        for (Iterator<Species> iterator = species.iterator(); iterator.hasNext(); ) {
            Species spec = iterator.next();
            if (spec.getSize() == 0) {
                iterator.remove();
            }
        }

        int maxSpecies = (int) Math.round(GPEFS.SPECIES_SIZE_MEAN + 0.5 * GPEFS.SPECIES_SIZE_RANGE);
        int minSpecies = (int) Math.round(GPEFS.SPECIES_SIZE_MEAN - 0.5 * GPEFS.SPECIES_SIZE_RANGE);

        if (species.size() > maxSpecies) {
            GPEFS.DISTANCE_DELTA *= 2;
        } else if (species.size() < minSpecies) {
            GPEFS.DISTANCE_DELTA /= 2;
        }
    }

    private void estimateOffspring() {
        double total = 0.0;
        for (Species spec : species) {
            spec.computeAverageFitness();
            total += spec.getAverageFitness();
        }

        int distribute = newPopulation.length;
        int assigned = 0;
        for (Species spec : species) {
            int assign = (int) (distribute * (spec.getAverageFitness() / total));
            spec.setEstimatedOffspring(assign);
            assigned += assign;
        }

        int[] toAssign = new int[distribute - assigned];
//        if (toAssign.length <= species.size()) {
//            RND.sampleRangeWithoutReplacement(species.size(), toAssign);
//        } else {
//            RND.sampleRangeWithReplacement(species.size(), toAssign);
//        }
        assert (toAssign.length <= species.size());
        //the opposite should can not happen as the rounding error is max 1 less for each species
        RND.sampleRangeWithoutReplacement(species.size(), toAssign);


        for (int i : toAssign) {
            Species spec = species.get(i);
            spec.setEstimatedOffspring(spec.getEstimatedOffspring() + 1);
            assigned++;
        }

        if (assigned != distribute) {
            throw new IllegalStateException("ERROR: bad sum of expected offspring: " + assigned);
        }

        for (Species spec : species) {
            spec.setElitistSize((int) Math.min(spec.getSize(), (spec.getEstimatedOffspring() * GPEFS.ELITIST_PROPORTION_SIZE)));
            if (spec.getElitistSize() == 0 && spec.getEstimatedOffspring() > 1) {
                spec.setElitistSize(1);
            }
        }
    }

    private void printSpeciesInfo() {
        for (Species spec : species) {
            System.out.println(spec);
        }
    }
}
