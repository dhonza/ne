package hyper.substrate.layer;

import common.net.linked.Neuron;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 15, 2009
 * Time: 1:25:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineLayerLateral1D extends LineLayer1D {
    public LineLayerLateral1D(NodeType nodeType, int xNodes, double xStart, double xStep, boolean biased) {
        super(nodeType, xNodes, xStart, xStep, biased);
    }

    public LineLayerLateral1D(NodeType nodeType, int xNodes, double xStart, double xStep, boolean biased, Neuron.Activation activationFunction) {
        super(nodeType, xNodes, xStart, xStep, biased, activationFunction);
    }

    public SubstrateIntraLayerConnection[] getIntraLayerConnections() {
        SubstrateIntraLayerConnection[] connections = new SubstrateIntraLayerConnection[(xNodes - 1) * 2];
        if (xNodes > 1) {
            int cnt = 0;
            for (int i = 0; i < xNodes - 1; i++) {
                connections[cnt++] = new SubstrateIntraLayerConnection(nodes[i], nodes[i + 1]);
                connections[cnt++] = new SubstrateIntraLayerConnection(nodes[i + 1], nodes[i]);
            }
        }
        return connections;
    }

    @Override
    public boolean hasIntraLayerConnections() {
        return true;
    }
}
