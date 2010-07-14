package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.printer.CMAESProgressPrinter1D;
import hyper.experiments.reco.ReportStorage;
import opt.cmaes.CMAES;
import opt.cmaes.MaxEvaluationsStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectCMAESSolver implements Solver {
    final protected ParameterCombination parameters;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    public DirectCMAESSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.stats = stats;
        this.reportStorage = reportStorage;
    }

    public void solve() {
        IProblem problem = ProblemFactory.getProblem(parameters, reportStorage);
        EvaluableSubstrateBuilder substrateBuilder =
                SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

        DirectCMAESObjectiveFunction function = new DirectCMAESObjectiveFunction(substrateBuilder, problem);
        CMAES cmaes = new CMAES(function);
        Utils.setParameters(parameters, cmaes.getOptions(), "DIRECT_CMAES");

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(cmaes, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new CMAESProgressPrinter1D(cmaes, problem, parameters));
//        solver.addProgressPrinter(new FileProgressPrinter(cmaes, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxEvaluationsStopCondition(cmaes));
//        solver.addStopCondition(new TargetFitnessStopCondition(cmaes));
        solver.addStopCondition(new SolvedStopCondition(problem));

        solver.run();
    }

    public String getConfigString() {
        return "IMPLEMENT!: DirectCMAESSolver.getConfigString()";
    }
}