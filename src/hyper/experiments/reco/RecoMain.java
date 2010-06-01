package hyper.experiments.reco;

import common.RND;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.stats.Stats;
import hyper.builder.NEATSubstrateBuilder;
import hyper.evaluate.Problem;
import hyper.evaluate.Solver;
import hyper.evaluate.SolverFactory;
import hyper.experiments.reco.problem.RecoSubstrateFactory;
import hyper.experiments.reco.problem.Recognition1D;
import hyper.substrate.BasicSubstrate;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecoMain {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("No configuration directory given!");
            System.exit(1);
        }

        ReportStorage reportStorage = new ReportStorage();

        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(args[0], "experiment.properties"));

        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
//        RND.initialize(8686925819525946L); //4

        System.out.println("PARAMETER SETTINGS: " + manager);

        int combinationId = 1;
        for (ParameterCombination combination : manager) {
            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());

            System.out.println("PARAMETER COMBINATION: " + combination.toStringOnlyChannging());
            int experiments = combination.getInteger("EXPERIMENTS");
            int lineSize = combination.getInteger("RECO.LINE_SIZE");

            Stats stats = new Stats();
            stats.createStat("GENERATIONS", "EXPERIMENT", "Number of Generations");
            stats.createStat("EVALUATIONS", "EXPERIMENT", "Number of Evaluations");

            for (int i = 0; i < experiments; i++) {
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputToOutput(lineSize);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, lineSize);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 3, 1);

                //XOR
                BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(2, 2, 1);

                //AND
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputToOutput(lineSize, 1);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, 1);

                NEATSubstrateBuilder substrateBuilder = new NEATSubstrateBuilder(substrate);

                Problem problem = new Recognition1D(combination);
                System.out.println("TARGET FITNESS " + problem.getTargetFitness());
//                System.out.println("EXPERIMENT: " + (i + 1));

                Solver solver = SolverFactory.getSolver(combination, substrateBuilder, stats, problem);
                if (i == 0) {
                    System.out.println(solver.getConfigString());
                    parameterString.append("\nSOLVER:\n").append("------\n").append(solver.getConfigString());
                    reportStorage.storeParameters(combinationId, parameterString.toString());

                }
                solver.solve();
            }
            reportStorage.storeExperimentResults(combinationId, stats);
            reportStorage.appendExperimentsOverallResults(combinationId, combination.toStringOnlyChannging(), stats);
            System.out.println(stats.scopeToString("EXPERIMENT"));
            combinationId++;
        }
        reportStorage.storeExperimentsOverallResults();
    }
}
