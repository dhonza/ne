package hyper.experiments;

import common.RND;
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
public class Main {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("No configuration directory given!");
            System.exit(1);
        }

        ReportStorage reportStorage = new ReportStorage();

        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(args[0], "experiment.properties"));
        System.out.println("PARAMETER SETTINGS: " + manager);

//        reportStorage.startAll(seed);
//        TODO sjednotit zapis seed!!! s GPATMain/GPMain
        reportStorage.openExperimentsOveralResults();
        for (ParameterCombination combination : manager) {
            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());

            System.out.println("PARAMETER COMBINATION: " + combination.toStringOnlyChannging());
            int experiments = combination.getInteger("EXPERIMENTS");
            boolean storeRun = combination.getBoolean("PRINT.storeRun");

            Stats stats = new Stats();
            stats.createStringStat("RND_SEED", "EXPERIMENT", "Random seed used to initialize generator");

            for (int i = 0; i < experiments; i++) {
                long seed = RND.initializeTime();
                stats.addSample("RND_SEED", Long.toString(seed));
                System.out.println("INITIALIZED SEED: " + seed);
//        RND.initialize(8686925819525946L); //4

//                System.out.println("TARGET FITNESS " + problem.getTargetFitness());
//                System.out.println("EXPERIMENT: " + (i + 1));

                Solver solver = SolverFactory.getSolver(combination, stats, reportStorage);
                if (i == 0) {
                    System.out.println(solver.getConfigString());
                    parameterString.append("\nSOLVER:\n").append("------\n").append(solver.getConfigString());
                    reportStorage.storeParameters(parameterString.toString());

                }
                solver.solve();

                if (storeRun) {
                    reportStorage.storeSingleRunResults();
                }
                reportStorage.incrementExperimentId();
            }
            reportStorage.storeExperimentResults(stats);
            reportStorage.appendExperimentsOverallResults(combination.toStringOnlyChannging(), stats);
            System.out.println(stats.scopeToString("EXPERIMENT"));
            reportStorage.prepareNewParameterCombination();
        }
        reportStorage.closeExperimentsOverallResults();
    }
}
