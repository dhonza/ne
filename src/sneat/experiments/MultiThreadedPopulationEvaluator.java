package sneat.experiments;

import sneat.evolution.EvolutionAlgorithm;
import sneat.evolution.IGenome;
import sneat.evolution.IPopulationEvaluator;
import sneat.evolution.Population;
import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.INetwork;

import java.util.concurrent.Semaphore;

/// <summary>
/// An implementation of IPopulationEvaluator that evaluates all new genomes(EvaluationCount==0)
/// within the population using multiple threads, using an INetworkEvaluator provided at construction time.
///

/// This class provides an IPopulationEvaluator for use within the EvolutionAlgorithm by simply
/// providing an INetworkEvaluator to its constructor. This usage is intended for experiments
/// where the genomes are evaluated independently of each other (e.g. not simultaneoulsy in
/// a simulated world) using a fixed evaluation function that can be described by an INetworkEvaluator.
/// </summary>
public class MultiThreadedPopulationEvaluator implements IPopulationEvaluator {

    INetworkEvaluator networkEvaluator;
    IActivationFunction activationFn;
    private static Semaphore sem = new Semaphore(HyperNEATParameters.numThreads);
    private static Semaphore sem2 = new Semaphore(1);

    long evaluationCount = 0;

    public MultiThreadedPopulationEvaluator(INetworkEvaluator networkEvaluator, IActivationFunction activationFn) {
        this.networkEvaluator = networkEvaluator;
        this.activationFn = activationFn;

    }

    public void evaluatePopulation(Population pop, EvolutionAlgorithm ea) {
        int count = pop.getGenomeList().size();

        evalPack e;
        IGenome g;
        int i;

        for (i = 0; i < count; i++) {


            sem.acquireUninterruptibly();
            g = pop.getGenomeList().get(i);
            e = new evalPack(networkEvaluator, activationFn, g);

            System.out.println("ThreadPool not ported!");
            System.exit(1);
//            ThreadPool.QueueUserWorkItem(new WaitCallback(evalNet), e);

            // Update master evaluation counter.
            evaluationCount++;

        }

        for (int j = 0; j < HyperNEATParameters.numThreads; j++) {
            sem.acquireUninterruptibly();
        }
        for (int j = 0; j < HyperNEATParameters.numThreads; j++) {
            sem.release();
        }


    }

    public boolean isSolved() {
        return networkEvaluator.isSolved();
    }

    public long getEvaluationCount() {
        return evaluationCount;
    }

    public String getEvaluatorStateMessage() {    // Pass on the network evaluator's message.
        return networkEvaluator.getEvaluatorStateMessage();
    }

    public boolean getBestIsIntermediateChampion() {    // Only relevant to incremental evolution experiments.
        return false;
    }

    public boolean getSearchCompleted() {    // This flag is not yet supported in the main search algorithm.
        return false;
    }

    public static void evalNet(Object input) {

        evalPack e = (evalPack) input;

        if (e.getG() == null)//|| e.getG().EvaluationCount != 0)
        {
            sem.release();
            return;
        }
        sem2.acquireUninterruptibly();
        INetwork network = e.getG().decode(e.getActivation());
        sem2.release();
        if (network == null) {    // Future genomes may not decode - handle the possibility.
            e.getG().setFitness(EvolutionAlgorithm.MIN_GENOME_FITNESS);
        } else {
            e.getG().setFitness(Math.max(e.getNetworkEvaluator().threadSafeEvaluateNetwork(network, sem2), EvolutionAlgorithm.MIN_GENOME_FITNESS));
        }

        // Reset these genome level statistics.
        e.getG().setTotalFitness(e.getG().getTotalFitness() + e.getG().getFitness());
        e.getG().setEvaluationCount(e.getG().getEvaluationCount() + 1);
        sem.release();

    }

    class evalPack {

        INetworkEvaluator networkEvaluator;
        IActivationFunction activationFn;
        IGenome genome;

        public evalPack(INetworkEvaluator n, IActivationFunction a, IGenome g) {

            networkEvaluator = n;
            activationFn = a;
            genome = g;

        }

        public INetworkEvaluator getNetworkEvaluator() {
            return networkEvaluator;
        }

        public IActivationFunction getActivation() {
            return activationFn;
        }

        public IGenome getG() {
            return genome;
        }
    }
}

