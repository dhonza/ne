package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.NetSubstrateBuilder;
import hyper.evaluate.printer.NetProgressPrinter1D;
import neat.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * mela by dostat z venku: parametry NEAT, progress printer (stejy pro GP)
 */
public class NEATSolver implements Solver {
    final private ParameterCombination parameters;
    final private NetSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;

    public NEATSolver(ParameterCombination parameters, NetSubstrateBuilder substrateBuilder, Stats stats, Problem problem) {
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
        config.populationSize = 1000;
        config.lastGeneration = 15000;
        config.netWeightsAmplitude = 10.0;
        config.targetFitness = problem.getTargetFitness();

        FitnessSharingPopulation population = new FitnessSharingPopulation(evaluator, getPrototype(evaluator));

        neat.setPopulation(population);

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(neat);
        solver.addProgressPrinter(new NetProgressPrinter1D(neat, substrateBuilder.getSubstrate(), problem));
        solver.addStopCondition(new LastGenerationStopCondition(neat));
        solver.addStopCondition(new TargetFitnessStopCondition(neat));
        solver.addStopCondition(new SolvedStopCondition(problem));
        solver.run();

        stats.addSample("STAT_GENERATIONS", population.getGeneration());
    }
}
