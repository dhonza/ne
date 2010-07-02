package sneat.experiments;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.evolution.ParallelPopulationEvaluator;
import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IGenome;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.Population;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;

import java.util.ArrayList;
import java.util.List;

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
    final private Evaluable<INetwork>[] perThreadEvaluators;
    final private ParallelPopulationEvaluator<INetwork> populationEvaluator;
    public IActivationFunction activationFn;
    public long evaluationCount = 0;

    public SingleFilePopulationEvaluator(Evaluable<INetwork>[] perThreadEvaluators, IActivationFunction activationFn) {
        this.perThreadEvaluators = perThreadEvaluators;
        populationEvaluator = new ParallelPopulationEvaluator<INetwork>();
        this.activationFn = activationFn;
    }

    public void evaluatePopulation(Population pop, EvolutionAlgorithm ea) {
        // Evaluate in single-file each genome within the population.
        // Only evaluate new genomes (those with EvaluationCount==0).
        int count = pop.getGenomeList().size();
        boolean[] toEvaluate = new boolean[count];
        List<INetwork> populationToEvaluate = new ArrayList<INetwork>();
        for (int i = 0; i < count; i++) {
            IGenome g = pop.getGenomeList().get(i);
            if (g.getEvaluationCount() != 0) {
                continue;
            }
            INetwork network = g.decode(activationFn);
            if (network != null) { // Future genomes may not decode - handle the possibility.
                toEvaluate[i] = true;
                populationToEvaluate.add(network);
            } else {
                g.setFitness(EvolutionAlgorithm.MIN_GENOME_FITNESS);
                g.setEvaluationInfo(new EvaluationInfo(EvolutionAlgorithm.MIN_GENOME_FITNESS));
            }
        }

        EvaluationInfo[] evaluationInfos = populationEvaluator.evaluate(perThreadEvaluators, populationToEvaluate);

        int cnt = 0;
        for (int i = 0; i < count; i++) {
            IGenome g = pop.getGenomeList().get(i);
            //TODO dhonza fitness has to be > 0 ?;
            if (toEvaluate[i]) {
                if (evaluationInfos[cnt].getFitness() < EvolutionAlgorithm.MIN_GENOME_FITNESS) {
                    throw new IllegalStateException("CHECK this limitation of fitness value");
                }
//                g.setFitness(Math.max(evaluationInfos[cnt++].getFitness(), EvolutionAlgorithm.MIN_GENOME_FITNESS));
                g.setFitness(evaluationInfos[cnt].getFitness());
                g.setEvaluationInfo(evaluationInfos[cnt++]);
            }

            if (g.getEvaluationCount() == 0) {
                // Reset these genome level statistics.
                g.setTotalFitness(g.getFitness());
                g.setEvaluationCount(1);

                // Update master evaluation counter.
                evaluationCount++;
            }
        }
    }

    public boolean isSolved() {
        for (Evaluable<INetwork> evaluator : perThreadEvaluators) {
            if (evaluator.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public long getEvaluationCount() {
        return evaluationCount;
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

