package hyper.substrate;

import com.google.common.collect.ImmutableSet;
import hyper.substrate.layer.IConnectable;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.INode;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:26:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISubstrate extends Serializable {
    INode getBiasNode();

    void addLayer(final ISubstrateLayer layer);

    /**
     * @param substrateLayerConnection
     * @throws IllegalArgumentException when from or to are not added also when duplicate connection
     */
    void connect(final SubstrateInterLayerConnection substrateLayerConnection) throws IllegalArgumentException;

    //note, that insertion order is preserved
    ImmutableSet<ISubstrateLayer> getLayers();

    //note, that insertion order is preserved
    ImmutableSet<SubstrateInterLayerConnection> getConnections();

    int getMaxDimension();

    int getNumOfLayerConnections();

    int getNumOfLinks();

    int getConnectionCPPNOutput(IConnectable connectable);

    int getBiasCPPNOutput(ISubstrateLayer layer);
}
