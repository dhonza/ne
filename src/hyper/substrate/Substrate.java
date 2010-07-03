package hyper.substrate;

import hyper.substrate.layer.Connectable;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:26:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Substrate extends Serializable {
    public void addLayer(final SubstrateLayer layer);

    /**
     * @param substrateLayerConnection
     * @throws IllegalArgumentException when from or to are not added also when duplicate connection
     */
    public void connect(final SubstrateInterLayerConnection substrateLayerConnection) throws IllegalArgumentException;

    public Set<SubstrateLayer> getLayers();

    public Set<SubstrateInterLayerConnection> getConnections();

    public int getMaxDimension();

    public int getNumOfLayerConnections();

    public int getNumOfLinks();

    public int getConnectionCPPNOutput(Connectable connectable);
}
