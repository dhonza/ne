package hyper.substrate.layer;

import hyper.substrate.node.Node;
import hyper.substrate.node.Node1D;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 7:23:13 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class LineLayer1D implements SubstrateLayer {
    final private NodeType nodeType;
    final protected int xNodes;
    final private double xStart;
    final private double xStep;
//    final private double xMin;
//    final private double xMax;
//    final private double xScale;

    final protected Node[] nodes;

    public LineLayer1D(NodeType nodeType, int xNodes, double xStart, double xStep) {
        this.nodeType = nodeType;
        this.xNodes = xNodes;
        this.xStart = xStart;
        this.xStep = xStep;
//        this.xScale = xScale;
//        this.xMax = xScale / 2.0;
//        this.xMin = -xMax;
//        this.xMax = xScale;
//        this.xMin = 0.0;
        this.nodes = new Node[xNodes];
        createNodes();
    }

    private void createNodes() {
        int cnt = 0;
        for (int j = 0; j < xNodes; j++) {
            nodes[cnt++] = new Node1D(xStart + j * xStep, nodeType);
        }
    }

    public int getNumber() {
        return nodes.length;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public int getDimension() {
        return 1;
    }

    public SubstrateIntraLayerConnection[] getIntraLayerConnections() {
        return new SubstrateIntraLayerConnection[0];
    }

    public boolean hasIntraLayerConnections() {
        return false;
    }
}