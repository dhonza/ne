package opt.cmaes;

import cma.CMAEvolutionStrategy;
import cma.CMAOptions;
import common.evolution.*;
import gp.Forest;
import opt.DoubleVectorGenome;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 4, 2010
 * Time: 1:45:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CMAES<P> implements EvolutionaryAlgorithm {
    private CMAEvolutionStrategy cma;

    private double[] fitness;

    private int generation;
    private int evaluations;
    private int lastInnovation;
    private double bestSolutionValue;

    final private GenotypeToPhenotype<DoubleVectorGenome, P>[] perThreadConverters;
    final private Evaluable<P>[] perThreadEvaluators;
    final private ParallelPopulationEvaluator<DoubleVectorGenome, P> populationEvaluator;

    private EvaluationInfo[] evaluationInfos;
    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    public CMAES(GenotypeToPhenotype<DoubleVectorGenome, P>[] perThreadConverters, Evaluable<P>[] perThreadEvaluators, int dimension) {
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
        populationEvaluator = new ParallelPopulationEvaluator<DoubleVectorGenome, P>();
        cma = new CMAEvolutionStrategy();
        cma.setDimension(dimension);
        cma.setInitialX(-0.3, 0.3);
        cma.setInitialStandardDeviation(0.2);
        cma.options.stopMaxFunEvals = Long.MAX_VALUE;
        cma.options.stopFitness = Double.MAX_VALUE;
        cma.options.verbosity = 2;
        bestSolutionValue = Double.MAX_VALUE;
    }

    public CMAOptions getOptions() {
        return cma.options;
    }

    private void evaluatePopulation() {
        double[][] pop = cma.samplePopulation(); // get a new population of solutions
        List<DoubleVectorGenome> evalPopulation = new ArrayList<DoubleVectorGenome>();
        for (double[] aPop : pop) {    // for each candidate solution i
            evalPopulation.add(new DoubleVectorGenome(aPop));
            evaluations++;
        }

        evaluationInfos = populationEvaluator.evaluate(perThreadConverters, perThreadEvaluators, evalPopulation);

        for (int i = 0; i < pop.length; ++i) {
            fitness[i] = -evaluationInfos[i].getFitness();
        }
    }

    private void checkForBetterSolutions() {
        if (cma.getBestFunctionValue() < bestSolutionValue) {
            bestSolutionValue = cma.getBestFunctionValue();
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }
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

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationEvaluator.evaluateGeneralization(perThreadConverters, perThreadEvaluators, new DoubleVectorGenome(getMaxReached()));
        generalizationGeneration = generation;
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
        return -cma.getBestFunctionValue();
    }

    public double[] getMaxReached() {
        return cma.getBestX();
    }

    public double getBestOfGenerationFitness() {
        return -cma.getBestRecentFunctionValue();
    }

    public EvaluationInfo[] getEvaluationInfo() {
        return evaluationInfos;
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public boolean isSolved() {
        for (Evaluable<P> evaluator : perThreadEvaluators) {
            if (evaluator.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public String getConfigString() {
        return "IMPLEMENT CMAES.getConfigString()";
    }
}
