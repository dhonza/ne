package gp;

import common.RND;
import common.TSVHelper;
import common.evolution.BasicInfo;
import common.evolution.GenomeCounter;
import common.evolution.PopulationManager;
import common.pmatrix.ParameterCombination;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 9/12/11
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPEFS<P> extends GP<P> {
    public static double DISTANCE_DELTA = 0.04;
    public static double SPECIES_COUNT_MEAN = 5.0;
    public static double SPECIES_COUNT_RANGE = 3.0;
    public static double SPECIES_REPRODUCTION_RATIO = 0.1;
    public static double ELITIST_PROPORTION_SIZE = 0.2;
    public static String SPECIES_ASSIGN = "original";

    private int lastInnovation;
    private int generationOfBSF;

    final private List<Species> species;
    private int maxSpecieId = 0;

    private int generalizationGeneration;

    private boolean writeSpecies;
    private Map<Forest, Species> forestToSpecies = new HashMap<Forest, Species>();

    public GPEFS(ParameterCombination parameters, PopulationManager<Forest, P> forestPPopulationManager, INode[] functions, INode[] terminals, String initialGenome) {
        super(parameters, forestPPopulationManager, functions, terminals, initialGenome);
        this.writeSpecies = parameters.getBoolean("WRITE_SPECIES", false);
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
        if (SPECIES_ASSIGN.equalsIgnoreCase("original")) {
            assignSpeciesOriginal();
        } else if (SPECIES_ASSIGN.equalsIgnoreCase("kmedoids")) {
            assignSpeciesKMedoids();
        } else {
            throw new IllegalStateException("Unknown SPECIES ASSIGN startegy for GPREFS: " + SPECIES_ASSIGN);
        }
    }

    private void assignSpeciesOriginal() {
        forestToSpecies.clear();
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
                    forestToSpecies.put(forest, spec);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Species spec = new Species(++maxSpecieId);
                species.add(spec);
                spec.setRepresentative(forest);
                spec.addMember(forest);
                forestToSpecies.put(forest, spec);
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

        int maxSpecies = (int) Math.round(GPEFS.SPECIES_COUNT_MEAN + 0.5 * GPEFS.SPECIES_COUNT_RANGE);
        int minSpecies = (int) Math.round(GPEFS.SPECIES_COUNT_MEAN - 0.5 * GPEFS.SPECIES_COUNT_RANGE);

        if (species.size() > maxSpecies) {
            GPEFS.DISTANCE_DELTA *= 2;
        } else if (species.size() < minSpecies) {
            GPEFS.DISTANCE_DELTA /= 2;
        }
    }

    //this method is based on Hae-Sang Park, Chi-Hyuck Jun: A simple and fast algorithm for K-medoids clustering
    private void assignSpeciesKMedoids() {
        species.clear();
        forestToSpecies.clear();
        int k = (int) Math.round(GPEFS.SPECIES_COUNT_MEAN);
        //initialization
        Forest[] medoids = new Forest[k];

        //random
        RND.sampleWithoutReplacement(population, medoids);

        //proposed
//        double[] v = new double[population.length];
//        for (int j1 = 0, populationLength = population.length; j1 < populationLength; j1++) {
//            Forest j = population[j1];
//            v[j1] = 0.0;
//            for (Forest i : population) {
//                double dij = populationManager.getGenomeDistance(i, j);
//                double totalL = 0.0;
//                for (Forest l : population) {
//                    double dil = populationManager.getGenomeDistance(i, l);
//                    totalL += dil;
//                }
//                v[j1] += dij / totalL;
//            }
//        }
//        Forest[] tpop = population.clone();
//        ArrayHelper.sortABasedOnB(tpop, v);
//        System.arraycopy(tpop, 0, medoids, 0, k);

        for (int i = 0, medoidsLength = medoids.length; i < medoidsLength; i++) {
            Forest medoid = medoids[i];
            Species spec = new Species(++maxSpecieId);
            species.add(spec);
            spec.addMember(medoid);
            spec.setRepresentative(medoid);
        }

        boolean change = true;
        int cnt = 0;
        while (change && cnt < 100) {
            change = false;
            Set<Forest> representatives = new HashSet<Forest>();
            for (Species spec : species) {
                spec.clearSpecies();
                spec.addMember(spec.getRepresentative());
                representatives.add(spec.getRepresentative());
            }
            //assign to clusters
            for (Forest forest : population) {
                if (representatives.contains(forest)) {//already in species
                    continue;
                }
                double minDist = Double.MAX_VALUE;
                Species minSpecies = null;
                for (Species spec : species) {
                    double d = populationManager.getGenomeDistance(forest, spec.getRepresentative());
                    if (d < minDist) {
                        minDist = d;
                        minSpecies = spec;
                    }
                }
                minSpecies.addMember(forest);
            }
            int specTotalSize = 0;
            for (Species spec : species) {
                specTotalSize += spec.getSize();
            }
//            System.out.println("SPEC TOTAL SIZE: " + specTotalSize);

            //update step
            double totalMinCost = 0.0;
            for (Species spec : species) {
                double minCost = Double.MAX_VALUE;
                Forest minCostMember = null;
                for (int i = 0; i < spec.getSize(); i++) {
                    double total = 0.0;
                    for (int j = 0; j < spec.getSize(); j++) {
                        if (i != j) {
                            total += populationManager.getGenomeDistance(spec.getMember(i), spec.getMember(j));
                        }
                    }
                    if (total < minCost) {
                        minCost = total;
                        minCostMember = spec.getMember(i);
                    }
                }
                if (spec.getRepresentative() != minCostMember) {
                    change = true;
                }
                spec.setRepresentative(minCostMember);
                totalMinCost += minCost;
            }
            cnt++;
//            System.out.println(" COST: " + totalMinCost);
//            if (!change) {
//                System.out.println("CNT: " + cnt);
//            }
//            if (!change && cnt < 10) {
//                System.out.println("---- " + cnt);
//                change = true;
//            }
        }

        for (Species spec : species) {
            spec.sort();
            for (int i = 0; i < spec.getSize(); i++) {
                forestToSpecies.put(spec.getMember(i), spec);
            }
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
        //the opposite should not happen as the rounding error is max 1 less for each species
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

    @Override
    public List<String> getEvaluationInfoItemNames() {
        List<String> l = new LinkedList<String>();
        return l;
    }

    @Override
    public BasicInfo getPopulationInfo() {
        BasicInfo infoMap = super.getPopulationInfo();
        if (!writeSpecies) {
            return infoMap;
        }
        int[] speciesId = new int[population.length];

        //DIRTY TRICK sort copy of population by ID to match the distance matrix.
        Forest[] tpop = population.clone();
        Arrays.sort(tpop, new Comparator<Forest>() {
            public int compare(Forest forest, Forest forest1) {
                return (new Integer(forest.getId())).compareTo(forest1.getId());
            }
        });
        for (int i = 0, populationLength = tpop.length; i < populationLength; i++) {
            Forest forest = tpop[i];
            speciesId[i] = forestToSpecies.get(forest).getId();
        }
        infoMap.put("G_SPECIES", TSVHelper.arrayToTSV(speciesId));
        return infoMap;
    }
}
