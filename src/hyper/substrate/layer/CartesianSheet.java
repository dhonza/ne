package hyper.substrate.layer;

import common.net.linked.Neuron;
import hyper.substrate.node.Node;
import hyper.substrate.node.Node2D;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 7:23:13 PM
 * This class is based on Brian Wooley's CartesianSheet class to exactly mimic the ordering and positions of nodes.
 * It works almost the same as MeshLayer2D
 */

/**
 * This class is immutable.
 */
public class CartesianSheet implements ISubstrateLayer {
    final private NodeType nodeType;

    private final Node[] nodes;

    public final int xSize, ySize;

    private final double xInterval, yInterval;

    public CartesianSheet(int dimX, int dimY, NodeType nodeType) {
        this(dimX, dimY, nodeType, Neuron.Activation.SIGMOID);
    }

    public CartesianSheet(int dimX, int dimY, NodeType nodeType, Neuron.Activation activationFunction) {
        this.nodeType = nodeType;
        assert (dimX > 0);
        xSize = dimX;
        assert (dimY > 0);
        ySize = dimY;

        xInterval = 2.0 / (xSize + 1);
        yInterval = 2.0 / (ySize + 1);

        double x, y;
        this.nodes = new Node[dimX * dimY];
        int cnt = 0;
//        System.out.println("--------------------" + xSize + " " + ySize + " " + xInterval + " " + yInterval);
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                x = (i * xInterval) - 1.0 + xInterval;
                y = (j * yInterval) - 1.0 + yInterval;
                nodes[cnt++] = new Node2D(x, y, nodeType, activationFunction);
//                System.out.println("POS: " + x + " " + y);
            }
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

    public int getNumberOfIntraLayerConnections() {
        return 0;
    }
}
