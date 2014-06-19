package mogp;

import com.google.common.collect.*;
import common.RND;
import common.evolution.*;
import common.pmatrix.ParameterCombination;
import gp.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:34:02 AM
 */
public class MOGP<P> implements IEvolutionaryAlgorithm, IGP<Forest>, Serializable {
    public static double CONSTANT_AMPLITUDE = 5.0;
    public static int MAX_GENERATIONS = 1000;
    public static int MAX_EVALUATIONS = Integer.MAX_VALUE;
    public static int POPULATION_SIZE = 100;
    public static double TARGET_FITNESS = Double.MAX_VALUE;

    public static double MUTATION_CAUCHY_PROBABILITY = 0.8;
    public static double MUTATION_CAUCHY_POWER = 0.01;

    public static int MAX_DEPTH = 3;
    public static double MUTATION_SUBTREE_PROBABILITY = 0.5;

    private static final int NUM_OF_OBJECTIVES = 2;

    private ImmutableList<Solution> population;
    private IEvaluable<P> evaluable;


    final protected int inputs;
    final protected int outputs;
    final protected NodeCollection nodeCollection;
    final protected ParameterCombination parameters;
    final protected PopulationManager<Forest, P> populationManager;

    final protected String initialGenome;

    protected int generation;
    protected Forest bestOfGeneration;
    protected Forest bestSoFar;
    private int lastInnovation;
    private int generationOfBSF;

    private int generalizationGeneration;


    protected Map<String, Long> origins;

    public MOGP(ParameterCombination parameters, PopulationManager<Forest, P> populationManager, INode[] functions, INode[] terminals, String initialGenome) {
        this.parameters = parameters;
        this.populationManager = populationManager;
        this.initialGenome = initialGenome;
        this.inputs = populationManager.getNumberOfInputs();
        this.outputs = populationManager.getNumberOfOutputs();

        this.nodeCollection = createNodeCollection(functions, terminals, populationManager.getNumberOfInputs());
        this.origins = new HashMap<>();

        bestOfGeneration = bestSoFar = Forest.createEmpty();
        evaluable = populationManager.getFirstEvaluable();
    }

