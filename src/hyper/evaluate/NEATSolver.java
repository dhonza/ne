package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.builder.NEATSubstrateBuilder;
import hyper.evaluate.printer.NEATProgressPrinter1D;
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

    public NEATSolver(ParameterCombination parameters, NEATSubstrateBuilder substrateBuilder, Stats stats, Problem problem) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
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
        NEATEvaluator evaluator = new NEATEvaluator(substrateBuilder, problem);

        NEAT neat = new NEAT();
        NEATConfig config = NEAT.getConfig();
        config.targetFitness = problem.getTargetFitness();
        Utils.setParameters(parameters, config, "NEAT");

        FitnessSharingPopulation population = new FitnessSharingPopulation(evaluator, getPrototype(evaluator));

        neat.setPopulation(population);

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(neat);
        solver.addProgressPrinter(new NEATProgressPrinter1D(neat, substrateBuilder.getSubstrate(), problem, parameters));
        solver.addStopCondition(new LastGenerationStopCondition(neat));
        solver.addStopCondition(new TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(problem));
        solver.run();

        stats.addSample("STAT_GENERATIONS", population.getGeneration());
    }
}
