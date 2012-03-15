package gp.demo;

import common.RND;
import common.SoundHelper;
import common.XMPPHelper;
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
import gpaac.AACTerminals;
import gpaac.GPAAC;
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
public class GPMain {
    public static void main(String[] args) {
        long seed = RND.initializeTime();
//        long seed = 1307157509603693014L;
        System.out.println("INITIALIZED SEED: " + seed);
//        RND.initialize(seed);
        String type = "GP";
//        String type = "GEP";
//        String type = "GPAAC";
//        String type = "GPAT";

        String cfgFile = args.length == 0 ? null : args[0];
        ParameterMatrixManager manager = createManager(type, cfgFile);

        ReportStorage reportStorage;

        if (args.length > 1) {
            reportStorage = new ReportStorage(args[1]);

        } else {
            reportStorage = new ReportStorage();
        }

        reportStorage.startAll(seed, manager);
        reportStorage.openExperimentsOveralResults();
        for (ParameterCombination combination : manager) {
            int experiments = combination.getInteger("EXPERIMENTS");

            Stats stats = prepareStats();

            StringBuilder parameterString = new StringBuilder();
            parameterString.append("FIXED:\n").append("-----\n").append(manager.toStringNewLines());
            parameterString.append("\nCHANGING:\n").append("--------\n").append(combination.toStringOnlyChanngingNewLines());
            reportStorage.storeParameters(parameterString.toString());

            for (int i = 1; i <= experiments; i++) {
                reportStorage.startSingleRun();

                System.out.println("PARAMETER SETTING: " + combination);
                GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
                GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
                GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
                GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");

                INode[] functions = createFunctions(type, combination);
                INode[] terminals = createTerminals(type);
                //--------------------

                PopulationManager<Forest, Forest> populationManager = createPopulationManager(combination);

                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GEP.class, "GEP");
                Utils.setStaticParameters(combination, GPAAC.class, "GPAAC");
                Utils.setStaticParameters(combination, GPAAC.class, "GPAT");
                Utils.setStaticParameters(combination, GPAAC.class, "GPATS");

                GPBase gp = createAlgorithm(type, combination, populationManager, functions, terminals);

                EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(gp, stats, false);
                solver.addProgressPrinter(new GPBasicProgressPrinter(gp));
                solver.addProgressPrinter(new FileProgressPrinter(gp, new DummyProblem(), reportStorage, combination));
                solver.addStopCondition(new MaxGenerationsStopCondition(gp, GP.MAX_GENERATIONS));
                solver.addStopCondition(new MaxEvaluationsStopCondition(gp, GP.MAX_EVALUATIONS));
                solver.addStopCondition(new TargetFitnessStopCondition(gp, GP.TARGET_FITNESS));
                solver.addStopCondition(new SolvedStopCondition(populationManager));
                solver.run();

                extractStats(stats, gp);

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
        XMPPHelper.sendViaXMPP("NE run (GPMain) finished " + experimentDirectory + ".");
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

    private static ParameterMatrixManager createManager(String type, String configFile) {
        if (configFile != null) {
            System.out.println("Loading: " + configFile);
            return ParameterMatrixStorage.load(new File(configFile));
        }
        if (type.equals("GP")) {
            return ParameterMatrixStorage.load(new File("cfg/demo/gpdemo.properties"));
        } else if (type.equals("GEP")) {
            return ParameterMatrixStorage.load(new File("cfg/demo/gepdemo.properties"));
        } else if (type.equals("GPAAC")) {
            return ParameterMatrixStorage.load(new File("cfg/demo/gpaacdemo.properties"));
        } else if (type.equals("GPAT")) {
            return ParameterMatrixStorage.load(new File("cfg/demo/gpatdemo.properties"));
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
            return NodeFactory.createByNameList("gpaac.AACFunctions$", combination.getString("GP.FUNCTIONS"));
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
//            return new INode[]{new AACTerminals.Constant(1.0), new AACTerminals.Random()};//GPACC
            return new INode[]{new AACTerminals.Constant(1.0)};//GPACC
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static PopulationManager<Forest, Forest> createPopulationManager(ParameterCombination combination) {
        boolean parallel = combination.getBoolean("PARALLEL");
        int threads = 1;
        if (parallel) {
            if (combination.contains("PARALLEL.FORCE_THREADS")) {
                threads = combination.getInteger("PARALLEL.FORCE_THREADS");
            } else {
                threads = PopulationManager.getNumberOfThreads();
            }
        }

        List<IGenotypeToPhenotype<Forest, Forest>> converter = new ArrayList<IGenotypeToPhenotype<Forest, Forest>>(threads);
        List<IEvaluable<Forest>> evaluator = new ArrayList<IEvaluable<Forest>>(threads);

        for (int i = (threads - 1); i >= 0; i--) {
            converter.add(new IdentityConversion<Forest>());
            IEvaluable<Forest> evaluable = EvaluableFactory.createByName(combination);
            evaluator.add(evaluable);
        }

        PopulationManager<Forest, Forest> populationManager = new PopulationManager<Forest, Forest>(
                combination, converter, evaluator);
        return populationManager;
    }

    private static GPBase createAlgorithm(String type, ParameterCombination combination, PopulationManager populationManager, INode[] functions, INode[] terminals) {
        if (type.equals("GP")) {
            return GPFactory.createByName(combination, populationManager, functions, terminals, null);
        } else if (type.equals("GEP")) {
            return new GEP(combination, populationManager, functions, terminals, null);
        } else if (type.equals("GPAAC")) {
            return new GPAAC(combination, populationManager, functions, terminals, null);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }
}
