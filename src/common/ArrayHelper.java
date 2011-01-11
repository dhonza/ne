package common;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 15, 2010
 * Time: 12:26:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayHelper {
    public static double mean(double[] array) {
        double sum = 0.0;
        for (double v : array) {
            sum += v;
        }
        return sum / array.length;
    }

    public static void range(int[] array, int min) {
        for (int i = 0; i < array.length; i++) {
            array[i] = min++;
        }
    }

    public static void printArray(double[] array) {
        for (double v : array) {
            System.out.print(v + " ");
        }
    }

    public static void printArrayMathematica(double[] array) {
        System.out.print("{");
        for (int i = 0; i < array.length - 1; i++) {
            System.out.print(array[i] + ", ");
        }
        System.out.print(array[array.length - 1]);
        System.out.print("}");
    }

    public static void printMatrixMathematica(double[][] array) {
        System.out.println("{");
        for (int i = 0; i < array.length - 1; i++) {
            printArrayMathematica(array[i]);
            System.out.println(", ");
        }
        printArrayMathematica(array[array.length - 1]);
        System.out.print("}");
    }

}
