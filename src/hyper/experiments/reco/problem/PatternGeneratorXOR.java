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
public class PatternGeneratorXOR implements PatternGenerator {
    final private double[][] inputpatterns = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    final private double[][] outputpatterns = {{0}, {1}, {1}, {0}};

    public PatternGeneratorXOR(ParameterCombination parameters) {
    }

    public double[][] generateInputPatterns() {
        //TODO make a defensive copy
        return inputpatterns;
    }

    public double[][] generateOutputPatterns() {
        //TODO make a defensive copy
        return outputpatterns;
    }
}