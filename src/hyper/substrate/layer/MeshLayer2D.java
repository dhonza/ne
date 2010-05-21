package hyper.substrate.layer;

import hyper.substrate.node.Node;
import hyper.substrate.node.Node2D;
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
public class MeshLayer2D implements SubstrateLayer {
    final private NodeType nodeType;
    final private int xNodes;
    final private int yNodes;
    final private double xMin;
    final private double xMax;
    final private double xScale;
    final private double yMin;
    final private double yMax;
    final private double yScale;

    final private Node[] nodes;

    public MeshLayer2D(NodeType nodeType, int xNodes, int yNodes, double xScale, double yScale) {
        this.nodeType = nodeType;
        this.xNodes = xNodes;
        this.yNodes = yNodes;
        this.xScale = xScale;
        this.xMax = xScale / 2.0;
        this.xMin = -xMax;
//        this.xMax = xScale;
//        this.xMin = 0.0;
        this.yScale = yScale;
        this.yMax = yScale / 2.0;
        this.yMin = -yMax;
//        this.yMax = yScale;
//        this.yMin = 0.0;

        this.nodes = new Node[xNodes * yNodes];
        createNodes();
    }

    private void createNodes() {
        double xStep = xScale / (xNodes - 1);
        double yStep = yScale / (yNodes - 1);
        double xPos = xMin;
        double yPos = yMax;
        int cnt = 0;
        for (int i = 0; i < yNodes; i++) {
            for (int j = 0; j < xNodes; j++) {
                nodes[cnt++] = new Node2D(xPos, yPos, nodeType);
                System.out.println("MeshLayer2D: SEVERE ROUNDOFF ERROR POSSIBLE!");
                xPos += xStep;
            }
            xPos = xMin;
            yPos -= yStep;
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
        return 2;
    }

    public SubstrateIntraLayerConnection[] getIntraLayerConnections() {
        return new SubstrateIntraLayerConnection[0];
    }

    public boolean hasIntraLayerConnections() {
        return false;
    }
}
