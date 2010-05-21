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
public class PatternGeneratorCopyRotated1D implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorCopyRotated1D(ParameterCombination parameters) {
        inputPatterns = PatternUtils.generateAllPatterns(parameters.getInteger("RECO.LINE_SIZE"));
        outputPatterns = PatternUtils.generateRotated(inputPatterns, parameters.getInteger("RECO.SHIFT_BY"));
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