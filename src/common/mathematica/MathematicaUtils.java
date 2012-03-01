package common.mathematica;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2010
 * Time: 11:06:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaUtils {
    public static String toMathematica(double n) {
        return Double.toString(n).toUpperCase().replace("E", "*10^");
    }

    public static String arrayToMathematica(int[] array) {
        if (array.length == 0) {
            return "{}";
        }
        StringBuilder b = new StringBuilder("{");
        for (int i = 0; i < array.length - 1; i++) {
            b.append(array[i]).append(", ");
        }
        b.append(array[array.length - 1]);
        b.append("}");
        return b.toString();
    }

    public static String arrayToMathematica(double[] array) {
        if (array.length == 0) {
            return "{}";
        }
        StringBuilder b = new StringBuilder("{");
        for (int i = 0; i < array.length - 1; i++) {
            b.append(array[i]).append(", ");
        }
        b.append(array[array.length - 1]);
        b.append("}");
        return b.toString();
    }

    public static String matrixToMathematica(double[][] array) {
        if (array.length == 0) {
            return "{{}}\n";
        }
        StringBuilder b = new StringBuilder("{\n");
        for (int i = 0; i < array.length - 1; i++) {
            b.append(arrayToMathematica(array[i]));
            b.append(", \n");
        }
        b.append(arrayToMathematica(array[array.length - 1]));
        b.append("\n}");
        return b.toString();
    }

    public static void printArrayMathematica(int[] array) {
        System.out.print(arrayToMathematica(array));
    }

    public static void printArrayMathematica(double[] array) {
        System.out.print(arrayToMathematica(array));
    }

    public static void printMatrixMathematica(double[][] array) {
        if (array.length == 0) {
            System.out.println("{{}}");
        }
        System.out.println("{");
        for (int i = 0; i < array.length - 1; i++) {
            printArrayMathematica(array[i]);
            System.out.println(", ");
        }
        printArrayMathematica(array[array.length - 1]);
        System.out.print("\n}");
    }
}
