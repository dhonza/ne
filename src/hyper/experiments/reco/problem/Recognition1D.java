package hyper.experiments.reco.problem;

import common.pmatrix.ParameterCombination;
import hyper.evaluate.Problem;
import hyper.experiments.reco.fitness.HyperNetEvaluator1D;
import hyper.experiments.reco.fitness.RecognitionFitness1D;
import hyper.experiments.reco.util.PatternGenerator;
import hyper.experiments.reco.util.PatternGeneratorFactory;
import hyper.experiments.reco.util.PatternUtils;
import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 12:15:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recognition1D implements Problem {
    final private PatternGenerator generator;
    final private int activations;
    final private double fitnessTolerance;

    public Recognition1D(ParameterCombination parameters) {
        this(parameters, 0.0);
    }

    public Recognition1D(ParameterCombination parameters, double fitnessTolerance) {
        this.generator = PatternGeneratorFactory.createByName(parameters);
        this.activations = parameters.getInteger("RECO.ACTIVATIONS");
        this.fitnessTolerance = fitnessTolerance;
    }

    public double evaluate(Net hyperNet) {
        HyperNetEvaluator1D hyperNetEvaluator = new HyperNetEvaluator1D(hyperNet, activations);

        //zatim pouze pro 1D, pak predelat
        RecognitionFitness1D recognition = new RecognitionFitness1D(hyperNetEvaluator);

        return recognition.evaluate(generator.generateInputPatterns(), generator.generateOutputPatterns());
    }

    public void show(Net hyperNet) {
        HyperNetEvaluator1D hyperNetEvaluator = new HyperNetEvaluator1D(hyperNet, activations);
        //zatim pouze pro 1D, pak predelat
        RecognitionFitness1D recognition = new RecognitionFitness1D(hyperNetEvaluator);
        for (int i = 0; i < generator.generateInputPatterns().length; i++) {
            PatternUtils.printPattern(generator.generateInputPatterns()[i]);
            System.out.print(" ");
            PatternUtils.printPattern(generator.generateOutputPatterns()[i]);
            System.out.print(" ");
            double[] output = recognition.propagate(generator.generateInputPatterns()[i]);
            PatternUtils.printPattern(output);
            System.out.println("");
        }
    }

    public double getTargetFitness() {
        double targetFitness = generator.generateInputPatterns().length * generator.generateOutputPatterns()[0].length;
        targetFitness -= fitnessTolerance * targetFitness;
        return targetFitness;
    }
}
