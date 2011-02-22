package gpat.demo;

import common.RND;
import common.evolution.*;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gpat.ATNodeFactory;
import gpat.ATNodeImpl;
import gpat.ATTerminals;
import gpat.GPAT;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.DummyProblem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:04:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class GPATMain {
    public static void main(String[] args) {
        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
//        RND.initialize(1307066165129825013L); //4
        String type = "GPAT";

        ParameterMatrixManager manager = createManager(type);

        ReportStorage reportStorage = new ReportStorage();

        for (ParameterCombination combination : manager) {
            int experiments = combination.getInteger("EXPERIMENTS");
            Stats stats = new Stats();
            stats.createDoubleStat("BSF", "EXPERIMENT", "Best So Far Fitness");
            stats.createLongStat("BSFG", "EXPERIMENT", "Best So Far Fitness Generation");

            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());
            reportStorage.storeParameters(parameterString.toString());

            for (int i = 1; i <= experiments; i++) {
                System.out.println("PARAMETER SETTING: " + combination);
                GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
                GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
                GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
                GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");

                ATNodeImpl[] functions = createFunctions(type, combination);
                ATNodeImpl[] terminals = createTerminals(type);
                //--------------------

                List<IGenotypeToPhenotype<Forest, Forest>> converter = new ArrayList<IGenotypeToPhenotype<Forest, Forest>>();
                converter.add(new IdentityConversion<Forest>());

                IEvaluable<Forest> evaluable = EvaluableFactory.createByName(combination.getString("PROBLEM"));
                List<IEvaluable<Forest>> evaluator = new ArrayList<IEvaluable<Forest>>();
                evaluator.add(evaluable);

                PopulationManager<Forest, Forest> populationManager = new PopulationManager<Forest, Forest>(
                        combination, converter, evaluator);
                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GPAT.class, "GPAT");

                GPAT gp = createAlgorithm(type, combination, populationManager, functions, terminals);

                EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(gp, stats, false);
                solver.addProgressPrinter(new GPBasicProgressPrinter(gp));
                solver.addProgressPrinter(new FileProgressPrinter(gp, new DummyProblem(), reportStorage, combination));
                solver.addStopCondition(new MaxGenerationsStopCondition(gp, GP.MAX_GENERATIONS));
                solver.addStopCondition(new MaxEvaluationsStopCondition(gp, GP.MAX_EVALUATIONS));
                solver.addStopCondition(new TargetFitnessStopCondition(gp, GP.TARGET_FITNESS));
                solver.addStopCondition(new SolvedStopCondition(populationManager));
                solver.run();

                stats.addSample("BSF", gp.getBestSoFar().getFitness());
                stats.addSample("BSFG", gp.getLastInnovation());

                reportStorage.storeSingleRunResults();
                reportStorage.incrementExperimentId();
            }
            reportStorage.storeExperimentResults(stats);
            reportStorage.appendExperimentsOverallResults(combination.toStringOnlyChannging(), stats);
            System.out.println(stats.scopeToString("EXPERIMENT"));
            reportStorage.incrementParameterCombinationId();
        }
        reportStorage.storeExperimentsOverallResults();
    }

    private static ParameterMatrixManager createManager(String type) {
        if (type.equals("GPAT")) {
            return ParameterMatrixStorage.load(new File("cfg/demo/gpatdemo.properties"));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static ATNodeImpl[] createFunctions(String type, ParameterCombination combination) {
        if (type.equals("GPAT")) {
            return ATNodeFactory.createByNameList("gpat.ATFunctions$", combination.getString("GPAT.FUNCTIONS"));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static ATNodeImpl[] createTerminals(String type) {
        if (type.equals("GPAT")) {
//            return new ATNode2[]{};//GPAT
            return new ATNodeImpl[]{new ATTerminals.Constant(1.0)};//GPAT
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static GPAT createAlgorithm(String type, ParameterCombination combination, PopulationManager populationManager, ATNodeImpl[] functions, ATNodeImpl[] terminals) {
        if (type.equals("GPAT")) {
            return new GPAT(populationManager, functions, terminals);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }
}
