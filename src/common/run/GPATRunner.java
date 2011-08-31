package common.run;

import common.evolution.*;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import gp.*;
import gpat.*;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.DummyProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPATRunner implements EvolutionaryAlgorithmRunner {
    private final ParameterCombination parameters;
    private final PopulationManager<ATForest, ATForest> populationManager;
    private final ATNodeImpl[] functions;
    private final ATNodeImpl[] terminals;
    private final IGPAT ea;

    public GPATRunner(ParameterCombination parameters) {
        this.parameters = parameters;
        populationManager = createPopulationManager(parameters);
        functions = createFunctions("GPAT", parameters);
        terminals = createTerminals("GPAT");
        ea = createAlgorithm(parameters, populationManager, functions, terminals);
    }

    public void run(Stats stats, ReportStorage reportStorage) {
        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(ea, stats, false);
        solver.addProgressPrinter(new GPBasicProgressPrinter(ea));
        solver.addProgressPrinter(new FileProgressPrinter(ea, new DummyProblem(), reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(ea, GP.MAX_GENERATIONS));
        solver.addStopCondition(new MaxEvaluationsStopCondition(ea, GP.MAX_EVALUATIONS));
        solver.addStopCondition(new TargetFitnessStopCondition(ea, GP.TARGET_FITNESS));
        solver.addStopCondition(new SolvedStopCondition(populationManager));
        solver.run();

        extractStats(stats, ea);
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

    private static IGPAT createAlgorithm(ParameterCombination combination, PopulationManager populationManager, ATNodeImpl[] functions, ATNodeImpl[] terminals) {
        String type = combination.getString("GP.TYPE");
        if (type.equals("gpat.GPAT")) {
            return new GPAT(populationManager, functions, terminals);
        } else if (type.equals("gpat.GPATSimple")) {
            return new GPATSimple(populationManager, functions, terminals);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type: " + type + ".");
        }
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
}
