package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import hyper.builder.NEATSubstrateBuilder;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.NeatParameters;
import sneat.experiments.AbstractExperimentView;
import sneat.experiments.IExperiment;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.activationfunctions.SteepenedSigmoid;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 7:45:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATExperiment implements IExperiment {
    private final int numOfInputs;
    private final int numOfOutputs;
    private final ParameterCombination parameters;
    private final NEATSubstrateBuilder substrateBuilder;
    private final Problem problem;

    NeatParameters neatParams = null;

    IPopulationEvaluator populationEvaluator = null;
    IActivationFunction activationFunction = new SteepenedSigmoid();

    public SNEATExperiment(ParameterCombination parameters, NEATSubstrateBuilder substrateBuilder, Problem problem, int numOfInputs, int numOfOutputs) {
        this.parameters = parameters;
        this.numOfInputs = numOfInputs;
        this.numOfOutputs = numOfOutputs;
        this.substrateBuilder = substrateBuilder;
        this.problem = problem;
    }

    public IPopulationEvaluator getPopulationEvaluator() {
        if (populationEvaluator == null) {
            resetEvaluator(null);
        }
        return populationEvaluator;
    }

    public void resetEvaluator(IActivationFunction activationFn) {
        populationEvaluator = new SingleFilePopulationEvaluator(new SNEATNetworkEvaluator(substrateBuilder, problem), null);
    }

    public int getInputNeuronCount() {
        return numOfInputs;
    }

    public int getOutputNeuronCount() {
        return numOfOutputs;
    }

    public NeatParameters getDefaultNeatParameters() {
        if (neatParams == null) {
            NeatParameters np = new NeatParameters();
            np.targetFitness = problem.getTargetFitness();
            Utils.setParameters(parameters, np, "SNEAT");
            np.activationProbabilities = new double[4];
            np.activationProbabilities[0] = .25;
            np.activationProbabilities[1] = .25;
            np.activationProbabilities[2] = .25;
            np.activationProbabilities[3] = .25;

            np.pruningPhaseBeginComplexityThreshold = Float.MAX_VALUE;
            np.pruningPhaseBeginFitnessStagnationThreshold = Integer.MAX_VALUE;
//                    np.pruningPhaseEndComplexityStagnationThreshold = Integer.MinValue;
            np.pruningPhaseEndComplexityStagnationThreshold = Integer.MIN_VALUE;
            np.targetSpeciesCountMax = np.populationSize / 10;
            np.targetSpeciesCountMin = np.populationSize / 10 - 2;
            neatParams = np;
        }
        return neatParams;
    }

    public IActivationFunction getSuggestedActivationFunction() {
        return activationFunction;
    }

    public AbstractExperimentView getCreateExperimentView() {
        return null;
    }

    public String getExplanatoryText() {
        return "Generic SNEAT Problem";
    }
}