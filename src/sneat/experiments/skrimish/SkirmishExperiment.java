package sneat.experiments.skrimish;

import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.NeatParameters;
import sneat.experiments.AbstractExperimentView;
import sneat.experiments.HyperNEATParameters;
import sneat.experiments.IExperiment;
import sneat.neuralnetwork.IActivationFunction;

import java.util.Map;

public class SkirmishExperiment implements IExperiment {
    int inputs;
    int outputs;
    public static boolean multiple;
    IPopulationEvaluator populationEvaluator = null;
    NeatParameters neatParams = null;
    String shape;

    public SkirmishExperiment(int ins, int outs, boolean isMulti, String s) {
        inputs = ins;
        outputs = outs;
        multiple = isMulti;
        shape = s;

        //TODO A nebylo tu
        resetEvaluator(HyperNEATParameters.substrateActivationFunction);
    }

    public void loadExperimentParameters(Map parameterTable) {
        throw new IllegalStateException("The method or operation is not implemented.");
    }

    public IPopulationEvaluator PopulationEvaluator() {
        if (populationEvaluator == null)
            resetEvaluator(HyperNEATParameters.substrateActivationFunction);

        return populationEvaluator;
    }

    public void resetEvaluator(IActivationFunction activationFn) {
        if (multiple)
            populationEvaluator = new SkirmishPopulationEvaluator(new SkirmishNetworkEvaluator(5, shape));
        else
            populationEvaluator = new SkirmishPopulationEvaluator(new SkirmishNetworkEvaluator(1, shape));
    }

    public IPopulationEvaluator getPopulationEvaluator() {
        return populationEvaluator;
    }

    public int getInputNeuronCount() {
        return inputs;
    }

    public int getOutputNeuronCount() {
        return outputs;
    }

    public NeatParameters getDefaultNeatParameters() {
        if (neatParams == null) {
            NeatParameters np = new NeatParameters();
            np.connectionWeightRange = 3;
            np.pMutateAddConnection = .03;
            np.pMutateAddNode = .01;
            np.pMutateConnectionWeights = .96;
            np.pMutateDeleteConnection = 0;
            np.pMutateDeleteSimpleNeuron = 0;
            np.activationProbabilities = new double[4];
            np.activationProbabilities[0] = .25;
            np.activationProbabilities[1] = .25;
            np.activationProbabilities[2] = .25;
            np.activationProbabilities[3] = .25;
            np.populationSize = 150;
            np.pruningPhaseBeginComplexityThreshold = Float.MAX_VALUE;
            np.pruningPhaseBeginFitnessStagnationThreshold = Integer.MAX_VALUE;
//                    np.pruningPhaseEndComplexityStagnationThreshold = Integer.MinValue;
            np.pruningPhaseEndComplexityStagnationThreshold = Integer.MIN_VALUE;
            np.pInitialPopulationInterconnections = 1;
            np.elitismProportion = .1;
            np.targetSpeciesCountMax = np.populationSize / 10;
            np.targetSpeciesCountMin = np.populationSize / 10 - 2;
            np.selectionProportion = .8;
            neatParams = np;
        }
        return neatParams;
    }

    public IActivationFunction getSuggestedActivationFunction()

    {
        return HyperNEATParameters.substrateActivationFunction;
    }

    public AbstractExperimentView getCreateExperimentView() {
        return null;
    }

    public String getExplanatoryText() {
        return "A HyperNEAT experiemnt for multiagent predator-prey";
    }
}
