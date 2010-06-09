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
public class PatternGeneratorXOR implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorXOR(ParameterCombination parameters) {
        inputPatterns = PatternUtils.generateAllPatterns(parameters.getInteger("RECO.LINE_SIZE"));
        outputPatterns = new double[inputPatterns.length][1];
        for (int i = 0; i < inputPatterns.length; i++) {
            double[] inputPattern = inputPatterns[i];
            int xor = (int) inputPattern[0];
            for (int j = 1; j < inputPattern.length; j++) {
                xor ^= (int) inputPattern[j];
            }
            outputPatterns[i][0] = xor;
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