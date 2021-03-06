package hyper.substrate.layer;

import common.net.linked.Neuron;
import hyper.substrate.node.INode;
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
public class MeshLayer2D implements ISubstrateLayer {
    final private NodeType nodeType;
    final private int xNodes;
    final private int yNodes;
    final private double xMin;
    final private double xMax;
    final private double xScale;
    final private double yMin;
    final private double yMax;
    final private double yScale;
    final private boolean biased;
    final private Neuron.Activation activationFunction;

    final private INode[] nodes;

    public MeshLayer2D(NodeType nodeType, int xNodes, int yNodes, double xScale, double yScale, boolean biased) {
        this(nodeType, xNodes, yNodes, xScale, yScale, biased, Neuron.Activation.SIGMOID);
    }

    public MeshLayer2D(NodeType nodeType, int xNodes, int yNodes, double xScale, double yScale, boolean biased, Neuron.Activation activationFunction) {
        this.nodeType = nodeType;
        this.xNodes = xNodes;
        this.yNodes = yNodes;
        this.xScale = xScale;
        this.xMax = (xNodes == 1) ? 0.0 : xScale / 2.0;
        this.xMin = -xMax;
//        this.xMax = xScale;
//        this.xMin = 0.0;
        this.yScale = yScale;
        this.yMax = (yNodes == 1) ? 0.0 : yScale / 2.0;
        this.yMin = -yMax;
//        this.yMax = yScale;
//        this.yMin = 0.0;
        this.biased = biased;
        this.activationFunction = activationFunction;

        this.nodes = new INode[xNodes * yNodes];
        createNodes();
    }

    private void createNodes() {
        System.out.println("MeshLayer2D: SEVERE ROUNDOFF ERROR POSSIBLE, REPLACE THIS CODE!");
        double xStep = (xNodes == 1) ? 0.0 : xScale / (xNodes - 1);
        double yStep = (yNodes == 1) ? 0.0 : yScale / (yNodes - 1);
        double xPos = xMin;
        double yPos = yMax;
        int cnt = 0;
//        System.out.println("--------------------" + xNodes + " " + yNodes + " " + xStep + " " + yStep);
        for (int i = 0; i < yNodes; i++) {
            for (int j = 0; j < xNodes; j++) {
                nodes[cnt++] = new Node2D(xPos, yPos, nodeType, activationFunction);
//                System.out.println("POS: " + xPos + " " + yPos);
                xPos += xStep;
            }
            xPos = xMin;
            yPos -= yStep;
        }
    }

    public int getNumber() {
        return nodes.length;
    }

    public INode[] getNodes() {
        return nodes;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public Neuron.Activation getNodeActivationFunction() {
        return activationFunction;
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

    public int getNumberOfIntraLayerConnections() {
        return 0;
    }

    public boolean isBiased() {
        return biased;
    }
}