    private NodeCollection createNodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        return new NodeCollection(functions, terminals, numOfInputs);
    }

    public void initialGeneration() {
        generation = 1;
        generalizationGeneration = -1;
        lastInnovation = 0;
        generationOfBSF = generation;

        GenomeCounter.INSTANCE.reset();

        createInitialPopulation();

        evaluate(population);
        evaluateDiversity(population);

        ImmutableList<ImmutableList<Solution>> fronts = fastNonDominatedSort(population);
//        printFronts(fronts);
//        printFront(fronts.get(0));

        crowdingDistanceAssignment(fronts);

        recomputeBest();
    }

    public void nextGeneration() {
        generation++;

        ImmutableList<Solution> offspring = createOffspring(population);

        evaluate(offspring);
//        evaluateDiversity(offspring);

        ImmutableList<Solution> oldAndNew = ImmutableList.copyOf(Iterables.concat(population, offspring));

        evaluateDiversity(oldAndNew);

        ImmutableList<ImmutableList<Solution>> fronts = fastNonDominatedSort(oldAndNew);
//        printFronts(fronts);
//        printFront(fronts.get(0));

        crowdingDistanceAssignment(fronts);

        population = reduce(population.size(), fronts);

        recomputeBest();
    }


    private void createInitialPopulation() {
        ImmutableList.Builder<Solution> builder = ImmutableList.builder();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (initialGenome == null) {
                builder.add(new Solution(Forest.createRandom(generation, inputs, outputs, nodeCollection)));
            } else {
                builder.add(new Solution(ForestStorage.load(initialGenome)));
            }
        }
        population = builder.build();
    }

    private void evaluate(ImmutableList<Solution> pop) {
        ImmutableList<Forest> forests = getPopulationForests(pop);

        //FITNESS
        populationManager.loadGenotypes(forests);
        List<EvaluationInfo> evaluationInfos = populationManager.evaluate();
        int cnt = 0;

        origins.clear();
        for (Forest forest : forests) {
            forest.setFitness(evaluationInfos.get(cnt).getFitness());
            forest.setEvaluationInfo(evaluationInfos.get(cnt++));
            saveOrigin(forest);
        }
    }

    private void evaluateDiversity(ImmutableList<Solution> pop) {
        //DIVERSITY
        IBehavioralDiversity maze = (IBehavioralDiversity) evaluable;
        ImmutableList<EvaluationInfo> evaluationInfos = getEvaluationInfo(pop);
        ImmutableList<Double> behavioralDiversities = maze.behavioralDiversity(evaluationInfos);

//        ImmutableList<Double> genotypeDiversities = genotypeDiversity(pop);

        /*
        ImmutableList.Builder<Double> sumDiversitiesBuilder = ImmutableList.builder();
        double diversity1Max = Ordering.<Double>natural().max(behavioralDiversities);
//        double diversity2Max = Ordering.<Double>natural().max(behavioralDiversities2);
        for (int i = 0; i < behavioralDiversities.size(); i++) {
            double diversity1 = diversity1Max == 0.0 ? 0.0 : behavioralDiversities.get(i) / diversity1Max;
//            double diversity2 = diversity2Max == 0.0 ? 0.0 : behavioralDiversities2.get(i) / diversity2Max;
            double diversity2 = 0.0;
            sumDiversitiesBuilder.add(diversity1 + diversity2);
        }
        ImmutableList<Double> sumDiversities = sumDiversitiesBuilder.build();
          */

        //SET OBJECTIVES
        for (int i = 0; i < pop.size(); i++) {
            Forest forest = pop.get(i).getX();
//            Objectives objectives = new Objectives(ImmutableList.of(forest.getFitness(), sumDiversities.get(i)));
            Objectives objectives = new Objectives(ImmutableList.of(forest.getFitness(), behavioralDiversities.get(i)));
//            Objectives objectives = new Objectives(ImmutableList.of(forest.getFitness(), genotypeDiversities.get(i)));
//            Objectives objectives = new Objectives(ImmutableList.of(forest.getFitness(), 0.0));
//            Objectives objectives = new Objectives(ImmutableList.of(forest.getFitness()));
            pop.get(i).setObjectives(objectives);
        }
    }

    public ImmutableList<Double> genotypeDiversity(ImmutableList<Solution> pop) {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();

        for (Solution e1 : pop) {
            double sum = 0.0;
            for (Solution e2 : pop) {
                if (e1 != e2) {
                    sum += populationManager.getGenomeDistance(e1.getX(), e2.getX());
                }
            }
            builder.add(sum / pop.size());
        }
        return builder.build();
    }

    private ImmutableList<ImmutableList<Solution>> fastNonDominatedSort(ImmutableList<Solution> pop) {
        Multiset<Solution> dominationCount = HashMultiset.create(); //domination count
        Multimap<Solution, Solution> dominated = HashMultimap.create();

        ImmutableList.Builder<ImmutableList<Solution>> fronts = ImmutableList.builder();
        List<Solution> currentFront = Lists.newArrayList();

        for (Solution p : pop) {
            for (Solution q : pop) {
                if (p == q) {
                    continue;
                }
                if (p.dominates(q)) {
                    dominated.put(p, q);
                } else if (q.dominates(p)) {
                    dominationCount.add(p);
                }
            }
            if (!dominationCount.contains(p)) {
                p.setRank(1);
                currentFront.add(p);
            }
        }

        int i = 1;
        while (!currentFront.isEmpty()) {
            List<Solution> nextFront = Lists.newArrayList();
            for (Solution p : currentFront) {
                for (Solution q : dominated.get(p)) {
                    dominationCount.remove(q);
                    if (!dominationCount.contains(q)) {
                        q.setRank(i + 1);
                        nextFront.add(q);
                    }
                }
            }
            i++;
            fronts.add(ImmutableList.copyOf(currentFront));
            currentFront = nextFront;
        }

        return fronts.build();
    }

    private void crowdingDistanceAssignment(ImmutableList<ImmutableList<Solution>> fronts) {
        for (ImmutableList<Solution> front : fronts) {
            crowdingDistanceAssignmentSingle(front);
        }
    }

    private void crowdingDistanceAssignmentSingle(ImmutableList<Solution> front) {
        for (Solution s : front) {
            s.setCrowdingDistance(0.0);
        }
        for (int m = 0; m < NUM_OF_OBJECTIVES; m++) {
            ImmutableList<Solution> sorted = Solution.ordering(m).immutableSortedCopy(front);
            Solution first = sorted.get(0);
            Solution last = Iterables.getLast(sorted);
            first.setCrowdingDistance(Double.POSITIVE_INFINITY);
            last.setCrowdingDistance(Double.POSITIVE_INFINITY);
            double norm = first.getObjectives().get(m) - last.getObjectives().get(m);
            for (int i = 1; i < sorted.size() - 1; i++) {
                Solution s = sorted.get(i);
                double neighbors = sorted.get(i - 1).getObjectives().get(m) - sorted.get(i + 1).getObjectives().get(m);
                if (!Double.isNaN(neighbors / norm)) {//TODO CHECK!!!!!
                    s.setCrowdingDistance(s.getCrowdingDistance() + Math.abs(neighbors / norm));
                }
            }
        }
    }

    protected void recomputeBest() {
        bestOfGeneration = population.get(0).getX();
        for (Solution solution : population) {
            if (solution.getX().getFitness() > bestOfGeneration.getFitness()) {
                bestOfGeneration = solution.getX();
            }
        }
        if (bestSoFar.getFitness() < bestOfGeneration.getFitness()) {
            bestSoFar = bestOfGeneration;
            lastInnovation = 0;
            generationOfBSF = generation;
        } else {
            lastInnovation++;
        }
    }

    // --------------- REPRODUCTION -----------------------------------------------------------------

    private ImmutableList<Solution> createOffspring(ImmutableList<Solution> pop) {
        //tournament selection
        Solution[] parents = new Solution[pop.size()];
        for (int i = 0; i < pop.size(); i++) {
            int[] p1p2 = new int[2];
            RND.sampleRangeWithoutReplacement(pop.size(), p1p2);
            Solution p1 = pop.get(p1p2[0]);
            Solution p2 = pop.get(p1p2[1]);
            parents[i] = p1.betterByCrowdedComparisonThan(p2) ? p1 : p2;
        }

        ImmutableList.Builder<Solution> builder = ImmutableList.builder();
        for (int i = 0; i < pop.size(); i++) {
            Forest parent = parents[i].getX();
            Forest offspring = parent.mutate(nodeCollection, generation);
            builder.add(new Solution(offspring));
        }

        return builder.build();
    }

    private ImmutableList<Solution> reduce(int size, ImmutableList<ImmutableList<Solution>> fronts) {
        List<Solution> reduced = Lists.newArrayList();
        int i = 0;
        while (reduced.size() + fronts.get(i).size() <= size) {
            reduced.addAll(fronts.get(i));
            i++;
        }
        ImmutableList<Solution> sorted = Solution.orderingCrowdingDistance().immutableSortedCopy(fronts.get(i));
        reduced.addAll(sorted.subList(0, size - reduced.size()));
        return ImmutableList.copyOf(reduced);
    }


    public String getConfigString() {
        StringBuilder s = new StringBuilder();
        s.append("CONSTANT_AMPLITUDE = ").append(CONSTANT_AMPLITUDE);
        s.append("\nMAX_GENERATIONS = ").append(MAX_GENERATIONS);
        s.append("\nMAX_EVALUATIONS = ").append(MAX_EVALUATIONS);
        s.append("\nMAX_DEPTH = ").append(MAX_DEPTH);
        s.append("\nMUTATION_CAUCHY_PROBABILITY = ").append(MUTATION_CAUCHY_PROBABILITY);
        s.append("\nMUTATION_CAUCHY_POWER = ").append(MUTATION_CAUCHY_POWER);
        s.append("\nMUTATION_SUBTREE_PROBABLITY = ").append(MUTATION_SUBTREE_PROBABILITY);
        s.append("\nPOPULATION_SIZE = ").append(POPULATION_SIZE);
        s.append("\nTARGET_FITNESS = ").append(TARGET_FITNESS);
        s.append("\n");
        return s.toString();
    }

    @Override
    public void performGeneralizationTest() {
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public void finished() {
        populationManager.shutdown();
    }

    @Override
    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    @Override
    public int getGeneration() {
        return generation;
    }

    @Override
    public int getEvaluations() {
        return getGeneration() * population.size();
    }

    @Override
    public int getLastInnovation() {
        return lastInnovation;
    }

    @Override
    public double getMaxFitnessReached() {
        return getBestSoFar().getFitness();
    }

    @Override
    public List<EvaluationInfo> getEvaluationInfo() {
        return getEvaluationInfo(population);
    }

    public ImmutableList<EvaluationInfo> getEvaluationInfo(ImmutableList<Solution> population) {
        ImmutableList.Builder<EvaluationInfo> evaluationInfosBuilder = ImmutableList.builder();
        for (Solution solution : population) {
            evaluationInfosBuilder.add(solution.getX().getEvaluationInfo());
        }
        return evaluationInfosBuilder.build();
    }

    @Override
    public EvaluationInfo getGeneralizationEvaluationInfo() {
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public BasicInfo getPopulationInfo() {
        BasicInfo infoMap = populationManager.getPopulationInfo();
        for (String origin : origins.keySet()) {
            infoMap.put("O_" + origin, origins.get(origin));
        }
        return infoMap;
    }

    @Override
    public boolean isSolved() {
        return populationManager.isSolved();
    }

    @Override
    public List<String> getEvaluationInfoItemNames() {
        return ImmutableList.of();
    }

    @Override
    public int getGenerationOfBSF() {
        return generationOfBSF;
    }

    @Override
    public Forest getBestSoFar() {
        return bestSoFar;
    }

    @Override
    public Forest getBestOfGeneration() {
        return bestOfGeneration;
    }

    @Override
    public List<Forest> getLastGenerationPopulation() {
        return getPopulationForests(population);
    }

    @Override
    public void showBestSoFar() {
        populationManager.showBSF(getBestSoFar());
    }

    private void saveOrigin(IGPForest forest) {
        for (String origin : forest.getOrigins()) {
            if (!origins.containsKey(origin)) {
                origins.put(origin, 1L);
            } else {
                origins.put(origin, origins.get(origin) + 1L);
            }
        }
    }

    private ImmutableList<Forest> getPopulationForests(ImmutableList<Solution> population) {
        ImmutableList.Builder<Forest> evalPopulationBuilder = ImmutableList.builder();
        for (Solution solution : population) {
            evalPopulationBuilder.add(solution.getX());
        }
        return evalPopulationBuilder.build();
    }

    private void printFronts(ImmutableList<ImmutableList<Solution>> fronts) {
        System.out.println("f={");
        for (ImmutableList<Solution> front : fronts) {
            System.out.println("{");
            for (Solution s : front) {
                System.out.print(s.getObjectives().toMathematica());
                if (s != front.get(front.size() - 1)) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }
            System.out.print("}");
            if (front != fronts.get(fronts.size() - 1)) {
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        System.out.println("};");
    }

    private void printFront(ImmutableList<Solution> front) {
        System.out.print("{");
        for (Solution s : front) {
            System.out.print(s.getObjectives().toMathematica());
            if (s != front.get(front.size() - 1)) {
                System.out.print(",");
            }
        }
        System.out.println("},");
    }
}
