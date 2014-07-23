package hyper.substrate;

import com.google.common.collect.ImmutableSet;
import hyper.substrate.layer.IConnectable;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.INode;
import hyper.substrate.node.NodeType;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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


    private final INode biasNode;
    private ImmutableSet<ISubstrateLayer> layers = null;
    private LinkedHashSet<ISubstrateLayer> layersBuilder = new LinkedHashSet<>();
    private ImmutableSet<SubstrateInterLayerConnection> connections = null;
    private LinkedHashSet<SubstrateInterLayerConnection> connectionsBuilder = new LinkedHashSet<>();

    private int connectionCounter = 0;
    private Map<IConnectable, Integer> connectionCPPNOutput = new HashMap<>();
    private Map<ISubstrateLayer, Integer> biasCPPNOutput = new HashMap<>();

    private int maxDimension;

    public BasicSubstrate(INode biasNode) {
        this.biasNode = biasNode;
        maxDimension = biasNode.getCoordinate().getDimension();
    }

    public INode getBiasNode() {
        return biasNode;
    }

    public void addLayer(ISubstrateLayer layer) throws IllegalStateException {
        if (completed) {
            throw new IllegalStateException("Substrate already completed. Cannot add a layer.");
        }
        if (layer.isBiased() && layer.getNodeType() == NodeType.INPUT) {
            throw new IllegalStateException("Can't bias input layer!");
        }

        layersBuilder.add(layer);
        maxDimension = maxDimension < layer.getDimension() ? layer.getDimension() : maxDimension;

        if (layer.isBiased()) {
            biasCPPNOutput.put(layer, connectionCounter++);
        }
        if (layer.hasIntraLayerConnections()) {
            connectionCPPNOutput.put(layer, connectionCounter++);
        }
    }

    public void connect(SubstrateInterLayerConnection substrateLayerConnection) throws IllegalArgumentException, IllegalStateException {
        if (completed) {
            throw new IllegalStateException("Substrate already completed. Cannot connect layers.");
        }
        if (!layersBuilder.contains(substrateLayerConnection.getFrom())) {
            throw new IllegalArgumentException("Nonexisting FROM layer.");
        }
        if (!layersBuilder.contains(substrateLayerConnection.getTo())) {
            throw new IllegalArgumentException("Nonexisting TO layer.");
        }
        if (connectionsBuilder.contains(substrateLayerConnection)) {
            throw new IllegalArgumentException("Already existing connection.");
        }
        connectionsBuilder.add(substrateLayerConnection);
        connectionCPPNOutput.put(substrateLayerConnection, connectionCounter++);
    }

    /**
     * Complete the substrate. No further modifications are possible.
     * Protects layers and connections which can now be obtained by getLayers and getConnections, making them unmodifiable.
     */
    public void complete() {
        completed = true;
        layers = ImmutableSet.copyOf(layersBuilder);
        connections = ImmutableSet.copyOf(connectionsBuilder);
    }

    public ImmutableSet<ISubstrateLayer> getLayers() throws IllegalStateException {
        checkCompletion();
        return layers;
    }

    public ImmutableSet<SubstrateInterLayerConnection> getConnections() throws IllegalStateException {
        checkCompletion();
        return connections;
    }

    public int getMaxDimension() throws IllegalStateException {
        checkCompletion();
        return maxDimension;
    }

    public int getNumOfLayerConnections() throws IllegalStateException {
        checkCompletion();
        return connectionCounter;
    }

    public int getNumOfLinks() {
        checkCompletion();

        int sum = 0;
        for (SubstrateInterLayerConnection connection : connections) {
            sum += connection.getNumOfLinks();
        }

        for (ISubstrateLayer layer : layers) {
            if (layer.isBiased()) {
                sum += layer.getNumber();
            }
            if (layer.hasIntraLayerConnections()) {
                sum += layer.getNumberOfIntraLayerConnections();
            }
        }

        return sum;
    }

    public int getConnectionCPPNOutput(IConnectable connectable) {
        checkCompletion();

        return connectionCPPNOutput.get(connectable);
    }

    public int getBiasCPPNOutput(ISubstrateLayer layer) {
        checkCompletion();
        return biasCPPNOutput.get(layer);
    }

    private void checkCompletion() {
        if (!completed) {
            throw new IllegalStateException("Substrate not completed.");
        }
    }
}
