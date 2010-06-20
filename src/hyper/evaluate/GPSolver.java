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

public class GPSolver implements Solver {
    final private ParameterCombination parameters;
    final private Substrate substrate;
    final private Stats stats;
    final private ReportStorage reportStorage;

    private GP gp;
    private EvolutionaryAlgorithmSolver solver;

    public GPSolver(ParameterCombination parameters, Substrate substrate, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.substrate = substrate;
        this.stats = stats;
        this.reportStorage = reportStorage;
        init();
    }

    private void init() {
        Utils.setStaticParameters(parameters, GP.class, "GP");

        Node[] functions = NodeFactory.createByNameList("gp.functions.", parameters.getString("GP.FUNCTIONS"));
        Node[] terminals = new Node[]{new Constant(-1.0), new Random()};

        //TODO ZLEPSIT A AT SE NEOPAKUJE KOD V DALSICH SOLVERECH A ZPREHLEDNIT!!!!
        int threads = ParallelPopulationEvaluator.getNumberOfThreads();
        EvaluableSubstrateBuilder[] perThreadSubstrateBuilders = new EvaluableSubstrateBuilder[threads];
        Evaluable[] perThreadEvaluators = new Evaluable[threads];
        Problem[] perThreadProblems = new Problem[threads];
        for (int i = 0; i < threads; i++) {
            EvaluableSubstrateBuilder substrateBuilder = SubstrateBuilderFactory.createEvaluableSubstrateBuilder(substrate, parameters);
            Problem problem = new Recognition1D(parameters);
//            Problem problem = new FindCluster(parameters);
            GPEvaluator evaluator = new GPEvaluator(substrateBuilder, problem);
            perThreadProblems[i] = problem;
            perThreadSubstrateBuilders[i] = substrateBuilder;
            perThreadEvaluators[i] = evaluator;
        }
        GP.TARGET_FITNESS = perThreadProblems[0].getTargetFitness();

        gp = GPFactory.createByName(parameters.getString("GP.TYPE"), perThreadEvaluators, functions, terminals);

        solver = new EvolutionaryAlgorithmSolver(gp, stats);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, substrate, perThreadProblems[0], parameters));
        solver.addProgressPrinter(new FileProgressPrinter(gp, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(gp));
        solver.addStopCondition(new MaxEvaluationsStopCondition(gp));
        solver.addStopCondition(new TargetFitnessStopCondition(gp));
        solver.addStopCondition(new SolvedStopCondition(perThreadProblems[0]));
    }

    public void solve() {

        solver.run();
    }

    public String getConfigString() {
        return gp.getConfigString();
    }
}