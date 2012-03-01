package divvis;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 8:48:48 PM
 * Implements euclidean distance.
 */
public class DistanceQuad implements DistanceInterface {

    protected int n;

    public DistanceQuad(int on) {
        n = on;
    }

    public double distance(double[] ox1, double[] ox2) {
        double t = 0.0;
        for (int i = 0; i < ox1.length; i++) {
            t += (ox2[i] - ox1[i]) * (ox2[i] - ox1[i]) * (ox2[i] - ox1[i]) * (ox2[i] - ox1[i]);
        }
        return Math.sqrt(t);
    }
}
