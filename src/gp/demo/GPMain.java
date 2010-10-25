package gp.demo;

import common.RND;
import common.evolution.*;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.pmatrix.Utils;
import common.stats.Stats;
import gep.GEP;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.RNC;
import gp.terminals.Random;
import gpaac.GPAAC;
import gpaac.Terminals;
import hyper.experiments.DummyProblem;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;

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
public class GPMain {
    public static void main(String[] args) {
        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
//        RND.initialize(8725627961384450L); //4
//        String type = "GP";
//        String type = "GEP";
        String type = "GPAAC";

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

                INode[] functions = createFunctions(type, combination);
                INode[] terminals = createTerminals(type);
                //--------------------

                List<IGenotypeToPhenotype<Forest, Forest>> converter = new ArrayList<IGenotypeToPhenotype<Forest, Forest>>();
                converter.add(new IdentityConversion<Forest>());

                IEvaluable<Forest> evaluable = EvaluableFactory.createByName(combination.getString("PROBLEM"));
                List<IEvaluable<Forest>> evaluator = new ArrayList<IEvaluable<Forest>>();
                evaluator.add(evaluable);

                PopulationManager<Forest, Forest> populationManager = new PopulationManager<Forest, Forest>(
                        combination, converter, evaluator);
                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GEP.class, "GEP");
                Utils.setStaticParameters(combination, GPAAC.class, "GPAAC");

                GPBase gp = createAlgorithm(type, combination, populationManager, functions, terminals);

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
        if (type.equals("GP")) {
            return ParameterMatrixStorage.load(new File("cfg/gpdemo.properties"));
        } else if (type.equals("GEP")) {
            return ParameterMatrixStorage.load(new File("cfg/gepdemo.properties"));
        } else if (type.equals("GPAAC")) {
            return ParameterMatrixStorage.load(new File("cfg/gpaacdemo.properties"));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static INode[] createFunctions(String type, ParameterCombination combination) {
        if (type.equals("GP")) {
            return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
        } else if (type.equals("GEP")) {
            return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
        } else if (type.equals("GPAAC")) {
            return NodeFactory.createByNameList("gpaac.Functions$", combination.getString("GP.FUNCTIONS"));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static INode[] createTerminals(String type) {
        if (type.equals("GP")) {
            return new Node[]{new Constant(-1.0), new Random()};//GP
        } else if (type.equals("GEP")) {
            return new Node[]{new RNC()};//GEP
        } else if (type.equals("GPAAC")) {
//            return new INode[]{new Terminals.Constant(1.0), new Terminals.Random()};//GPACC
            return new INode[]{new Terminals.Constant(1.0)};//GPACC
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static GPBase createAlgorithm(String type, ParameterCombination combination, PopulationManager populationManager, INode[] functions, INode[] terminals) {
        if (type.equals("GP")) {
            return GPFactory.createByName(combination.getString("GP.TYPE"), populationManager, functions, terminals);
        } else if (type.equals("GEP")) {
            return new GEP(populationManager, functions, terminals);
        } else if (type.equals("GPAAC")) {
            return new GPAAC(populationManager, functions, terminals);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }
}
