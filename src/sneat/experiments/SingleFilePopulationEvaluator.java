package sneat.experiments;

import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IGenome;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.Population;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;

/// <summary>
/// An implementation of IPopulationEvaluator that evaluates all new genomes(EvaluationCount==0)
/// within the population in single-file, using an INetworkEvaluator provided at construction time.
///

/// This class provides an IPopulationEvaluator for use within the EvolutionAlgorithm by simply
/// providing an INetworkEvaluator to its constructor. This usage is intended for experiments
/// where the genomes are evaluated independently of each other (e.g. not simultaneoulsy in
/// a simulated world) using a fixed evaluation function that can be described by an INetworkEvaluator.
/// </summary>
public class SingleFilePopulationEvaluator implements IPopulationEvaluator {
    public INetworkEvaluator networkEvaluator;
    public IActivationFunction activationFn;
    public long evaluationCount = 0;

    public SingleFilePopulationEvaluator() {
    }

    public SingleFilePopulationEvaluator(INetworkEvaluator networkEvaluator, IActivationFunction activationFn) {
        this.networkEvaluator = networkEvaluator;
        this.activationFn = activationFn;
    }

    public void evaluatePopulation(Population pop, EvolutionAlgorithm ea) {
        // Evaluate in single-file each genome within the population.
        // Only evaluate new genomes (those with EvaluationCount==0).
        int count = pop.getGenomeList().size();
        for (int i = 0; i < count; i++) {
            IGenome g = pop.getGenomeList().get(i);
            if (g.getEvaluationCount() != 0)
                continue;

            INetwork network = g.decode(activationFn);
            if (network == null) {    // Future genomes may not decode - handle the possibility.
                g.setFitness(EvolutionAlgorithm.MIN_GENOME_FITNESS);
            } else {
                g.setFitness(Math.max(networkEvaluator.evaluateNetwork(network), EvolutionAlgorithm.MIN_GENOME_FITNESS));
            }

            // Reset these genome level statistics.
            g.setTotalFitness(g.getFitness());
            g.setEvaluationCount(1);

            // Update master evaluation counter.
            evaluationCount++;
        }
    }

    public boolean isSolved() {
        return networkEvaluator.isSolved();
    }

    public long getEvaluationCount() {
        return evaluationCount;
    }

    public String getEvaluatorStateMessage() {
        return networkEvaluator.getEvaluatorStateMessage();
    }

    // Only relevant to incremental evolution experiments.
    public boolean getBestIsIntermediateChampion() {
        return false;
    }


    // This flag is not yet supported in the main search algorithm.
    public boolean getSearchCompleted() {
        return false;
    }
}

