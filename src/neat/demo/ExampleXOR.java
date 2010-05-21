package neat.demo;

import common.RND;
import common.function.Function3D;
import neat.*;

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

    public static final int AXES = 1, XOR = 2;

    FitnessSharingPopulation population;
//    DeterministicCrowdingPopulation population;

    Net neval;

//    VNet vn;

    int evals = 0;

    EvaluateXOR evaluateXOR;

    class XORDraw implements Function3D {
        public double getValue(double ox, double oy) {
            double[] in = {1.0, ox, oy};
            neval.loadInputs(in);
            neval.reset();
            evaluateXOR.activate(neval);
            double y = neval.getOutputValues()[0];
            return y;
        }
    }

    public ExampleXOR() {
        NEAT problem = new NEAT();

        NEATConfig config = NEAT.getConfig();

        config.populationSize = 150;
        config.targetFitness = 15.9;
        config.lastGeneration = 1000;
        config.distanceDelta = 15;

        config.distanceC1 = 1.0;
        config.distanceC2 = 1.0;
        config.distanceC3 = 0.4;

        config.mutateAddLink = 0.3;
        config.mutateAddNeuron = 0.1;

        config.mutationPower = 10.0;

        config.recurrent = false;

        config.globalNeuronInnovationAcceptNewRatio = 1.0;
        config.globalNeuronInnovationAttentuationRatio = 0.8;

//        System.out.println("INITIALIZED SEED: " + RND.initializeTime());
        RND.initialize(8804995495815986L);

        Net net = new Net(1);
        int[] h = {};
        net.createFeedForward(2, h, 1);

        Genome proto = new Genome(net);
        evaluateXOR = new EvaluateXOR();

        population = new FitnessSharingPopulation(evaluateXOR, proto);
//        population = new DeterministicCrowdingPopulation(evaluateXOR, proto);

        problem.setPopulation(population);
        problem.setProgressPrinter(new BasicProgressPrinter(population));

        problem.run(false);

        neval = population.getBestSoFarNet();
        System.out.println(neval);
    }

    public static void main(String[] args) {
        new ExampleXOR();
    }

}