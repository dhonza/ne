package sneat.utilityclasses;

import java.util.Random;

/// <summary>

/// Summary description for Utilities.
/// </summary>
public class Utilities {
    private static Random random = new Random();
//    private static FastRandom random = new FastRandom();

    // Static/global method for generating random numbers.
    public static double nextDouble() {
        return random.nextDouble();
    }

    public static int next(int upperBound) {
        return random.nextInt(upperBound);
    }

    public static double LimitRange(double val, double lower, double upper) {
        val = Math.min(val, upper);
        return Math.max(val, lower);
    }

    public static void NormalizeValueArray(double targetMin, double targetMax, double[] valueArray) {
        if (valueArray == null || valueArray.length == 0)
            return;

        if (targetMin >= targetMax)
            throw new IllegalArgumentException();

        // Scan the array and make note of the min and max values.
        double min = valueArray[0];
        double max = valueArray[0];

        for (int i = 1; i < valueArray.length; i++) {
            if (valueArray[i] < min)
                min = valueArray[i];
            else if (valueArray[i] > max)
                max = valueArray[i];
        }

        // Now scale/translate the data into the target range.
        double range = max - min;
        double targetRange = targetMax - targetMin;

        if (range > 0) {
            for (int i = 0; i < valueArray.length; i++)
                valueArray[i] = ((valueArray[i] - min) / range) * targetRange + targetMin;
        } else {
            for (int i = 0; i < valueArray.length; i++)
                valueArray[i] = targetMin;
        }
    }
}

