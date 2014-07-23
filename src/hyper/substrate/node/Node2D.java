package hyper.substrate.node;

import common.net.linked.Neuron;
import hyper.substrate.Coordinate2D;

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
public class Node2D implements INode {
    final private Coordinate2D coordinate;
    final private NodeType type;
    final private Neuron.Activation activationFunction;

    public Node2D(double x, double y, NodeType type) {
        this(x, y, type, Neuron.Activation.SIGMOID);
    }

    public Node2D(double x, double y, NodeType type, Neuron.Activation activationFunction) {
        this.coordinate = new Coordinate2D(x, y);
        this.type = type;
        this.activationFunction = activationFunction;
    }

    public Coordinate2D getCoordinate() {
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
        return "Node2D{" +
                "coordinate=" + coordinate +
                ", type=" + type +
                ", activationFunction=" + activationFunction +
                '}';
    }
}
