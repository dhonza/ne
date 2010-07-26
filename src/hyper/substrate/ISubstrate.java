package hyper.substrate;

import hyper.substrate.layer.IConnectable;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:26:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISubstrate extends Serializable {
    public void addLayer(final ISubstrateLayer layer);

    /**
     * @param substrateLayerConnection
     * @throws IllegalArgumentException when from or to are not added also when duplicate connection
     */
    public void connect(final SubstrateInterLayerConnection substrateLayerConnection) throws IllegalArgumentException;

    public Set<ISubstrateLayer> getLayers();

    public Set<SubstrateInterLayerConnection> getConnections();

    public int getMaxDimension();

    public int getNumOfLayerConnections();

    public int getNumOfLinks();

    public int getConnectionCPPNOutput(IConnectable connectable);
}
