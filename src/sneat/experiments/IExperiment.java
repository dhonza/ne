package sneat.experiments;

import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.NeatParameters;
import sneat.neuralnetwork.IActivationFunction;

import java.util.Map;

public interface IExperiment {
    /// <summary>
    /// The IPopulationEvaluator to use for the experiment. This is passed to the
    /// constructor of EvolutionAlgorithm.
    /// </summary>
    IPopulationEvaluator getSinglePopulationEvaluator();

    /// <summary>
    /// This is called prior to constructing a new EvolutionAlgorithm to ensure we have a
    /// fresh evaluator - some evaluators have state.
    /// </summary>
    /// <param name="activationFn"></param>

    void resetEvaluator(IActivationFunction activationFn);

    /// <summary>
    /// The number of input neurons required for an experiment. This figure is used
    /// to generate a population of genomes with the correct number of inputs.
    /// </summary>
    int getInputNeuronCount();

    /// <summary>
    /// The number of output neurons required for an experiment. This figure is used
    /// to generate a population of genomes with the correct number of outputs.
    /// </summary>
    int getOutputNeuronCount();

    /// <summary>
    /// The default NeatParameters object to use for the experiment.
    /// </summary>
    NeatParameters getDefaultNeatParameters();

    /// <summary>
    /// This is the suggested netowkr activation function for an experiment. The default
    /// activation function is shown within SharpNEAT's GUI and can be overriden by
    /// selecting an alternative function in the drop-down combobox.
    /// </summary>
    IActivationFunction getSuggestedActivationFunction();

    /// <summary>
    /// Returns a Form based view of the experiment. It is accetable to return null to
    /// indicate that no view is available.
    /// </summary>
    /// <returns></returns>
    AbstractExperimentView getCreateExperimentView();

    /// <summary>
    /// A description of the evaluator and domain to aid new users.
    /// </summary>
    String getExplanatoryText();
}
