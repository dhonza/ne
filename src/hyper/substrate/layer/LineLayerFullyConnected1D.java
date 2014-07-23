package hyper.substrate.layer;

import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 15, 2009
 * Time: 1:25:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineLayerFullyConnected1D extends LineLayer1D {
    public LineLayerFullyConnected1D(NodeType nodeType, int xNodes, double xStart, double xStep, boolean biased) {
        super(nodeType, xNodes, xStart, xStep, biased);
    }

    public SubstrateIntraLayerConnection[] getIntraLayerConnections() {
        SubstrateIntraLayerConnection[] connections = new SubstrateIntraLayerConnection[xNodes * xNodes];
        int cnt = 0;
        for (int i = 0; i < xNodes; i++) {
            for (int j = 0; j < xNodes; j++) {
                connections[cnt++] = new SubstrateIntraLayerConnection(nodes[i], nodes[j]);
            }
        }
        return connections;
    }

    @Override
    public boolean hasIntraLayerConnections() {
        return true;
    }

    public int getNumberOfIntraLayerConnections() {
        return xNodes * xNodes;
    }
}