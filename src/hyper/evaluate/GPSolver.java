package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.SolvedStopCondition;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gep.GEP;
import gp.*;
import gpaac.GPAAC;
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

public class GPSolver extends AbstractSolver {

    private GPBase gp;

    protected GPSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }


    private void init() {
        Utils.setStaticParameters(parameters, GP.class, "GP");
        Utils.setStaticParameters(parameters, GEP.class, "GEP");
        Utils.setStaticParameters(parameters, GPAAC.class, "GPAAC");

        String functionPackage = "gp.functions.";
        if(parameters.getString("GP.TYPE").equals("gpaac.GPAAC")) {
            functionPackage = "gpaac.AACFunctions$";
        }

        INode[] functions = NodeFactory.createByNameList(functionPackage, parameters.getString("GP.FUNCTIONS"));
        INode[] terminals = GPFactory.createTerminalsByName(parameters.getString("GP.TYPE"));

        GP.TARGET_FITNESS = problem.getTargetFitness();

        gp = GPFactory.createByName(parameters.getString("GP.TYPE"), populationManager, functions, terminals);

        solver = new EvolutionaryAlgorithmSolver(gp, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, problem, parameters));
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