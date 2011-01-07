package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.SolvedStopCondition;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.evaluate.printer.CMAESProgressPrinter1D;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.ReportStorage;
import opt.cmaes.CMAES;
import opt.cmaes.MaxEvaluationsStopCondition;
import opt.cmaes.MaxGenerationsStopCondition;
import opt.cmaes.TargetFitnessStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectCMAESSolver extends AbstractSolver {
    public DirectCMAESSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }

    private void init() {
        CMAES cmaes = new CMAES(populationManager, populationManager.getPhenotypeDimension());

        Utils.setParameters(parameters, cmaes.getOptions(), "DIRECT_CMAES");

        solver = new EvolutionaryAlgorithmSolver(cmaes, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new CMAESProgressPrinter1D(cmaes, problem, reportStorage, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(cmaes, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(cmaes));
        solver.addStopCondition(new MaxEvaluationsStopCondition(cmaes));
        solver.addStopCondition(new TargetFitnessStopCondition(cmaes));
        solver.addStopCondition(new SolvedStopCondition(populationManager));
    }

    public void solve() {
        solver.run();
    }

    public String getConfigString() {
        return "IMPLEMENT!: DirectCMAESSolver.getConfigString()";
    }
}