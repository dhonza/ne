package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.ParallelPopulationEvaluator;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.Random;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.printer.GPProgressPrinter1D;
import hyper.experiments.findcluster.FindCluster;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;
import hyper.experiments.reco.problem.Recognition1D;
import hyper.substrate.Substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class GPSolver extends AbstractSolver {

    private GP gp;
    private EvolutionaryAlgorithmSolver solver;

    protected GPSolver(ParameterCombination parameters, Substrate substrate, Stats stats, ReportStorage reportStorage) {
        super(parameters, substrate, stats, reportStorage);
        init();
    }


    private void init() {
        Utils.setStaticParameters(parameters, GP.class, "GP");

        Node[] functions = NodeFactory.createByNameList("gp.functions.", parameters.getString("GP.FUNCTIONS"));
        Node[] terminals = new Node[]{new Constant(-1.0), new Random()};

        GP.TARGET_FITNESS = problem.getTargetFitness();

        gp = GPFactory.createByName(parameters.getString("GP.TYPE"), perThreadEvaluators, functions, terminals);

        solver = new EvolutionaryAlgorithmSolver(gp, stats);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, substrate, problem, parameters));
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