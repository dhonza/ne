package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.PopulationManager;
import common.evolution.SolvedStopCondition;
import common.net.INet;
import common.net.linked.Net;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.NEATProgressPrinter1D;
import hyper.evaluate.printer.ReportStorage;
import neat.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class NEATSolver extends AbstractSolver {
    private NEAT neat;
    private FitnessSharingPopulation population;
//    private DeterministicCrowdingPopulation population;

    protected NEATSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }


    private void init() {
        neat = new NEAT();
        NEATConfig config = NEAT.getConfig();
        config.targetFitness = problem.getTargetFitness();
        Net.ACTIVATION_STEPS = parameters.getInteger("NET.ACTIVATION_STEPS");
        Utils.setParameters(parameters, config, "NEAT");

        population = new FitnessSharingPopulation<INet>(populationManager, getPrototype(populationManager, parameters));
//        population = new DeterministicCrowdingPopulation<INet>(populationManager, getPrototype(populationManager));

        neat.setPopulation(population);

        solver = new EvolutionaryAlgorithmSolver(neat, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new NEATProgressPrinter1D(neat, problem, reportStorage, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(neat, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(neat));
        solver.addStopCondition(new MaxEvaluationsStopCondition(neat));
        solver.addStopCondition(new TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(populationManager));
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

    public void solve() {
        solver.run();
        extractStats(stats, neat);
    }

    public String getConfigString() {
        return neat.getConfigString();
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
