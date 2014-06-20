package common.run;

import common.RND;
import common.SoundHelper;
import common.XMPPHelper;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.pmatrix.Utils;
import common.stats.Stats;
import gep.GEP;
import gp.GP;
import gp.GPEFS;
import gpaac.GPAAC;
import gpat.GPAT;
import gpat.GPATSimple;
import hyper.evaluate.printer.ReportStorage;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Runner {
    public static void main(String[] args) {
        long seed = RND.initializeTime();
//        long seed = 12341957678627684L;
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

        reportStorage.startAll(seed, manager);
        reportStorage.openExperimentsOverallResults();
        for (ParameterCombination combination : manager) {
            int experiments = combination.getInteger("EXPERIMENTS");

            Stats stats = prepareStats();

            reportStorage.storeParameters(combination.toStringAllSeparatedNewLines());

            for (int i = 1; i <= experiments; i++) {
                reportStorage.startSingleRun();

                System.out.println("PARAMETER SETTING: " + combination);

                EvolutionaryAlgorithmRunner runnerEA;

                String solver = combination.getString("SOLVER");
                if (solver.equals("GP")) {
                    initializeGP(combination);
                    runnerEA = new GPRunner(combination);
                } else if (solver.equals("MOGP")) {
                    initializeMOGP(combination);
                    runnerEA = new MOGPRunner(combination);
                } else if (solver.equals("GPAT")) {
                    initializeGP(combination);
                    runnerEA = new GPATRunner(combination);
                } else if (solver.equals("NEAT")) {
                    initializeNEAT(combination);
                    runnerEA = new NEATRunner(combination);
                } else {
                    throw new IllegalStateException("Unknown SOLVER: " + solver + ".");
                }

                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GEP.class, "GEP");
                Utils.setStaticParameters(combination, GPAAC.class, "GPAAC");
                Utils.setStaticParameters(combination, GPEFS.class, "GPEFS");
                Utils.setStaticParameters(combination, GPAT.class, "GPAT");
                Utils.setStaticParameters(combination, GPATSimple.class, "GPATS");

                runnerEA.run(stats, reportStorage);

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
        XMPPHelper.sendViaXMPP("NE run (Runner) finished " + experimentDirectory + ".");
    }

    private static void initializeGP(ParameterCombination combination) {
        GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
        GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
        GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
        GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");
    }

    private static void initializeMOGP(ParameterCombination combination) {
        GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
        GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
        GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
        GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");
    }

    private static void initializeNEAT(ParameterCombination combination) {
//        NEAT.MAX_GENERATIONS = combination.getInteger("NEAT.MAX_GENERATIONS");
//        NEAT.MAX_EVALUATIONS = combination.getInteger("NEAT.MAX_EVALUATIONS");
//        NEAT.POPULATION_SIZE = combination.getInteger("NEAT.POPULATION_SIZE");
//        NEAT.TARGET_FITNESS = combination.getDouble("NEAT.TARGET_FITNESS");
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
