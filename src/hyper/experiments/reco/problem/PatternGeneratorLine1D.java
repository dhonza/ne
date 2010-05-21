package hyper.experiments.reco.problem;

import common.pmatrix.ParameterCombination;
import hyper.experiments.reco.util.PatternGenerator;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 10:15:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class PatternGeneratorLine1D implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorLine1D(ParameterCombination parameters) {
        int n = parameters.getInteger("RECO.LINE_SIZE");
        int combinations = (int) (n * ((1.0 + n) / 2.0));
        inputPatterns = new double[combinations][n];
        outputPatterns = new double[combinations][n];
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                inputPatterns[cnt][i] = 1.0;
                inputPatterns[cnt][j] = 1.0;
                for (int k = i; k <= j; k++) {
                    outputPatterns[cnt][k] = 1.0;
                }
                cnt++;
            }
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