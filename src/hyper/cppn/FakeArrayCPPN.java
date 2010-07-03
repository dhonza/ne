package hyper.cppn;

import hyper.substrate.Coordinate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 1:39:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeArrayCPPN implements CPPN {
    final private double[] weights;
    final private int maxCoordinateDimension;

    private int cnt = 0;

    public FakeArrayCPPN(double[] weights, int maxCoordinateDimension) {
        this.weights = weights;
        this.maxCoordinateDimension = maxCoordinateDimension;
    }

    public double evaluate(int outputId, Coordinate from, Coordinate to) {
        if (cnt >= weights.length) {
            throw new IllegalStateException("All " + weights.length + " weights already evaluated!");
        }
        return weights[cnt++];
    }

    public int getNumInputs() {
        return maxCoordinateDimension * 2;
    }
}
