package divvis;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 8:45:10 PM
 * This interface is used to derive various distance measures (DistanceEuclidean for example)
 */
public interface DistanceInterface {
    public double distance(double[] ox1, double[] ox2);
}
