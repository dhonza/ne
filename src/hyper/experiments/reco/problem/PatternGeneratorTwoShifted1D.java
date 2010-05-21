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
public class PatternGeneratorTwoShifted1D implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorTwoShifted1D(ParameterCombination parameters) {
        int n = parameters.getInteger("RECO.LINE_SIZE");
        double[][] patterns = PatternUtils.extractPatterns(parameters.getString("RECO.PATTERNS"));
        if (patterns.length != 2) {
            throw new IllegalStateException("Needs exactly 2 patterns.");
        }
        int combinations = 0;
        for (double[] pattern : patterns) {
            combinations += n - pattern.length + 1;
        }
        inputPatterns = new double[combinations][n];
        outputPatterns = new double[combinations][1];

        int cnt = 0;

        for (int p = 0; p < patterns.length; p++) {
            for (int i = 0; i <= n - patterns[p].length; i++) {
                System.arraycopy(patterns[p], 0, inputPatterns[cnt], i, patterns[p].length);
                outputPatterns[cnt++][0] = p;
            }
        }
        PatternUtils.printInputOutputPatterns(inputPatterns, outputPatterns);
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