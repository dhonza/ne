package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.SolvedStopCondition;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.GP;
import gp.MaxEvaluationsStopCondition;
import gp.MaxGenerationsStopCondition;
import gp.TargetFitnessStopCondition;
import gpat.ATNodeFactory;
import gpat.ATNodeImpl;
import gpat.ATTerminals;
import gpat.GPAT;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.GPATProgressPrinter1D;
import hyper.evaluate.printer.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class GPATSolver extends AbstractSolver {

    private GPAT gp;

    protected GPATSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }


    private void init() {
        Utils.setStaticParameters(parameters, GP.class, "GP");
        Utils.setStaticParameters(parameters, GPAT.class, "GPAT");

        ATNodeImpl[] functions = ATNodeFactory.createByNameList("gpat.ATFunctions$", parameters.getString("GPAT.FUNCTIONS"));
        ATNodeImpl[] terminals = new ATNodeImpl[]{new ATTerminals.Constant(1.0)};

        GP.TARGET_FITNESS = problem.getTargetFitness();

        gp = new GPAT(populationManager, functions, terminals);

        solver = new EvolutionaryAlgorithmSolver(gp, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new GPATProgressPrinter1D(gp, problem, reportStorage, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(gp, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(gp, GP.MAX_GENERATIONS));
        solver.addStopCondition(new MaxEvaluationsStopCondition(gp, GP.MAX_EVALUATIONS));
        solver.addStopCondition(new TargetFitnessStopCondition(gp, GP.TARGET_FITNESS));
        solver.addStopCondition(new SolvedStopCondition(populationManager));
    }

    public void solve() {

        solver.run();
    }

    public String getConfigString() {
        return gp.getConfigString();
    }
}