package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.GenotypeToPhenotype;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.NeatParameters;
import sneat.experiments.AbstractExperimentView;
import sneat.experiments.IExperiment;
import sneat.experiments.SingleFilePopulationEvaluator;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.activationfunctions.SteepenedSigmoid;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Apr 28, 2010
 * Time: 7:45:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATExperiment<P> implements IExperiment {
    final private ParameterCombination parameters;
    final private GenotypeToPhenotype<INetwork, P>[] perThreadConverters;
    final private Evaluable<P>[] perThreadEvaluators;
    final private double targetFitness;
    NeatParameters neatParams = null;

    IPopulationEvaluator populationEvaluator = null;
    IActivationFunction activationFunction = new SteepenedSigmoid();

    public SNEATExperiment(ParameterCombination parameters, GenotypeToPhenotype<INetwork, P>[] perThreadConverters, Evaluable<P>[] perThreadEvaluators, double targetFitness) {
        this.parameters = parameters;
        this.perThreadConverters = perThreadConverters;
        this.perThreadEvaluators = perThreadEvaluators;
        this.targetFitness = targetFitness;
    }

    public IPopulationEvaluator getPopulationEvaluator() {
        if (populationEvaluator == null) {
            resetEvaluator(null);
        }
        return populationEvaluator;
    }

    public void resetEvaluator(IActivationFunction activationFn) {
        populationEvaluator = new SingleFilePopulationEvaluator(perThreadConverters, perThreadEvaluators, null);
    }

    public int getInputNeuronCount() {
        return perThreadEvaluators[0].getNumberOfInputs();
    }

    public int getOutputNeuronCount() {
        return perThreadEvaluators[0].getNumberOfOutputs();
    }

    public NeatParameters getDefaultNeatParameters() {
        if (neatParams == null) {
            NeatParameters np = new NeatParameters();
            np.targetFitness = targetFitness;
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