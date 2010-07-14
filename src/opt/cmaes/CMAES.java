package opt.cmaes;

import cma.CMAEvolutionStrategy;
import cma.CMAOptions;
import common.evolution.EvaluationInfo;
import common.evolution.EvolutionaryAlgorithm;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 4, 2010
 * Time: 1:45:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMAES implements EvolutionaryAlgorithm {
    final private CMAESObjectiveFunction function;

    private CMAEvolutionStrategy cma;

    private double[] fitness;

    private int generation;
    private int evaluations;
    private int lastInnovation;
    private double bestSolutionValue;

    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    public CMAES(CMAESObjectiveFunction function) {
        this.function = function;
        cma = new CMAEvolutionStrategy();
        cma.setDimension(function.getDim());
        cma.setInitialX(-0.3, 0.3);
        cma.setInitialStandardDeviation(0.2);
        cma.options.stopMaxFunEvals = 1000000;
        cma.options.stopFitness = 1E-1;
        cma.options.verbosity = 2;
        bestSolutionValue = -Double.MAX_VALUE;
    }

    public CMAOptions getOptions() {
        return cma.options;
    }

    public void initialGeneration() {
        generation = 0;
        // initialize cma and get fitness array to fill in later
        fitness = cma.init();
    }

    public void nextGeneration() {
        // --- core iteration step ---
        double[][] pop = cma.samplePopulation(); // get a new population of solutions
        for (int i = 0; i < pop.length; ++i) {    // for each candidate solution i
            fitness[i] = function.valueOf(pop[i]); // fitfun.valueOf() is to be minimized
            evaluations++;
        }
        cma.updateDistribution(fitness);         // pass fitness array to update search distribution
        // --- end core iteration step ---

        if (cma.getBestFunctionValue() > bestSolutionValue) {
            bestSolutionValue = cma.getBestFunctionValue();
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }

        generation++;
    }

    public void performGeneralizationTest() {
        throw new IllegalStateException("Not yet implemented!: CMAES.performGeneralizationTest()");
    }

    public void finished() {
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public double getMaxFitnessReached() {
        return cma.getBestFunctionValue();
    }

    public double[] getMaxReached() {
        return cma.getBestX();
    }

    public double getBestOfGenerationFitness() {
        return cma.getBestRecentFunctionValue();
    }

    public EvaluationInfo[] getEvaluationInfo() {
        System.out.println("EvaluationInfo!!!!!!");
        return new EvaluationInfo[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public boolean isSolved() {
        return function.isSolved();
    }

    public String getConfigString() {
        return "IMPLEMENT CMAES.getConfigString()";
    }
}
