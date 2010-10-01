package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import hyper.evaluate.printer.SADEProgressPrinter1D;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;
import opt.sade.MaxEvaluationsStopCondition;
import opt.sade.MaxGenerationsStopCondition;
import opt.sade.SADE;
import opt.sade.TargetFitnessStopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectSADESolver extends AbstractSolver {
    protected DirectSADESolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }

    private void init() {
        SADE sade = new SADE(populationManager);

        Utils.setParameters(parameters, sade, "DIRECT_SADE");
        sade.targetFitness = problem.getTargetFitness();
        sade.dimensions = populationManager.getPhenotypeDimension();

        solver = new EvolutionaryAlgorithmSolver(sade, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new SADEProgressPrinter1D(sade, problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(sade, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(sade));
        solver.addStopCondition(new MaxEvaluationsStopCondition(sade));
        solver.addStopCondition(new TargetFitnessStopCondition(sade));
        solver.addStopCondition(new SolvedStopCondition(populationManager));

    }

    public void solve() {
        solver.run();
    }

    public String getConfigString() {
        return "IMPLEMENT!: DirectSADESolver.getConfigString()";
    }
}
