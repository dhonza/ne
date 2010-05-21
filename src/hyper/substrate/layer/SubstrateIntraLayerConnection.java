package hyper.substrate.layer;

import hyper.substrate.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 15, 2009
 * Time: 1:28:40 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class SubstrateIntraLayerConnection {
    final private Node from;
    final private Node to;

    public SubstrateIntraLayerConnection(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}
