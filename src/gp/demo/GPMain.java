package gp.demo;

import common.RND;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.pmatrix.ParameterMatrixManager;
import common.pmatrix.ParameterMatrixStorage;
import common.stats.Stats;
import gp.*;
import gp.terminals.Constant;
import gp.terminals.Random;

import java.io.File;

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


        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File("cfg/gpdemo.properties"));
        for (ParameterCombination combination : manager) {
            int experiments = combination.getInteger("EXPERIMENTS");
            Stats stats = new Stats();
            stats.createDoubleStat("BSF", "EXPERIMENT", "Best So Far Fitness");
            stats.createDoubleStat("BSFG", "EXPERIMENT", "Best So Far Fitness Generation");
            for (int i = 1; i <= experiments; i++) {
                System.out.println("PARAMETER SETTING: " + combination);
                GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
                GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
                GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
                GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");

                Node[] functions = NodeFactory.createByNameList("gp.functions.", combination.getString("GP.FUNCTIONS"));
                Node[] terminals = new Node[]{new Constant(-1.0), new Random()};

                Evaluable evaluable = EvaluableFactory.createByName(combination.getString("PROBLEM"));
                GP gp = GPFactory.createByName(combination.getString("GP.TYPE"), evaluable, functions, terminals);

                EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(gp, stats);
                solver.addProgressPrinter(new GPBasicProgressPrinter(gp));
                solver.addStopCondition(new MaxGenerationsStopCondition(gp));
                solver.addStopCondition(new MaxEvaluationsStopCondition(gp));
                solver.addStopCondition(new TargetFitnessStopCondition(gp));
                solver.run();

                stats.addSample("BSF", gp.getBestSoFar().getFitness());
                stats.addSample("BSFG", gp.getLastInnovation());
            }
            System.out.println(stats.scopeToString("EXPERIMENT"));
        }
    }
}
