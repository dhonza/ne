package sneat.experiments;

import common.evolution.Evaluable;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.NeatParameters;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.activationfunctions.SteepenedSigmoid;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 7:45:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class XORExperiment implements IExperiment {
    private final ParameterCombination parameters;
    NeatParameters neatParams = null;

    IPopulationEvaluator populationEvaluator = null;
    IActivationFunction activationFunction = new SteepenedSigmoid();

    public XORExperiment(ParameterCombination parameters) {
        this.parameters = parameters;
    }

    public IPopulationEvaluator getPopulationEvaluator() {
        if (populationEvaluator == null) {
            resetEvaluator(null);
        }
        return populationEvaluator;
    }

    public void resetEvaluator(IActivationFunction activationFn) {
        populationEvaluator = new SingleFilePopulationEvaluator(new Evaluable[]{new XORNetworkEvaluator()}, null);
    }

    public int getInputNeuronCount() {
        return 2;
    }

    public int getOutputNeuronCount() {
        return 1;
    }

    public NeatParameters getDefaultNeatParameters() {
        if (neatParams == null) {
            NeatParameters np = new NeatParameters();
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
        return "XOR Problem";
    }
}
