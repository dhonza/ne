package common.run;

import common.evolution.*;
import common.pmatrix.ParameterCombination;
import common.pmatrix.Utils;
import common.stats.Stats;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.Random;
import hyper.evaluate.printer.FileProgressPrinter;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.DummyProblem;
import mogp.MOGP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MOGPRunner implements EvolutionaryAlgorithmRunner {
    private final ParameterCombination parameters;
    private final PopulationManager<Forest, Forest> populationManager;
    private final INode[] functions;
    private final INode[] terminals;
    private final MOGP ea;

    public MOGPRunner(ParameterCombination parameters) {
        this.parameters = parameters;
        populationManager = createPopulationManager(parameters);
        functions = createFunctions(parameters);
        terminals = createTerminals();
        ea = createAlgorithm(parameters, populationManager, functions, terminals);
        Utils.setStaticParameters(parameters, MOGP.class, "GP");
    }

    public void run(Stats stats, ReportStorage reportStorage) {
        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(ea, stats, false);
        solver.addProgressPrinter(new GPBasicProgressPrinter(ea));
        solver.addProgressPrinter(new FileProgressPrinter(ea, new DummyProblem(), reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(ea, GP.MAX_GENERATIONS));
        solver.addStopCondition(new MaxEvaluationsStopCondition(ea, GP.MAX_EVALUATIONS));
        solver.addStopCondition(new TargetFitnessStopCondition(ea, GP.TARGET_FITNESS));
        solver.addStopCondition(new SolvedStopCondition<Forest, Forest>(populationManager));
        solver.run();

        extractStats(stats, ea);
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

        PopulationManager<Forest, Forest> populationManager = new PopulationManager<>(
                combination, converter, evaluator);
        return populationManager;
    }

    private static MOGP createAlgorithm(ParameterCombination combination, PopulationManager populationManager, INode[] functions, INode[] terminals) {
        MOGP gp = null;
        try {
            Constructor constructor = Class.forName("mogp.MOGP").getConstructor(ParameterCombination.class, PopulationManager.class, INode[].class, INode[].class, String.class);
            gp = (MOGP) constructor.newInstance(combination, populationManager, functions, terminals, null);
        } catch (NoSuchMethodException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        }
        return gp;
    }

    private static void extractStats(Stats stats, MOGP gp) {
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

    private static INode[] createFunctions(ParameterCombination combination) {
        return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));

    }

    private static INode[] createTerminals() {
        return new Node[]{new Constant(-1.0), new Random()};//GP
    }
}
