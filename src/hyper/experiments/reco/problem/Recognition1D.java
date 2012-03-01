package hyper.experiments.reco.problem;

import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.experiments.reco.fitness.HyperNetEvaluator1D;
import hyper.experiments.reco.fitness.RecognitionFitness1D;
import hyper.experiments.reco.util.PatternGenerator;
import hyper.experiments.reco.util.PatternGeneratorFactory;
import hyper.experiments.reco.util.PatternUtils;
import hyper.substrate.BasicSubstrate;
import hyper.substrate.ISubstrate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 12:15:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recognition1D implements IProblem<INet> {
    final private PatternGenerator generator;
    final private int activations;
    final private int lineSize;
    final private double fitnessTolerance;
    final private String problem;

    private boolean solved = false;

    public Recognition1D(ParameterCombination parameters) {
        this(parameters, 0.0);
    }

    public Recognition1D(ParameterCombination parameters, double fitnessTolerance) {
        this.generator = PatternGeneratorFactory.createByName(parameters);
        this.activations = parameters.getInteger("NET_ACTIVATIONS");
        this.lineSize = parameters.getInteger("RECO.LINE_SIZE");
        this.problem = parameters.getString("PROBLEM");
        this.fitnessTolerance = fitnessTolerance;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        HyperNetEvaluator1D hyperNetEvaluator = new HyperNetEvaluator1D(hyperNet, activations);

        //zatim pouze pro 1D, pak predelat
        RecognitionFitness1D recognition = new RecognitionFitness1D(hyperNetEvaluator);

        double fitness = recognition.evaluate(generator.generateInputPatterns(), generator.generateOutputPatterns());
        solved = solved || recognition.isSolved();

        return new EvaluationInfo(fitness);
    }

    public void show(INet hyperNet) {
        HyperNetEvaluator1D hyperNetEvaluator = new HyperNetEvaluator1D(hyperNet, activations);
        //zatim pouze pro 1D, pak predelat
        RecognitionFitness1D recognition = new RecognitionFitness1D(hyperNetEvaluator);
        double[][] inputPatterns = generator.generateInputPatterns();
        double[][] outputPatterns = generator.generateOutputPatterns();
        for (int i = 0; i < inputPatterns.length; i++) {
            PatternUtils.printFormattedPattern(inputPatterns[i]);
            System.out.print(" ");
            PatternUtils.printFormattedPattern(outputPatterns[i]);
            System.out.print(" ");
            double[] output = recognition.propagate(inputPatterns[i]);
            PatternUtils.printFormattedPattern(output);
//            System.out.print(" ");
//            PatternUtils.printPattern(output);
            System.out.println("");
        }
    }

    public double getTargetFitness() {
        double targetFitness = generator.generateInputPatterns().length * generator.generateOutputPatterns()[0].length;
        targetFitness -= fitnessTolerance * targetFitness;
        return targetFitness;
    }

    public ISubstrate getSubstrate() {
        BasicSubstrate substrate;
        if (problem.equals("RECO")) {
            substrate = RecoSubstrateFactory.createInputToOutput(lineSize);
//            substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, lineSize);
//            substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 3, 1);
        } else if (problem.equals("XOR")) {
            substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, lineSize, 1);
        } else if (problem.equals("AND")) {
            substrate = RecoSubstrateFactory.createInputToOutput(lineSize, 1);
//            substrate = RecoSubstrateFactory.createInputHiddenOutput(lineSize, 2, 1);
        } else {
            throw new IllegalStateException("Unknown problem type: " + problem);
        }
        return substrate;
    }

    public boolean isSolved() {
        return solved;
    }

    public List<String> getEvaluationInfoItemNames() {
        return new ArrayList<String>();
    }
}
