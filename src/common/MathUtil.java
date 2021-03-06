package common;

/**
 * Created by IntelliJ IDEA.
 * User: honza
 * Date: 8.3.2007
 * Time: 20:44:25
 * To change this template use File | Settings | File Templates.
 */
public class MathUtil {
    /**
     * Dot product of 2 vectors.
     * TODO better use Colt package
     *
     * @param ovx
     * @param ovy
     * @return
     */
    public static double dotProduct(double[] ovx, double[] ovy) {
        double ddot = 0.0;

        if (ovx.length <= 0) {
            return ddot;
        }

        for (int i = 0; i < ovx.length; i++) {
            ddot += ovx[i] * ovy[i];
        }
        return ddot;
    }

    /**
     * Muliply vector by scalar.
     *
     * @param os
     * @param ov
     * @param oz
     */
    public static void multVectorByScalar(double os, double[] ov, double[] oz) {
        int i;
        for (i = 0; i < ov.length; i++) {
            oz[i] = os * ov[i];
        }
        return;
    }

    /**
     * This method implements the FORTRAN sign (not sin) function.
     *
     * @param oa
     * @param ob
     * @return
     */
    public static double sign(double oa, double ob) {
        if (ob < 0.0) {
            return -Math.abs(oa);
        } else {
            return Math.abs(oa);
        }
    }

    public static void negateVector(double[] ovec) {
        for (int i = 0; i < ovec.length; i++) {
            ovec[i] = -ovec[i];
        }
    }

    /**
     * Prints given vector.
     *
     * @param ovec
     */
    public static void printlnVector(double[] ovec) {
        int i;
        for (i = 0; i < ovec.length - 1; i++) {
            System.out.print(ovec[i] + ",");
        }
        System.out.println(ovec[i]);
    }

    public static int maxIndexFirst(double[] a) {
        double m = a[0];
        int idx = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > m) {
                m = a[i];
                idx = i;
            }
        }
        return idx;
    }

    //squashes a vector of doubles to a probability vector of the same size
    //see http://en.wikipedia.org/wiki/Softmax_function
    public static double[] softmax(double[] a) {
        double[] t = new double[a.length];
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            t[i] = Math.exp(a[i]);
            sum += t[i];
        }
        for (int i = 0; i < a.length; i++) {
            t[i] /= sum;
        }
        return t;
    }

    //p is a vector of probabilities which sums to 1
    //method returns a random index given the probabilities
    public static int roulette(double[] p) {
        double r = RND.getDouble();
        double sum = 0.0; //running total
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
            if (r < sum) {
                return i;
            }
        }
        return p.length - 1;
    }


    public static double[][] partition(double[] v, int columns) {
        assert (v.length % columns == 0);
        double[][] n = new double[v.length / columns][];
        int offset = 0;
        for (int row = 0; row < n.length; row++) {
            n[row] = new double[columns];
            System.arraycopy(v, offset, n[row], 0, columns);
            offset += columns;
        }
        return n;
    }

}
