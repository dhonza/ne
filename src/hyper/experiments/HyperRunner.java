package hyper.experiments;

import common.RND;
import common.SoundHelper;
import common.XMPPHelper;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.stats.Stats;
import hyper.evaluate.Solver;
import hyper.evaluate.SolverFactory;
import hyper.evaluate.printer.ReportStorage;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class HyperRunner {

    public static void main(String[] args) {
        long seed = RND.initializeTime();
//        long seed = 1321410388649375013L;
        System.out.println("INITIALIZED SEED: " + seed);
        RND.initialize(seed);

        if (args.length == 0) {
            throw new IllegalArgumentException("Missing parameters!");
        }

        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(args[0]));

        ReportStorage reportStorage;

        if (args.length > 1) {
            reportStorage = new ReportStorage(args[1]);

        } else {
            reportStorage = new ReportStorage();
        }

        reportStorage.startAll(seed);
        reportStorage.openExperimentsOveralResults();
        for (ParameterCombination combination : manager) {
            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());
            reportStorage.storeParameters(parameterString.toString());

            int experiments = combination.getInteger("EXPERIMENTS");
            boolean storeRun = combination.getBoolean("PRINT.storeRun");

            Stats stats = prepareStats();

            for (int i = 1; i <= experiments; i++) {
                reportStorage.startSingleRun();
                Solver solver = SolverFactory.getSolver(combination, stats, reportStorage);
                if (i == 1) {
                    System.out.println("SOLVER PARAMS:");
                    System.out.println(solver.getConfigString());
//                    parameterString.append("\nSOLVER:\n").append("------\n").append(solver.getConfigString());
//                    reportStorage.storeParameters(parameterString.toString());
                }
                solver.solve();

                reportStorage.storeSingleRunResults();
                reportStorage.incrementExperimentId();
            }
            reportStorage.storeExperimentResults(stats);
            reportStorage.appendExperimentsOverallResults(combination.toStringOnlyChannging(), stats);
            System.out.println(stats.scopeToString("EXPERIMENT"));
            reportStorage.prepareNewParameterCombination();
        }
        reportStorage.closeExperimentsOverallResults();
        SoundHelper.playSoundFile("/System/Library/Sounds/Glass.aiff");
        String experimentDirectory = args.length > 1 ? "(" + args[1] + ")" : "";
        XMPPHelper.sendViaXMPP("NE run (HyperRunner) finished " + experimentDirectory + ".");
    }

    private static Stats prepareStats() {
        Stats stats = new Stats();
        stats.createDoubleStat("BSF", "EXPERIMENT", "Best So Far Fitness");
        stats.createLongStat("BSFG", "EXPERIMENT", "Best So Far Fitness Generation");
        stats.createDoubleStat("ARITY_BSF", "EXPERIMENT", "Best So Far Average Node Arity");
        stats.createDoubleStat("ARITY_LG", "EXPERIMENT", "Last Generation Average of Average Node Arity");
        stats.createDoubleStat("CONSTANTS_BSF", "EXPERIMENT", "Best So Far Number of Node Constants");
        stats.createDoubleStat("CONSTANTS_LG", "EXPERIMENT", "Last Generation Average Number of Node Constants");
        stats.createDoubleStat("DEPTH_BSF", "EXPERIMENT", "Best So Far Max Tree Depth");
        stats.createDoubleStat("DEPTH_LG", "EXPERIMENT", "Last Generation Average Max Tree Depth");
        stats.createDoubleStat("LEAVES_BSF", "EXPERIMENT", "Best So Far Number of Leaves");
        stats.createDoubleStat("LEAVES_LG", "EXPERIMENT", "Last Generation Average Number of Leaves");
        stats.createDoubleStat("NODES_BSF", "EXPERIMENT", "Best So Far Number of Nodes");
        stats.createDoubleStat("NODES_LG", "EXPERIMENT", "Last Generation Average Number of Nodes");
        return stats;
    }
}
