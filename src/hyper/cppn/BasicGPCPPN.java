package hyper.cppn;

import gp.Forest;
import hyper.substrate.ICoordinate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 8:51:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicGPCPPN implements ICPPN {
    final private Forest forest;
    final private int maxCoordinateDimension;
    final private double[] in;

    public BasicGPCPPN(Forest forest, int maxCoordinateDimension) {
        this.forest = forest;
        this.maxCoordinateDimension = maxCoordinateDimension;
        this.in = new double[forest.getNumOfInputs()];
    }

    public double evaluate(int outputId, ICoordinate from, ICoordinate to) {
        //TODO same in BasicNetCPPN
        if (from.getDimension() > maxCoordinateDimension || to.getDimension() > maxCoordinateDimension) {
            throw new IllegalArgumentException("One of from: " + from + ", to: " + to + " greater than the maxCoordinateDimension: " + maxCoordinateDimension
            );
        }
        int cnt = 0;
        //there are zeros in remaining dimensions (in case of different coordinate dimensions)
        for (int i = 0; i < from.getDimension(); i++) {
            in[cnt++] = from.get(i);
        }

        cnt = maxCoordinateDimension;
        for (int i = 0; i < to.getDimension(); i++) {
            in[cnt++] = to.get(i);
        }

        forest.loadInputs(in);
        //TODO caching?
        return forest.getOutputs()[outputId];
    }

    public int getNumInputs() {
        return maxCoordinateDimension * 2;
    }
}