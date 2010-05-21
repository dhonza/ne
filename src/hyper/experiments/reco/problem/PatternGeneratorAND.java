package hyper.experiments.reco.problem;

import common.pmatrix.ParameterCombination;
import hyper.experiments.reco.util.PatternGenerator;
import hyper.experiments.reco.util.PatternUtils;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 10:15:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class PatternGeneratorAND implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorAND(ParameterCombination parameters) {
        inputPatterns = PatternUtils.generateAllPatterns(parameters.getInteger("RECO.LINE_SIZE"));
        outputPatterns = new double[inputPatterns.length][1];
        for (int i = 0; i < inputPatterns.length; i++) {
            double[] inputPattern = inputPatterns[i];
            double and = 1.0;
            for (int j = 0; j < inputPattern.length; j++) {
                and *= inputPattern[j];
            }
            outputPatterns[i][0] = and;
        }
    }

    public double[][] generateInputPatterns() {
        //TODO make a defensive copy
        return inputPatterns;
    }

    public double[][] generateOutputPatterns() {
        //TODO make a defensive copy
        return outputPatterns;
    }
}