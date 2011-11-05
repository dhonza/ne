package common.run;

import common.evolution.*;
import common.net.INet;
import common.net.linked.Net;
import common.net.linked.Neuron;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.EvaluableFactory;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.DummyProblem;
import neat.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATRunner implements EvolutionaryAlgorithmRunner {
    private final ParameterCombination parameters;
    private final PopulationManager<Genome, INet> populationManager;
    private NEAT neat;

    public NEATRunner(ParameterCombination parameters) {
        this.parameters = parameters;
        populationManager = createPopulationManager(parameters);
        neat = createAlgorithm(parameters, populationManager);
    }

    public void run(Stats stats, ReportStorage reportStorage) {
        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(neat, stats, false);
        solver.addProgressPrinter(new NEATBasicProgressPrinter(neat));
        solver.addProgressPrinter(new FileProgressPrinter(neat, new DummyProblem(), reportStorage, parameters));
        solver.addStopCondition(new neat.MaxGenerationsStopCondition(neat));
        solver.addStopCondition(new neat.MaxEvaluationsStopCondition(neat));
        solver.addStopCondition(new neat.TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(populationManager));

        solver.run();

        extractStats(stats, neat);
    }

    private static PopulationManager<Genome, INet> createPopulationManager(ParameterCombination combination) {
        boolean parallel = combination.getBoolean("PARALLEL");
        int threads = 1;
        if (parallel) {
            if (combination.contains("PARALLEL.FORCE_THREADS")) {
                threads = combination.getInteger("PARALLEL.FORCE_THREADS");
            } else {
                threads = PopulationManager.getNumberOfThreads();
            }
        }

        List<IGenotypeToPhenotype<Genome, INet>> converter = new ArrayList<IGenotypeToPhenotype<Genome, INet>>(threads);
        List<IEvaluable<INet>> evaluator = new ArrayList<IEvaluable<INet>>(threads);

        for (int i = (threads - 1); i >= 0; i--) {
            converter.add(new GenomeConversion());
            IEvaluable<INet> evaluable = EvaluableFactory.createByName(combination);
            evaluator.add(evaluable);
        }

        PopulationManager<Genome, INet> populationManager = new PopulationManager<Genome, INet>(
                combination, converter, evaluator);
        return populationManager;
    }

    private static NEAT createAlgorithm(ParameterCombination parameters, PopulationManager populationManager) {
        NEAT neat = new NEAT();
        NEATConfig config = NEAT.getConfig();
        Neuron.Activation.setFunctions(parameters);
        config.targetFitness = parameters.getDouble("GP.TARGET_FITNESS");
        Net.ACTIVATION_STEPS = parameters.getInteger("NET.ACTIVATION_STEPS");
        Utils.setParameters(parameters, config, "NEAT");

        Population population = new FitnessSharingPopulation<INet>(populationManager, getPrototype(populationManager, parameters));
//        population = new DeterministicCrowdingPopulation<INet>(populationManager, getPrototype(populationManager));

        neat.setPopulation(population);

        return neat;
    }

    private static Genome getPrototype(PopulationManager<Genome, INet> populationManager, ParameterCombination parameters) {
        Net net = new Net(1);
        net.createFeedForward(populationManager.getNumberOfInputs(), new int[]{}, populationManager.getNumberOfOutputs());
        for (int i = 0; i < populationManager.getNumberOfOutputs(); i++) {
            net.getOutputNodes().get(i).setActivation(OutputNeuronTypeFactory.getOutputNeuron(parameters));
        }
        net.randomizeWeights(-0.3, 0.3);
        return new Genome(net);
    }

    private static void extractStats(Stats stats, NEAT neat) {
        List<Genome> lastGeneration = neat.getLastGenerationPopulation();
//        double arityLG = 0.0;
//        double constantsLG = 0.0;
//        double depthLG = 0.0;
//        double leavesLG = 0.0;
        double nodesLG = 0.0;
        for (Genome genome : lastGeneration) {
//            arityLG += forest.getAverageArity();
//            constantsLG += forest.getNumOfConstants();
//            depthLG += forest.getMaxTreeDepth();
//            leavesLG += forest.getNumOfLeaves();
            //TODO use only Hidden or add Input?
            nodesLG += genome.getNet().getNumHidOut();
        }
//        arityLG /= lastGeneration.size();
//        constantsLG /= lastGeneration.size();
//        depthLG /= lastGeneration.size();
//        leavesLG /= lastGeneration.size();
        nodesLG /= lastGeneration.size();
        //TODO repair

        stats.addSample("BSF", neat.getMaxFitnessReached());
        stats.addSample("BSFG", neat.getGenerationOfBSF());
        stats.addSample("ARITY_BSF", 0.0);
        stats.addSample("ARITY_LG", 0.0);
        stats.addSample("CONSTANTS_BSF", 0.0);
        stats.addSample("CONSTANTS_LG", 0.0);
        stats.addSample("DEPTH_BSF", 0.0);
        stats.addSample("DEPTH_LG", 0.0);
        stats.addSample("LEAVES_BSF", 0.0);
        stats.addSample("LEAVES_LG", 0.0);
        stats.addSample("NODES_BSF", (double) neat.getBestSoFar().getNet().getNumHidOut());
        stats.addSample("NODES_LG", nodesLG);
    }
}
