package gpat.demo;

import common.RND;
import common.SoundHelper;
import common.XMPPHelper;
import common.evolution.*;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gpat.*;
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
        long seed = RND.initializeTime();
//        long seed = 1321410388649375013L;
        System.out.println("INITIALIZED SEED: " + seed);
        RND.initialize(seed);
        String type = "GPAT";

        if (args.length == 0) {
            throw new IllegalArgumentException("Missing parameters!");
        }

        ParameterMatrixManager manager = createManager(type, args[0]);

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
                GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
                GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
                GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
                GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");

                PopulationManager<ATForest, ATForest> populationManager = createPopulationManager(combination);

                ATNodeImpl[] functions = createFunctions(type, combination);
                ATNodeImpl[] terminals = createTerminals(type);
                //--------------------

                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GPAT.class, "GPAT");
                Utils.setStaticParameters(combination, GPATSimple.class, "GPATS");


                IGPAT gp = createAlgorithm(combination, populationManager, functions, terminals);

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
        XMPPHelper.sendViaXMPP("NE run (GPATMain) finished " + experimentDirectory + ".");
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

    private static void extractStats(Stats stats, IGPAT gp) {
        List<ATForest> lastGeneration = gp.getLastGenerationPopulation();
        double arityLG = 0.0;
        double constantsLG = 0.0;
        double depthLG = 0.0;
        double leavesLG = 0.0;
        double nodesLG = 0.0;
        for (ATForest forest : lastGeneration) {
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
        System.out.println("Loading: " + configFile);
        if (type.equals("GPAT")) {
            return ParameterMatrixStorage.load(new File(configFile));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static ATNodeImpl[] createFunctions(String type, ParameterCombination combination) {
        if (type.equals("GPAT")) {
            return ATNodeFactory.createByNameList(combination.getString("GPAT.FUNCTION_IMPL"), combination.getString("GPAT.FUNCTIONS"));
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

    private static PopulationManager<ATForest, ATForest> createPopulationManager(ParameterCombination combination) {
        boolean parallel = combination.getBoolean("PARALLEL");
        int threads = 1;
        if (parallel) {
            if (combination.contains("PARALLEL.FORCE_THREADS")) {
                threads = combination.getInteger("PARALLEL.FORCE_THREADS");
            } else {
                threads = PopulationManager.getNumberOfThreads();
            }
        }

        List<IGenotypeToPhenotype<ATForest, ATForest>> converter = new ArrayList<IGenotypeToPhenotype<ATForest, ATForest>>(threads);
        List<IEvaluable<ATForest>> evaluator = new ArrayList<IEvaluable<ATForest>>(threads);

        for (int i = (threads - 1); i >= 0; i--) {
            converter.add(new IdentityConversion<ATForest>());
            IEvaluable<ATForest> evaluable = EvaluableFactory.createByName(combination);
            evaluator.add(evaluable);
        }

        PopulationManager<ATForest, ATForest> populationManager = new PopulationManager<ATForest, ATForest>(
                combination, converter, evaluator);
        return populationManager;
    }

    private static IGPAT createAlgorithm(ParameterCombination combination, PopulationManager populationManager, ATNodeImpl[] functions, ATNodeImpl[] terminals) {
        String solver = combination.getString("SOLVER").toUpperCase();
        if (solver.equals("GPAT")) {
            return new GPAT(populationManager, functions, terminals);
        } else if (solver.equals("GPATS")) {
            return new GPATSimple(populationManager, functions, terminals);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }
}
