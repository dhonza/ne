package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.builder.NEATSubstrateBuilder;
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

public class NEATSolver implements Solver {
    final private ParameterCombination parameters;
    final private NEATSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;
    final private ReportStorage reportStorage;

    private NEAT neat;
    private FitnessSharingPopulation population;
    private EvolutionaryAlgorithmSolver solver;

    public NEATSolver(ParameterCombination parameters, NEATSubstrateBuilder substrateBuilder, Stats stats, Problem problem, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
        this.reportStorage = reportStorage;
        init();
    }

    private void init() {
        NEATEvaluator evaluator = new NEATEvaluator(substrateBuilder, problem);

        neat = new NEAT();
        NEATConfig config = NEAT.getConfig();
        config.targetFitness = problem.getTargetFitness();
        Utils.setParameters(parameters, config, "NEAT");

        population = new FitnessSharingPopulation(evaluator, getPrototype(evaluator));

        neat.setPopulation(population);

        solver = new EvolutionaryAlgorithmSolver(neat, stats);
        solver.addProgressPrinter(new NEATProgressPrinter1D(neat, substrateBuilder.getSubstrate(), problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(neat, reportStorage));
        solver.addStopCondition(new MaxGenerationsStopCondition(neat));
        solver.addStopCondition(new MaxEvaluationsStopCondition(neat));
        solver.addStopCondition(new TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(problem));
    }

    private static Genome getPrototype(Evaluable evaluator) {
        Net net = new Net(1);
        net.createFeedForward(evaluator.getNumberOfInputs(), new int[]{}, evaluator.getNumberOfOutputs());
        for (int i = 0; i < evaluator.getNumberOfOutputs(); i++) {
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
