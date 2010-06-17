package hyper.experiments;

import common.RND;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.NetSubstrateBuilder;
import hyper.evaluate.JPPFSolver;
import hyper.evaluate.Problem;
import hyper.evaluate.Solver;
import hyper.evaluate.SolverFactory;
import hyper.experiments.reco.ReportStorage;
import hyper.experiments.reco.problem.RecoSubstrateFactory;
import hyper.experiments.reco.problem.Recognition1D;
import hyper.substrate.BasicSubstrate;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.server.protocol.JPPFTask;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecoMainJPPF {
    private static JPPFClient jppfClient = null;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("No configuration directory given!");
            System.exit(1);
        }

        ReportStorage reportStorage = new ReportStorage();

        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File(args[0], "experiment.properties"));
        System.out.println("PARAMETER SETTINGS: " + manager);

        jppfClient = new JPPFClient();
        JPPFJob job = new JPPFJob();
        job.setId("Pokus");
        job.setBlocking(true);

        int combinationId = 1;

        for (ParameterCombination combination : manager) {
            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());

            System.out.println("PARAMETER COMBINATION: " + combination.toStringOnlyChannging());
            int lineSize = combination.getInteger("RECO.LINE_SIZE");

            Stats stats = new Stats();
            stats.createStringStat("RND_SEED", "EXPERIMENT", "Random seed used to initialize generator");

            int experiments = combination.getInteger("EXPERIMENTS");
            for (int i = 0; i < experiments; i++) {
                long seed = RND.initializeTime();
                stats.addSample("RND_SEED", Long.toString(seed));
                System.out.println("INITIALIZED SEED: " + seed);
//        RND.initialize(8686925819525946L); //4

//            BasicSubstrate substrate = RecoSubstrateFactory.createInputToOutput(lineSize);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, lineSize);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 3, 1);

                //XOR
                BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(2, 2, 1);

                //AND
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputToOutput(lineSize, 1);
//            BasicSubstrate substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, 1);

                EvaluableSubstrateBuilder substrateBuilder = new NetSubstrateBuilder(substrate);

                Problem problem = new Recognition1D(combination);
                System.out.println("TARGET FITNESS " + problem.getTargetFitness());
//                System.out.println("EXPERIMENT: " + (i + 1));

                Solver solver = SolverFactory.getSolver(combination, substrateBuilder, stats, problem, reportStorage);
                if (i == 0) {
                    System.out.println(solver.getConfigString());
                    parameterString.append("\nSOLVER:\n").append("------\n").append(solver.getConfigString());
                    reportStorage.storeParameters(combinationId, parameterString.toString());

                }

                try {
                    job.addTask(new JPPFSolver(solver, combinationId, i, combination, reportStorage, stats));
                } catch (JPPFException e) {
                    e.printStackTrace();
                }
            }
            combinationId++;
        }

        try {
            List<JPPFTask> results = jppfClient.submit(job);
            int counter = 0;
            for (ParameterCombination combination : manager) {
                int experiments = combination.getInteger("EXPERIMENTS");
                for (int i = 0; i < experiments; i++) {
                    JPPFSolver task = (JPPFSolver) results.get(counter++);
                    if (task.getException() != null) {
                        task.getException().printStackTrace();
                    } else {
                        System.out.println("OK: " + task.getResult());
//                        reportStorage.storeExperimentResults(task.getCombinationId(), task.getStats());
//                        reportStorage.appendExperimentsOverallResults(combinationId, combination.toStringOnlyChannging(), task.getStats());
                        System.out.println(task.getStats().scopeToString("EXPERIMENT"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (jppfClient != null) jppfClient.close();
        }
        reportStorage.storeExperimentsOverallResults();
    }
}