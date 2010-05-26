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
public class PatternGeneratorZoomAll1D implements PatternGenerator {
    final private double[][] inputPatterns;
    final private double[][] outputPatterns;

    public PatternGeneratorZoomAll1D(ParameterCombination parameters) {
        int lineSize = parameters.getInteger("RECO.LINE_SIZE");
        inputPatterns = PatternUtils.generateAllPatterns(lineSize);
        outputPatterns = new double[inputPatterns.length][lineSize];
        for (int p = 0; p < inputPatterns.length; p++) {
            double[] pattern = inputPatterns[p];
            int l = -1;//leftmost point
            for (int i = 0; i < lineSize; i++) {
                if (pattern[i] == 1.0) {
                    l = i;
                    break;
                }
            }
//            System.out.print("I:");
//            PatternUtils.printFormattedPattern(pattern);
//            System.out.print(" l=" + l);
            if (l == -1) {
//                System.out.println("");
                continue;
            }
            int r = l;//rightmost point
            for (int i = lineSize - 1; i > l; i--) {
                if (pattern[i] == 1.0) {
                    r = i;
                    break;
                }

            }
//            System.out.print(" r=" + r + " ");
            int diff = r - l + 1;
            double ptWidth = 1.0 / diff;
            double lSide = l - ptWidth / 2.0;
            double rSide = r + ptWidth / 2.0;
            double lineWidth = rSide - lSide;
            double targetPtWidth = 1.0 / lineSize;
            double targetWidth = 1.0;
//            System.out.print("lineWidth=" + lineWidth + " ");
            for (int i = 0; i < lineSize; i++) {
                double pos = lSide + (((i + 0.5) * targetPtWidth) / targetWidth) * lineWidth;
//                System.out.print(pos + " ");
                outputPatterns[p][i] = pattern[((int) Math.round(pos))];
            }
//            System.out.print(" O:");
//            PatternUtils.printFormattedPattern(outputPatterns[p]);
//            System.out.println("");
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