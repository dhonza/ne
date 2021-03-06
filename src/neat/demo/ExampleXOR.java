package neat.demo;

import common.RND;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.IEvaluable;
import common.evolution.IGenotypeToPhenotype;
import common.evolution.PopulationManager;
import common.net.linked.Net;
import common.stats.Stats;
import neat.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Title: NeuroEvolution
 * </p>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company:
 * </p>
 *
 * @author Jan Drchal
 * @version 0001
 */

public class ExampleXOR {
    FitnessSharingPopulation<Net> population;
//    DeterministicCrowdingPopulation population;

    Net neval;

    EvaluateXOR evaluateXOR;

    public ExampleXOR() {
        NEAT problem = new NEAT();

        NEATConfig config = NEAT.getConfig();

        config.populationSize = 150;
        config.targetFitness = 15.9;
        config.maxGenerations = 1000;
        config.distanceDelta = 15;

        config.distanceC1 = 1.0;
        config.distanceC2 = 1.0;
        config.distanceC3 = 0.4;

        config.mutateAddLink = 0.3;
//        config.mutateAddLink = 0.8;

        config.mutateAddNeuron = 0.1;

        config.mutationPower = 10.0;

        config.recurrent = false;

        config.globalNeuronInnovationAcceptNewRatio = 1.0;
        config.globalNeuronInnovationAttentuationRatio = 0.8;

        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
//        RND.initialize(8804995495815986L);

        Net net = new Net(1);
        int[] h = {};
        net.createFeedForward(2, h, 1);

        Genome proto = new Genome(net);
        evaluateXOR = new EvaluateXOR();

        List<IGenotypeToPhenotype<Genome, Net>> converter = new ArrayList<IGenotypeToPhenotype<Genome, Net>>();
        converter.add(new GenomeToNet());

        List<IEvaluable<Net>> evaluator = new ArrayList<IEvaluable<Net>>();
        evaluator.add(evaluateXOR);

        PopulationManager<Genome, Net> populationManager = new PopulationManager<Genome, Net>(converter, evaluator);
        population = new FitnessSharingPopulation<Net>(populationManager, proto);
//        population = new DeterministicCrowdingPopulation(populationManager , proto);
        problem.setPopulation(population);

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(problem, new Stats(), false);
        solver.addProgressPrinter(new NEATBasicProgressPrinter(problem));
        solver.addStopCondition(new MaxGenerationsStopCondition(problem));
        solver.addStopCondition(new TargetFitnessStopCondition(problem));
        solver.run();

        neval = population.getBestSoFarNet();
        System.out.println(neval);
    }

    public static void main(String[] args) {
        new ExampleXOR();
    }

}