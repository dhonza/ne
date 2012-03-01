package divvis;

import java.util.Arrays;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 8:48:48 PM
 * Implements euclidean distance.
 */
public class DistanceEuclideanWeighted extends DistanceEuclidean {
    double[] weights;

    public DistanceEuclideanWeighted(int on) {
        super(on);
        double[] tweights = new double[n];
        Arrays.fill(tweights, 1.0);
        setWeights(tweights);
    }

    public DistanceEuclideanWeighted(int on, double[] oweights) {
        super(on);
        setWeights(oweights);
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double distance(double[] ox1, double[] ox2) {
        double t = 0.0;
        for (int i = 0; i < ox1.length; i++) {
            t += (ox2[i] - ox1[i]) * (ox2[i] - ox1[i]) * weights[i] * weights[i];
        }
        return Math.sqrt(t);
    }
}
