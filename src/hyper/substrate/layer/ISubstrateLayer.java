package hyper.substrate.layer;

import hyper.substrate.node.Node;
import hyper.substrate.node.NodeType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:32:13 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * A layer has all nodes of a single type (INPUT, OUTPUT, HIDDEN).
 * To combine more layers use the same CPPN output.
 * Implementing class must be immutable.
 */
public interface ISubstrateLayer extends IConnectable, Serializable {
    public int getNumber();

    public Node[] getNodes();

    public boolean hasIntraLayerConnections();

    public int getNumberOfIntraLayerConnections();

    public SubstrateIntraLayerConnection[] getIntraLayerConnections();

    public NodeType getNodeType();

    public int getDimension();

}
