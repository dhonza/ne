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
import gpaac.GPAAC;
import gpaac.Terminals;
import hyper.evaluate.SolvedStopCondition;

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

        //--------------------
//        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File("cfg/gpdemo.properties"));
//        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File("cfg/gepdemo.properties"));
        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File("cfg/gpaccdemo.properties"));
        //--------------------

        for (ParameterCombination combination : manager) {
            int experiments = combination.getInteger("EXPERIMENTS");
            Stats stats = new Stats();
            stats.createDoubleStat("BSF", "EXPERIMENT", "Best So Far Fitness");
            stats.createLongStat("BSFG", "EXPERIMENT", "Best So Far Fitness Generation");
            for (int i = 1; i <= experiments; i++) {
                System.out.println("PARAMETER SETTING: " + combination);
                GP.MAX_GENERATIONS = combination.getInteger("GP.MAX_GENERATIONS");
                GP.MAX_EVALUATIONS = combination.getInteger("GP.MAX_EVALUATIONS");
                GP.POPULATION_SIZE = combination.getInteger("GP.POPULATION_SIZE");
                GP.TARGET_FITNESS = combination.getDouble("GP.TARGET_FITNESS");

//                INode[] functions = NodeFactory.createByNameList("gp.functions.",
//                        combination.getString("GP.FUNCTIONS"));//GP, GEP

                INode[] functions = NodeFactory.createByNameList("gpaac.Functions$",
                        combination.getString("GP.FUNCTIONS"));//GPACC

                //--------------------
//                INode[] terminals = new Node[]{new Constant(-1.0), new Random()};//GP
//                INode[] terminals = new Node[]{new RNC()};//GEP
                INode[] terminals = new INode[]{new Terminals.Constant(1.0)};//GPACC
                //--------------------

                List<IGenotypeToPhenotype<Forest, Forest>> converter = new ArrayList<IGenotypeToPhenotype<Forest, Forest>>();
                converter.add(new IdentityConversion<Forest>());

                IEvaluable<Forest> evaluable = EvaluableFactory.createByName(combination.getString("PROBLEM"));
                List<IEvaluable<Forest>> evaluator = new ArrayList<IEvaluable<Forest>>();
                evaluator.add(evaluable);

                PopulationManager<Forest, Forest> populationManager = new PopulationManager<Forest, Forest>(
                        converter, evaluator);
                Utils.setStaticParameters(combination, GP.class, "GP");
                Utils.setStaticParameters(combination, GEP.class, "GEP");
                Utils.setStaticParameters(combination, GEP.class, "GPAAC");

                //--------------------
//                GPBase gp = GPFactory.createByName(combination.getString("GP.TYPE"), populationManager, functions, terminals);
//                GEP gp = new GEP(populationManager, functions, terminals);
                GPAAC gp = new GPAAC(populationManager, functions, terminals);
                //--------------------


                EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(gp, stats, false);
                solver.addProgressPrinter(new GPBasicProgressPrinter(gp));
                solver.addStopCondition(new MaxGenerationsStopCondition(gp, GP.MAX_GENERATIONS));
                solver.addStopCondition(new MaxEvaluationsStopCondition(gp, GP.MAX_EVALUATIONS));
                solver.addStopCondition(new TargetFitnessStopCondition(gp, GP.TARGET_FITNESS));
                solver.addStopCondition(new SolvedStopCondition(populationManager));
                solver.run();

                stats.addSample("BSF", gp.getBestSoFar().getFitness());
                stats.addSample("BSFG", gp.getLastInnovation());
            }
            System.out.println(stats.scopeToString("EXPERIMENT"));
        }
    }
}
