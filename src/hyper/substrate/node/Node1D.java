package hyper.substrate.node;

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
public class Node1D implements Node {
    private Coordinate1D coordinate;
    private NodeType type;

    public Node1D(double x, NodeType type) {
        this.coordinate = new Coordinate1D(x);
        this.type = type;
    }

    public Coordinate1D getCoordinate() {
        return coordinate;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Node1D{" +
                "coordinate=" + coordinate +
                ", type=" + type +
                '}';
    }
}