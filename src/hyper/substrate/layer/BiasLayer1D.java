package hyper.substrate.layer;

import hyper.substrate.node.Node;
import hyper.substrate.node.Node1D;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 9:31:29 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class BiasLayer1D implements SubstrateLayer, IBias {
    final private Node node;

    public BiasLayer1D(double x) {
        node = new Node1D(x, getNodeType());
    }

    public int getNumber() {
        return 1;
    }

    public Node[] getNodes() {
        return new Node[]{node};
    }

    public NodeType getNodeType() {
        return NodeType.BIAS;
    }

    public int getDimension() {
        return 1;
    }

    public boolean hasIntraLayerConnections() {
        return false;
    }

    public SubstrateIntraLayerConnection[] getIntraLayerConnections() {
        return new SubstrateIntraLayerConnection[0];
    }
}