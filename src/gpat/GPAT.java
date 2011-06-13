package gpat;

import common.RND;
import common.evolution.BasicInfo;
import common.evolution.EvaluationInfo;
import common.evolution.IEvolutionaryAlgorithm;
import common.evolution.PopulationManager;
import gp.GP;
import gp.IGP;
import gp.IGPForest;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 6, 2010
 * Time: 12:10:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPAT<P> implements IEvolutionaryAlgorithm, IGP<ATForest> {
    public static double MUTATION_ADD_LINK = 0.1;
    public static double MUTATION_ADD_NODE = 0.005;
    public static double MUTATION_INSERT_ROOT = 0.05;
    public static double MUTATION_REPLACE_CONSTANT = 0.5;
    public static double MUTATION_SWITCH_NODE = 0.05;
    public static double MUTATION_SWITCH_CONSTANT_LOCK = 0.05;
    public static double DISTANCE_DELTA = 0.04;
    public static double SPECIES_SIZE_MEAN = 5.0;
    public static double SPECIES_SIZE_RANGE = 3.0;
    public static double SPECIES_REPRODUCTION_RATIO = 0.1;
    public static double DISTANCE_C1 = 1.0;
    public static double DISTANCE_C2 = 1.0;
    public static double DISTANCE_C3 = 2.0;
    public static double ELITIST_PROPORTION_SIZE = 0.2;

    final protected int inputs;
    final protected int outputs;
    final protected ATNodeCollection nodeCollection;
    final protected PopulationManager<ATForest, P> populationManager;

    protected ATForest[] population;
    protected ATForest[] newPopulation;
    protected int generation;
    protected ATForest bestOfGeneration;
    protected ATForest bestSoFar;
    private ATInnovationHistory innovationHistory;
    private int lastInnovation;

    final private List<ATSpecies> species;
    private int maxSpecieId = 0;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    protected Map<String, Long> origins;

    public GPAT(PopulationManager<ATForest, P> populationManager, ATNodeImpl[] functions, ATNodeImpl[] terminals) {
        this.populationManager = populationManager;
        if (populationManager == null) {//for debuging only
            this.inputs = 0;
            this.outputs = 0;
        } else {
            this.inputs = populationManager.getNumberOfInputs();
            this.outputs = populationManager.getNumberOfOutputs();
        }

        this.nodeCollection = createNodeCollection(functions, terminals, populationManager.getNumberOfInputs());
        this.origins = new HashMap<String, Long>();
        this.innovationHistory = new ATInnovationHistory(this.inputs + terminals.length + 1);
        bestOfGeneration = bestSoFar = ATForest.createEmpty();
        this.species = new ArrayList<ATSpecies>();
    }

    protected ATNodeCollection createNodeCollection(ATNodeImpl[] functions, ATNodeImpl[] terminals, int numOfInputs) {
        return new ATNodeCollection(functions, terminals, numOfInputs);
    }

    public void initialGeneration() {
        generation = 1;
        generalizationGeneration = -1;
        lastInnovation = 0;

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

    protected void createInitialGeneration() {
        population = new ATForest[GP.POPULATION_SIZE];
        newPopulation = new ATForest[GP.POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = ATForest.createRandom(generation, inputs, outputs, nodeCollection, innovationHistory);
        }
    }

    protected void selectAndReproduce() {
        int cnt = 0;
        for (ATSpecies spec : species) {
            spec.markForReproduction();

            for (int i = 0; i < spec.getElitistSize(); i++) {
                newPopulation[cnt++] = spec.getMember(i).eliteCopy(generation);
            }

            for (int i = spec.getElitistSize(); i < spec.getEstimatedOffspring(); i++) {
                newPopulation[cnt++] = spec.getRandomMember().mutate(generation);
            }
        }
        if (cnt != population.length) {
            throw new IllegalStateException("selectAndReproduce() error");
        }
    }

    private void evaluate(ATForest[] evalPopulation) {
        populationManager.loadGenotypes(Arrays.asList(evalPopulation));
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;

        origins.clear();
        for (ATForest forest : evalPopulation) {
            EvaluationInfo oldInfo = evalPopulation[cnt].getEvaluationInfo();
            EvaluationInfo newInfo = evaluationInfos.get(cnt++);
            EvaluationInfo mixedInfo = EvaluationInfo.mixTwo(oldInfo, newInfo);

            forest.setFitness(mixedInfo.getFitness());
//            System.out.println(cnt + ": " + forest);

            forest.setEvaluationInfo(mixedInfo);
            saveOrigin(forest);
        }
//        System.out.println(innovationHistory);
    }

    protected void saveOrigin(IGPForest forest) {
        for (String origin : forest.getOrigins()) {
            if (!origins.containsKey(origin)) {
                origins.put(origin, 1L);
            } else {
                origins.put(origin, origins.get(origin) + 1L);
            }
        }
    }

    protected void reduce() {
        System.arraycopy(newPopulation, 0, population, 0, population.length);
        Arrays.sort(population);
    }

    private void recomputeBest() {
        bestOfGeneration = population[0];
        for (ATForest forest : population) {
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

    private void assignSpecies() {
        for (Iterator<ATSpecies> iterator = species.iterator(); iterator.hasNext();) {
            ATSpecies spec = iterator.next();
            if (spec.getSize() > 0) {
                //CHECK THIS WITH SNEAT
                spec.resetSpecies();
            } else {
                iterator.remove();
            }
        }
        for (ATForest forest : population) {
            boolean found = false;
            for (ATSpecies spec : species) {
                if (populationManager.getGenomeDistance(forest, spec.getRepresentative()) < GPAT.DISTANCE_DELTA) {
                    spec.addMember(forest);
                    found = true;
                    break;
                }
            }
            if (!found) {
                ATSpecies spec = new ATSpecies(++maxSpecieId);
                species.add(spec);
                spec.setRepresentative(forest);
                spec.addMember(forest);
            }
        }
        for (ATSpecies spec : species) {
            spec.sort();
        }
        for (Iterator<ATSpecies> iterator = species.iterator(); iterator.hasNext();) {
            ATSpecies spec = iterator.next();
            if (spec.getSize() == 0) {
                iterator.remove();
            }
        }

        int maxSpecies = (int) Math.round(GPAT.SPECIES_SIZE_MEAN + 0.5 * GPAT.SPECIES_SIZE_RANGE);
        int minSpecies = (int) Math.round(GPAT.SPECIES_SIZE_MEAN - 0.5 * GPAT.SPECIES_SIZE_RANGE);

        if (species.size() > maxSpecies) {
            GPAT.DISTANCE_DELTA *= 2;
        } else if (species.size() < minSpecies) {
            GPAT.DISTANCE_DELTA /= 2;
        }
    }

    private void optimizeConstantsStage() {
        for (ATSpecies spec : species) {
            IGPForest bestOfSpecies = spec.getMember(0).eliteCopy(generation);
            if (bestOfSpecies.getConstants().length == 0) {
                continue;
            }
            ATOptimizeConstantsStage constantsOptimizer = new ATOptimizeConstantsStage(populationManager, bestOfSpecies);
            IGPForest optimized = constantsOptimizer.optimize();
            if (spec.getMember(0).getFitness() < optimized.getFitness()) {
                System.out.println("BETTER!!!!");
                spec.setMember(0, (ATForest) optimized);
            } else {
                System.out.println("WORSE !!!!");
            }
        }

    }

    private void estimateOffspring() {
        double total = 0.0;
        for (ATSpecies spec : species) {
            spec.computeAverageFitness();
            total += spec.getAverageFitness();
        }

        int distribute = newPopulation.length;
        int assigned = 0;
        for (ATSpecies spec : species) {
            int assign = (int) (distribute * (spec.getAverageFitness() / total));
            spec.setEstimatedOffspring(assign);
            assigned += assign;
        }

        int[] toAssign = new int[distribute - assigned];
        if (toAssign.length <= species.size()) {
            RND.sampleRangeWithoutReplacement(species.size(), toAssign);
        } else {
            RND.sampleRangeWithReplacement(species.size(), toAssign);
        }

        for (int i : toAssign) {
            ATSpecies spec = species.get(i);
            spec.setEstimatedOffspring(spec.getEstimatedOffspring() + 1);
            assigned++;
        }

        if (assigned != distribute) {
            throw new IllegalStateException("ERROR: bad sum of expected offspring: " + assigned);
        }

        for (ATSpecies spec : species) {
            spec.setElitistSize((int) Math.min(spec.getSize(), (spec.getEstimatedOffspring() * GPAT.ELITIST_PROPORTION_SIZE)));
            if (spec.getElitistSize() == 0 && spec.getEstimatedOffspring() > 1) {
                spec.setElitistSize(1);
            }
        }
    }

    private void printSpeciesInfo() {
        for (ATSpecies spec : species) {
            System.out.println(spec);
        }
    }

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationManager.evaluateGeneralization(bestSoFar);
        generalizationGeneration = generation;
    }

    public void finished() {
        populationManager.shutdown();
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return getGeneration() * population.length;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public double getMaxFitnessReached() {
        return getBestSoFar().getFitness();
    }

    public List<EvaluationInfo> getEvaluationInfo() {
        List<EvaluationInfo> infoList = new ArrayList<EvaluationInfo>(population.length);
        for (IGPForest chromosome : population) {
            infoList.add(chromosome.getEvaluationInfo());
        }
        return infoList;
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public BasicInfo getPopulationInfo() {
        BasicInfo infoMap = populationManager.getPopulationInfo();
        for (String origin : origins.keySet()) {
            infoMap.put("O_" + origin, origins.get(origin));
        }
        return infoMap;
    }

    public boolean isSolved() {
        return populationManager.isSolved();
    }

    public String getConfigString() {
        StringBuilder s = new StringBuilder();
        s.append("CONSTANT_AMPLITUDE = ").append(GP.CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(GP.MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(GP.MAX_EVALUATIONS);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(GP.MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(GP.MUTATION_CAUCHY_POWER);
        s.append("\nPOPULATION_SIZE = ").append(GP.POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(GP.TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }

    public ATForest getBestSoFar() {
        return bestSoFar;
    }

    public ATForest getBestOfGeneration() {
        return bestOfGeneration;
    }

    public List<ATForest> getLastGenerationPopulation() {
        return Arrays.asList(population);
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> l = new LinkedList<String>();
        l.add("G_NODE_NUM");
        l.add("G_CONST_NUM");
        l.add("G_MAX_DEPTH");
        return l;
    }
}
