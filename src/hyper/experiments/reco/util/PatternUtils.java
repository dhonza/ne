package hyper.experiments.reco.util;

import common.pmatrix.Utils;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 10, 2009
 * Time: 3:51:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternUtils {
    private static DecimalFormat format = new DecimalFormat("0");

    public static double[][] generateAllPatterns(int nodes) {
        int n = 1 << nodes;
        double[][] allPatterns = new double[n][nodes];
        for (int p = 0; p < n; p++) {
            for (int i = 0; i < nodes; i++) {
                allPatterns[p][nodes - i - 1] = (p & (1 << i)) > 0 ? 1.0 : 0.0;
            }
        }
        return allPatterns;
    }

    public static double[][] generateAllPatternsNegative(int nodes) {
        int n = 1 << nodes;
        double[][] allPatterns = new double[n][nodes];
        for (int p = 0; p < n; p++) {
            for (int i = 0; i < nodes; i++) {
                allPatterns[p][nodes - i - 1] = (p & (1 << i)) > 0 ? 0.0 : 1.0;
            }
        }
        return allPatterns;
    }

    public static double[][] generateAllPatternsMirrored(int nodes) {
        int n = 1 << nodes;
        double[][] allPatterns = new double[n][nodes];
        for (int p = 0; p < n; p++) {
            for (int i = 0; i < nodes; i++) {
                allPatterns[p][i] = (p & (1 << i)) > 0 ? 1.0 : 0.0;
            }
        }
        return allPatterns;
    }

    /**
     * @param sourcePattern source pattern
     * @param shiftBy       rotate - positive to right, negative to left
     * @return rotated pattern
     */
    public static double[][] generateRotated(double[][] sourcePattern, int shiftBy) {
        int n = sourcePattern.length;
        int nodes = sourcePattern[0].length;
        double[][] targetPattern = new double[n][nodes];
        for (int p = 0; p < n; p++) {
            if (shiftBy >= 0) {
                System.arraycopy(sourcePattern[p], 0, targetPattern[p], shiftBy, nodes - shiftBy);
                System.arraycopy(sourcePattern[p], nodes - shiftBy, targetPattern[p], 0, shiftBy);
            } else {
                System.arraycopy(sourcePattern[p], -shiftBy, targetPattern[p], 0, nodes + shiftBy);
                System.arraycopy(sourcePattern[p], 0, targetPattern[p], nodes + shiftBy, -shiftBy);
            }
        }
        return targetPattern;
    }

    /**
     * @param sourcePattern source pattern
     * @param shiftBy       shift - positive to right, negative to left
     * @return shifted pattern
     */
    public static double[][] generateShifted(double[][] sourcePattern, int shiftBy) {
        int n = sourcePattern.length;
        int nodes = sourcePattern[0].length;
        double[][] targetPattern = new double[n][nodes];
        for (int p = 0; p < n; p++) {
            if (shiftBy >= 0) {
                System.arraycopy(sourcePattern[p], 0, targetPattern[p], shiftBy, nodes - shiftBy);
            } else {
                System.arraycopy(sourcePattern[p], -shiftBy, targetPattern[p], 0, nodes + shiftBy);
            }
        }
        return targetPattern;
    }

    /**
     * Inserts 1.0 at index 0 of each pattern - biasing of neural networks.
     *
     * @param sourcePattern
     * @return
     */
    public static double[][] generateBiased(double[][] sourcePattern) {
        int n = sourcePattern.length;
        int nodes = sourcePattern[0].length;
        double[][] targetPattern = new double[n][nodes + 1];
        for (int p = 0; p < n; p++) {
            System.arraycopy(sourcePattern[p], 0, targetPattern[p], 1, nodes);
            targetPattern[p][0] = 1.0;
        }
        return targetPattern;
    }

    public static double[][] extractPatterns(String patterns) {
        String[] patternCodes = Utils.extractIdentificators(patterns);
        double[][] patternMatrix = new double[patternCodes.length][];
        for (int i = 0; i < patternCodes.length; i++) {
            patternMatrix[i] = new double[patternCodes[i].length()];
            for (int j = 0; j < patternCodes[i].length(); j++) {
                if (patternCodes[i].charAt(j) == '1') {
                    patternMatrix[i][j] = 1.0;
                } else if (patternCodes[i].charAt(j) == '0') {
                    patternMatrix[i][j] = 0.0;
                } else {
                    throw new IllegalArgumentException("Wrong pattern: " + patternCodes[i]);
                }
            }
        }
        return patternMatrix;
    }

    public static void printPattern(double[] pattern) {
        StringBuilder sb = new StringBuilder();
        for (double element : pattern) {
            sb.append(format.format(element)).append(" ");
        }
        System.out.print(sb);
    }

    public static void printInputOutputPatterns(double[][] inputPatterns, double[][] outputPatterns) {
        for (int p = 0; p < inputPatterns.length; p++) {
            printPattern(inputPatterns[p]);
            System.out.print(" ");
            printPattern(outputPatterns[p]);
            System.out.println();
        }
    }
}
