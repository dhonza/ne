package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.ParallelPopulationEvaluator;
import common.net.INet;
import common.net.linked.Net;
import common.net.linked.Neuron;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.evaluate.printer.NEATProgressPrinter1D;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;
import neat.*;

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

    protected NEATSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }


    private void init() {
        neat = new NEAT();
        NEATConfig config = NEAT.getConfig();
        config.targetFitness = problem.getTargetFitness();
        Utils.setParameters(parameters, config, "NEAT");

        population = new FitnessSharingPopulation<INet>(populationEvaluator, getPrototype(populationEvaluator));

        neat.setPopulation(population);

        solver = new EvolutionaryAlgorithmSolver(neat, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new NEATProgressPrinter1D(neat, problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(neat, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(neat));
        solver.addStopCondition(new MaxEvaluationsStopCondition(neat));
        solver.addStopCondition(new TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(problem));
    }

    private static Genome getPrototype(ParallelPopulationEvaluator<Genome, INet> populationEvaluator) {
        Net net = new Net(1);
        net.createFeedForward(populationEvaluator.getNumberOfInputs(), new int[]{}, populationEvaluator.getNumberOfOutputs());
        for (int i = 0; i < populationEvaluator.getNumberOfOutputs(); i++) {
            net.getOutputs().get(i).setActivation(Neuron.Activation.BIPOLAR_SIGMOID);
//            net.getOutputs().get(i).setActivation(Neuron.Activation.LINEAR);
        }
        net.randomizeWeights(-0.3, 0.3);
        return new Genome(net);
    }

    public void solve() {
        solver.run();
    }

    public String getConfigString() {
        return neat.getConfigString();
    }
}
