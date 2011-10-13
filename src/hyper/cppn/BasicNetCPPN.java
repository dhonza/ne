package hyper.cppn;

import common.net.linked.Net;
import hyper.substrate.ICoordinate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 8:51:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicNetCPPN implements ICPPN {
    final private Net net;
    final private int maxCoordinateDimension;
    final private double[] in;

    public BasicNetCPPN(Net net, int maxCoordinateDimension) {
        this.net = net;
        this.maxCoordinateDimension = maxCoordinateDimension;
        this.in = new double[net.getNumInputs()];
        in[0] = 1.0; //bias
    }

    public double evaluate(int outputId, ICoordinate from, ICoordinate to) {
        if (from.getDimension() > maxCoordinateDimension || to.getDimension() > maxCoordinateDimension) {
            throw new IllegalArgumentException("One of from: " + from + ", to: " + to + " greater than the maxCoordinateDimension: " + maxCoordinateDimension
            );
        }
        int cnt = 1;
        //there are zeros in remaining dimensions (in case of different coordinate dimensions)
        for (int i = 0; i < from.getDimension(); i++) {
            in[cnt++] = from.get(i);
        }

        cnt = maxCoordinateDimension + 1;
        for (int i = 0; i < to.getDimension(); i++) {
            in[cnt++] = to.get(i);
        }

        net.loadInputs(in);
        net.reset();
        for (int i = 0; i < 5; i++) {
            net.activate();
        }
        return net.getOutputs()[outputId];
    }

    public int getNumInputs() {
        return maxCoordinateDimension * 2;
    }
}
