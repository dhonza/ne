package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.Random;
import hyper.builder.NetSubstrateBuilder;
import hyper.evaluate.printer.GPProgressPrinter1D;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class GPSolver implements Solver {
    final private ParameterCombination parameters;
    final private NetSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;

    public GPSolver(ParameterCombination parameters, NetSubstrateBuilder substrateBuilder, Stats stats, Problem problem) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
    }

    public void solve() {
        GP.TARGET_FITNESS = problem.getTargetFitness();
        Utils.setStaticParameters(parameters, GP.class, "GP");

        Node[] functions = NodeFactory.createByNameList("gp.functions.", parameters.getString("GP.FUNCTIONS"));
        Node[] terminals = new Node[]{new Constant(-1.0), new Random()};

        GPEvaluator evaluator = new GPEvaluator(substrateBuilder, problem);

        GP gp = GPFactory.createByName(parameters.getString("GP.TYPE"), evaluator, functions, terminals);

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(gp);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, substrateBuilder.getSubstrate(), problem));
        solver.addStopCondition(new LastGenerationStopCondition(gp));
        solver.addStopCondition(new TargetFitnessStopCondition(gp));
        solver.addStopCondition(new SolvedStopCondition(problem));
        solver.run();

        stats.addSample("STAT_GENERATIONS", gp.getGeneration());
    }
}