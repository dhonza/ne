package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.printer.SADEProgressPrinter1D;
import hyper.experiments.reco.ReportStorage;
import opt.sade.MaxEvaluationsStopCondition;
import opt.sade.SADE;
import opt.sade.TargetFitnessStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectSADESolver implements Solver {
    final protected ParameterCombination parameters;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    public DirectSADESolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.stats = stats;
        this.reportStorage = reportStorage;
    }

    public void solve() {
        Problem problem = ProblemFactory.getProblem(parameters, reportStorage);
        EvaluableSubstrateBuilder substrateBuilder =
                SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

        DirectSADEObjectiveFunction function = new DirectSADEObjectiveFunction(substrateBuilder, problem);
        SADE sade = new SADE(function);
        Utils.setParameters(parameters, sade, "DIRECT_SADE");

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(sade, stats);
        solver.addProgressPrinter(new SADEProgressPrinter1D(sade, problem, parameters));
//        solver.addProgressPrinter(new FileProgressPrinter(sade, problem, reportStorage, parameters));
//        solver.addStopCondition(new MaxGenerationsStopCondition(sade));
        solver.addStopCondition(new MaxEvaluationsStopCondition(sade));
        solver.addStopCondition(new TargetFitnessStopCondition(sade));
        solver.addStopCondition(new SolvedStopCondition(problem));

        solver.run();
    }

    public String getConfigString() {
        return "IMPLEMENT!: DirectSADESolver.getConfigString()";
    }
}
