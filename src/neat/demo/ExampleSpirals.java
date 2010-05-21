package neat.demo;

import common.RND;
import common.function.Function3D;
import neat.*;

import java.awt.*;

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

public class ExampleSpirals {

    public static final int AXES = 1,
            SPIRALS = 2,
            SPIRAL = 3;

    FitnessSharingPopulation population;
//    DeterministicCrowdingPopulation population;

    Net neval;

//    VNet vn;

    int evals = 0;

    EvaluateSpirals evaluateSpirals;

    class SpiralsDraw implements Function3D {
        public double getValue(double ox, double oy) {
            double[] in = {1.0, ox, oy};
            neval.loadInputs(in);
            neval.reset();
            evaluateSpirals.activate(neval);
            double[] y = neval.getClampedOutputValues(-1.0, 1.0);
//            return y[0]-y[1];
//            if (y[0] < y[1]) {
//                return -1.0;
//            } else {
//                return 1.0;
//            }
            return y[0];
        }
    }

    public ExampleSpirals() {
        NEAT problem = new NEAT();

        NEATConfig config = NEAT.getConfig();

        config.populationSize = 150;
        config.targetFitness = 200;
        config.lastGeneration = 1000;
        config.distanceDelta = 15;

//        config.distanceC1 = 1.0;
//        config.distanceC2 = 1.0;
//        config.distanceC3 = 0.4;

        config.distanceC1 = 2.0;
        config.distanceC2 = 2.0;
        config.distanceC3 = 0.5;
        config.distanceCActivation = 1.0;

//        config.mutateAddLink = 0.3;
//        config.mutateAddNeuron = 0.1;
        config.mutateAddLink = 0.3;
        config.mutateAddNeuron = 0.1;
        config.mutateActivation = 0.1;

        config.mutationPower = 10.0;

        config.recurrent = false;

        config.globalNeuronInnovationAcceptNewRatio = 1.0;
        config.globalNeuronInnovationAttentuationRatio = 0.8;

//        config.activationSigmoidProbability = 0.0;
//        config.activationLinearProbability = 0.5;
//        config.activationGaussProbability = 0.0;
//        config.activationAbsProbability = 0.0;
//        config.activationSinProbability = 0.5;

        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
//        RND.initialize(8804995495815986L);

        Net net = new Net(1);
        int[] h = {};
//        net.createFeedForward(2, h, 2);
        net.createFeedForward(2, h, 1);

        Genome proto = new Genome(net);
        evaluateSpirals = new EvaluateSpirals();

        population = new FitnessSharingPopulation(evaluateSpirals, proto);
//        population = new DeterministicCrowdingPopulation(evaluateSpirals, proto);

        problem.setPopulation(population);
        problem.setProgressPrinter(new BasicProgressPrinter(population));

        problem.run(false);

        neval = population.getBestSoFarNet();
        System.out.println(neval);
    }

    public static void main(String[] args) {
        new ExampleSpirals();
    }

}