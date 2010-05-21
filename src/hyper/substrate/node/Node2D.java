package hyper.substrate.node;

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
public class Node2D implements Node {
    private Coordinate2D coordinate;
    private NodeType type;

    public Node2D(double x, double y, NodeType type) {
        this.coordinate = new Coordinate2D(x, y);
        this.type = type;
    }

    public Coordinate2D getCoordinate() {
        return coordinate;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String
    toString() {
        return "Node2D{" +
                "coordinate=" + coordinate +
                ", type=" + type +
                '}';
    }
}
