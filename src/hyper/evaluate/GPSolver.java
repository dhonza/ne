package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.SolvedStopCondition;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gep.GEP;
import gp.*;
import gpaac.GPAAC;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.GPProgressPrinter1D;
import hyper.evaluate.printer.ReportStorage;

import java.util.List;

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
        Utils.setStaticParameters(parameters, GPEFS.class, "GPEFS");

        String functionPackage = "gp.functions.";
        if (parameters.getString("GP.TYPE").equals("gpaac.GPAAC")) {
            functionPackage = "gpaac.AACFunctions$";
        }

        INode[] functions = NodeFactory.createByNameList(functionPackage, parameters.getString("GP.FUNCTIONS"));
        INode[] terminals = GPFactory.createTerminalsByName(parameters.getString("GP.TYPE"));

        GP.TARGET_FITNESS = problem.getTargetFitness();

        String initialGenome = null;
        if (parameters.contains("INITIAL_GENOME")) {
            initialGenome = parameters.getString("INITIAL_GENOME");
        }

        gp = GPFactory.createByName(parameters.getString("GP.TYPE"), populationManager, functions, terminals, initialGenome);

        solver = new EvolutionaryAlgorithmSolver(gp, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new GPProgressPrinter1D(gp, problem, reportStorage, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(gp, problem, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(gp, GP.MAX_GENERATIONS));
        solver.addStopCondition(new MaxEvaluationsStopCondition(gp, GP.MAX_EVALUATIONS));
        solver.addStopCondition(new TargetFitnessStopCondition(gp, GP.TARGET_FITNESS));
        solver.addStopCondition(new SolvedStopCondition(populationManager));
    }

    public void solve() {
        solver.run();
        extractStats(stats, gp);
    }

    public String getConfigString() {
        return gp.getConfigString();
    }

    private static void extractStats(Stats stats, GPBase gp) {
        List<Forest> lastGeneration = gp.getLastGenerationPopulation();
        double arityLG = 0.0;
        double constantsLG = 0.0;
        double depthLG = 0.0;
        double leavesLG = 0.0;
        double nodesLG = 0.0;
        for (Forest forest : lastGeneration) {
            arityLG += forest.getAverageArity();
            constantsLG += forest.getNumOfConstants();
            depthLG += forest.getMaxTreeDepth();
            leavesLG += forest.getNumOfLeaves();
            nodesLG += forest.getNumOfNodes();
        }
        arityLG /= lastGeneration.size();
        constantsLG /= lastGeneration.size();
        depthLG /= lastGeneration.size();
        leavesLG /= lastGeneration.size();
        nodesLG /= lastGeneration.size();

        stats.addSample("BSF", gp.getBestSoFar().getFitness());
        stats.addSample("BSFG", gp.getGenerationOfBSF());
        stats.addSample("ARITY_BSF", gp.getBestSoFar().getAverageArity());
        stats.addSample("ARITY_LG", arityLG);
        stats.addSample("CONSTANTS_BSF", (double) gp.getBestSoFar().getNumOfConstants());
        stats.addSample("CONSTANTS_LG", constantsLG);
        stats.addSample("DEPTH_BSF", (double) gp.getBestSoFar().getMaxTreeDepth());
        stats.addSample("DEPTH_LG", depthLG);
        stats.addSample("LEAVES_BSF", (double) gp.getBestSoFar().getNumOfLeaves());
        stats.addSample("LEAVES_LG", leavesLG);
        stats.addSample("NODES_BSF", (double) gp.getBestSoFar().getNumOfNodes());
        stats.addSample("NODES_LG", nodesLG);
    }
}