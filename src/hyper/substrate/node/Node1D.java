package hyper.substrate.node;

import common.net.linked.Neuron;
import hyper.substrate.Coordinate1D;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 8:15:39 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class Node1D implements INode {
    final private Coordinate1D coordinate;
    final private NodeType type;
    final private Neuron.Activation activationFunction;

    public Node1D(double x, NodeType type) {
        this(x, type, Neuron.Activation.SIGMOID);
    }

    public Node1D(double x, NodeType type, Neuron.Activation activationFunction) {
        this.coordinate = new Coordinate1D(x);
        this.type = type;
        this.activationFunction = activationFunction;
    }

    public Coordinate1D getCoordinate() {
        return coordinate;
    }

    public NodeType getType() {
        return type;
    }

    public Neuron.Activation getActivationFunction() {
        return activationFunction;
    }

    @Override
    public String toString() {
        return "Node1D{" +
                "coordinate=" + coordinate +
                ", type=" + type +
                ", activationFunction=" + activationFunction +
                '}';
    }
}