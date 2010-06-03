package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.Random;
import hyper.builder.NEATSubstrateBuilder;
import hyper.evaluate.printer.GPProgressPrinter1D;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class GPSolver implements Solver {
    final private ParameterCombination parameters;
    final private NEATSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;
    final private ReportStorage reportStorage;

    private GP gp;
    private EvolutionaryAlgorithmSolver solver;

    public GPSolver(ParameterCombination parameters, NEATSubstrateBuilder substrateBuilder, Stats stats, Problem problem, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
        this.reportStorage = reportStorage;
        init();
    }

    private void init() {
        GP.TARGET_FITNESS = problem.getTargetFitness();
        Utils.setStaticParameters(parameters, GP.class, "GP");

        Node[] functions = NodeFactory.createByNameList("gp.functions.", parameters.getString("GP.FUNCTIONS"));
        Node[] terminals = new Node[]{new Constant(-1.0), new Random()};

        GPEvaluator evaluator = new GPEvaluator(substrateBuilder, problem);

        gp = GPFactory.createByName(parameters.getString("GP.TYPE"), evaluator, functions, terminals);

        solver = new EvolutionaryAlgorithmSolver(gp, stats);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, substrateBuilder.getSubstrate(), problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(gp, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(gp));
        solver.addStopCondition(new MaxEvaluationsStopCondition(gp));
        solver.addStopCondition(new TargetFitnessStopCondition(gp));
        solver.addStopCondition(new SolvedStopCondition(problem));
    }

    public void solve() {

        solver.run();
    }

    public String getConfigString() {
        return gp.getConfigString();
    }
}