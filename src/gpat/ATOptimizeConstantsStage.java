package gpat;

import cma.CMAEvolutionStrategy;
import common.evolution.EvaluationInfo;
import common.evolution.PopulationManager;
import gp.IGPForest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/23/11
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATOptimizeConstantsStage<P> {
    final private PopulationManager<IGPForest, P> populationManager;
    final private IGPForest seedGenome;
    final private int dimension;
    final private CMAEvolutionStrategy cma;

    private double[] fitness;

    private int generation;
    private int evaluations;
    private int lastInnovation;
    private double bestSolutionValue;

    public ATOptimizeConstantsStage(PopulationManager<IGPForest, P> populationManager, IGPForest seedGenome) {
        this.populationManager = populationManager;
        this.seedGenome = seedGenome;
        this.cma = new CMAEvolutionStrategy();
        this.dimension = seedGenome.getConstants().length;
        cma.setDimension(dimension);
        cma.setInitialX(-0.3, 0.3);
        cma.setInitialStandardDeviation(0.2);
        cma.options.stopMaxFunEvals = Long.MAX_VALUE;
        cma.options.stopFitness = Double.MAX_VALUE;
//        cma.options.verbosity = 2;
        cma.options.verbosity = 0;
        bestSolutionValue = Double.MAX_VALUE;
    }

    public IGPForest optimize() {
        initialGeneration();
        for (int i = 0; i < 10; i++) {
            nextGeneration();
        }

        seedGenome.setConstants(cma.getBestX());

        seedGenome.setFitness(-cma.getBestFunctionValue());
        seedGenome.setEvaluationInfo(new EvaluationInfo(-cma.getBestFunctionValue()));

        return seedGenome;
    }

    private void evaluatePopulation() {
        double[][] pop = cma.samplePopulation(); // get a new population of solutions
        List<IGPForest> evalPopulation = new ArrayList<IGPForest>();
        for (double[] aPop : pop) {    // for each candidate solution i
            IGPForest copy = seedGenome.copy();
            copy.setConstants(aPop);
//            System.out.println(copy);
            evalPopulation.add(copy);
            evaluations++;
        }

        populationManager.loadGenotypes(evalPopulation);
        List<EvaluationInfo> evaluationInfos = populationManager.evaluateNoDistances();

        for (int i = 0; i < pop.length; ++i) {
            fitness[i] = -evaluationInfos.get(i).getFitness();
        }
    }

    private void checkForBetterSolutions() {
        if (cma.getBestFunctionValue() < bestSolutionValue) {
            bestSolutionValue = cma.getBestFunctionValue();
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }
        System.out.println("bestSolutionValue = " + bestSolutionValue);
    }

    public void initialGeneration() {
        generation = 1;
        // initialize cma and get fitness array to fill in later
        fitness = cma.init();
        evaluatePopulation();
        cma.updateDistribution(fitness);
        checkForBetterSolutions();
    }

    public void nextGeneration() {
        generation++;
        evaluatePopulation();
        cma.updateDistribution(fitness);
        checkForBetterSolutions();
    }
}
