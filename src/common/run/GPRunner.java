package common.run;

import common.evolution.*;
import common.pmatrix.ParameterCombination;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPRunner implements EvolutionaryAlgorithmRunner {
    private final ParameterCombination parameters;
    private final PopulationManager<Forest, Forest> populationManager;
    private final INode[] functions;
    private final INode[] terminals;
    private final GPBase ea;

    public GPRunner(ParameterCombination parameters) {
        this.parameters = parameters;
        populationManager = createPopulationManager(parameters);
        String type = parameters.getAsString("GP.TYPE");
        functions = createFunctions(type, parameters);
        terminals = createTerminals(type);
        ea = createAlgorithm(type, parameters, populationManager, functions, terminals);
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

        PopulationManager<Forest, Forest> populationManager = new PopulationManager<Forest, Forest>(
                combination, converter, evaluator);
        return populationManager;
    }

    private static GPBase createAlgorithm(String type, ParameterCombination combination, PopulationManager populationManager, INode[] functions, INode[] terminals) {
        if (type.equals("gp.GP")) {
            return GPFactory.createByName(combination, populationManager, functions, terminals, null);
        } else if (type.equals("gp.GPEFS")) {
            return GPFactory.createByName(combination, populationManager, functions, terminals, null);
        } else if (type.equals("gep.GEP")) {
            return new GEP(combination, populationManager, functions, terminals, null);
        } else if (type.equals("GPAAC")) {
            return new GPAAC(combination, populationManager, functions, terminals, null);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
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

    private static INode[] createFunctions(String type, ParameterCombination combination) {
        if (type.equals("gp.GP")) {
            return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
        } else if (type.equals("gp.GPEFS")) {
            return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
        } else if (type.equals("gep.GEP")) {
            return NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
        } else if (type.equals("gpaac.GPAAC")) {
            return NodeFactory.createByNameList("gpaac.AACFunctions$", combination.getString("GP.FUNCTIONS"));
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }

    private static INode[] createTerminals(String type) {
        if (type.equals("gp.GP")) {
            return new Node[]{new Constant(-1.0), new Random()};//GP
        } else if (type.equals("gp.GPEFS")) {
            return new Node[]{new Constant(-1.0), new Random()};//GPEFS
        } else if (type.equals("gep.GEP")) {
            return new Node[]{new RNC()};//GEP
        } else if (type.equals("gpaac.GPAAC")) {
//            return new INode[]{new AACTerminals.Constant(1.0), new AACTerminals.Random()};//GPACC
            return new INode[]{new AACTerminals.Constant(1.0)};//GPACC
        } else {
            throw new IllegalArgumentException("Unsupported algorithm type");
        }
    }
}
