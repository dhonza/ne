package hyper.cppn;

import hyper.substrate.Coordinate;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 8:51:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicSNEATCPPN implements CPPN {
    final private INetwork net;
    final private int maxCoordinateDimension;
    final private float[] in; //TODO SNEAT works with floats!

    public BasicSNEATCPPN(INetwork net, int maxCoordinateDimension) {
        this.net = net;
        this.maxCoordinateDimension = maxCoordinateDimension;
        this.in = new float[net.getInputNeuronCount()];
    }

    public double evaluate(int outputId, Coordinate from, Coordinate to) {
        if (from.getDimension() > maxCoordinateDimension || to.getDimension() > maxCoordinateDimension) {
            throw new IllegalArgumentException("One of from: " + from + ", to: " + to + " greater than the maxCoordinateDimension: " + maxCoordinateDimension
            );
        }
        int cnt = 0;
        //there are zeros in remaining dimensions (in case of different coordinate dimensions)
        for (int i = 0; i < from.getDimension(); i++) {
            in[cnt++] = (float) from.get(i);
        }

        cnt = maxCoordinateDimension;
        for (int i = 0; i < to.getDimension(); i++) {
            in[cnt++] = (float) to.get(i);
        }

        net.clearSignals();
        net.setInputSignals(in);
        net.multipleSteps(5); //TODO recurrent substrates!

        return net.getOutputSignal(outputId);
    }

    public int getNumInputs() {
        return maxCoordinateDimension * 2;
    }
}