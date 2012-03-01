package divvis;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 8:54:16 PM
 * This class converts multidimensional data to distance matrix.
 * Although distance matrix is symetric, it is represented as a simple two dimensional
 * NxN array. Data samples are stored in rows while columns represent features.
 */
public class DistanceMatrix {
    public static double[][] createDistanceMatrix(double[][] odata, DistanceInterface odistance) {
        int n = odata.length;
        double[][] D = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                D[i][j] = D[j][i] = odistance.distance(odata[i], odata[j]);
            }
        }
        return D;
    }

    public static double[][] createDistanceMatrix(double[][] odata) {
        return createDistanceMatrix(odata, new DistanceEuclidean(odata[0].length));
    }
}
