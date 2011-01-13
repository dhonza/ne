package hyper.substrate;

import hyper.substrate.layer.IConnectable;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.ISubstrateLayer;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 1:02:30 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Class has to states: not completed and completed.
 */
public class BasicSubstrate implements ISubstrate {

    private boolean completed = false;

    private Set<ISubstrateLayer> layers = new LinkedHashSet<ISubstrateLayer>();
    private Set<SubstrateInterLayerConnection> connections = new LinkedHashSet<SubstrateInterLayerConnection>();

    private int connectionCounter = 0;
    private Map<IConnectable, Integer> connectionCPPNOutput = new HashMap<IConnectable, Integer>();

    private int maxDimension = 0;

    public void addLayer(ISubstrateLayer layer) throws IllegalStateException {
        if (completed) {
            throw new IllegalStateException("Substrate already completed. Cannot add a layer.");
        }
        layers.add(layer);
        maxDimension = maxDimension < layer.getDimension() ? layer.getDimension() : maxDimension;
        if (layer.hasIntraLayerConnections()) {
            connectionCPPNOutput.put(layer, connectionCounter++);
        }
    }

    public void connect(SubstrateInterLayerConnection substrateLayerConnection) throws IllegalArgumentException, IllegalStateException {
        if (completed) {
            throw new IllegalStateException("Substrate already completed. Cannot connect layers.");
        }
        if (!layers.contains(substrateLayerConnection.getFrom())) {
            throw new IllegalArgumentException("Nonexisting FROM layer.");
        }
        if (!layers.contains(substrateLayerConnection.getTo())) {
            throw new IllegalArgumentException("Nonexisting TO layer.");
        }
        if (connections.contains(substrateLayerConnection)) {
            throw new IllegalArgumentException("Already existing connection.");
        }
        connections.add(substrateLayerConnection);
        connectionCPPNOutput.put(substrateLayerConnection, connectionCounter++);
    }

    /**
     * Complete the substrate. No further modifications are possible.
     * Protects layers and connections which can now be obtained by getLayers and getConnections, making them unmodifiable.
     */
    public void complete() {
        completed = true;
        layers = Collections.unmodifiableSet(layers);
        connections = Collections.unmodifiableSet(connections);
    }

    public Set<ISubstrateLayer> getLayers() throws IllegalStateException {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        return layers;
    }

    public Set<SubstrateInterLayerConnection> getConnections() throws IllegalStateException {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        return connections;
    }

    public int getMaxDimension() throws IllegalStateException {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        return maxDimension;
    }

    public int getNumOfLayerConnections() throws IllegalStateException {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        return connectionCounter;
    }

    public int getNumOfLinks() {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        int sum = 0;
        for (SubstrateInterLayerConnection connection : connections) {
            sum += connection.getNumOfLinks();
        }

        for (ISubstrateLayer layer : layers) {
            if (layer.hasIntraLayerConnections()) {
                sum += layer.getNumberOfIntraLayerConnections();
            }
        }

        return sum;
    }

    public int getConnectionCPPNOutput(IConnectable connectable) {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
        return connectionCPPNOutput.get(connectable);
    }
}
