package common;

import common.mathematica.MathematicaUtils;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 15, 2010
 * Time: 12:26:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayHelper {
    public static double[] flatten(double[][] arrays) {
        int len = arrays[0].length;
        for (int i = 1; i < arrays.length; i++) {
            len += arrays[i].length;
        }
        double[] dst = new double[len];
        int start = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, dst, start, arrays[i].length);
            start += arrays[i].length;
        }
        return dst;
    }

    public static double mean(double[] array) {
        double sum = 0.0;
        for (double v : array) {
            sum += v;
        }
        return sum / array.length;
    }

    public static double[][] partition(double[] array, int[] parts) {
        double[][] dst = new double[parts.length][];
        int start = 0;
        for (int i = 0; i < parts.length; i++) {
            dst[i] = new double[parts[i]];
            System.arraycopy(array, start, dst[i], 0, parts[i]);
            start += parts[i];
        }
        if (array.length != start) {
            throw new IllegalStateException("Array does not match sum of parts!");
        }
        return dst;
    }

    public static void range(int[] array, int min) {
        for (int i = 0; i < array.length; i++) {
            array[i] = min++;
        }
    }

    public static void main(String[] args) {
        double[][] t1 = new double[][]{{1, 2, 3}, {4, 5, 6, 7}, {}, {8, 9}, {10}};
        double[] t2 = flatten(t1);
        double[][] t3 = partition(t2, new int[]{1, 2, 3, 4});
        MathematicaUtils.printMatrixMathematica(t1);
        System.out.println();
        MathematicaUtils.printArrayMathematica(t2);
        System.out.println();
        MathematicaUtils.printMatrixMathematica(t3);
    }
}
