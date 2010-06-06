package hyper.builder;

import hyper.cppn.CPPN;
import hyper.substrate.Substrate;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateIntraLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
import hyper.substrate.node.Node;
import hyper.substrate.node.NodeType;
import neat.Link;
import neat.Net;
import neat.Neuron;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 14, 2009
 * Time: 11:32:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class NEATSubstrateBuilder implements SubstrateBuilder {
    private class NeuronIndices implements Serializable {
        private int lower;
        private int upper;

        private NeuronIndices(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        public int getLower() {
            return lower;
        }

        public int getUpper() {
            return upper;
        }
    }

    final private Substrate substrate;

    private Map<SubstrateLayer, NeuronIndices> indices;

    private boolean built = false;
    private Net net;

    public NEATSubstrateBuilder(Substrate substrate) {
        this.substrate = substrate;
    }

    public Substrate getSubstrate() {
        return substrate;
    }

    public void build(CPPN aCPPN) {
        //TODO checks
        indices = new HashMap<SubstrateLayer, NeuronIndices>();
        Map<Node, Neuron> nodeMap = new LinkedHashMap<Node, Neuron>();
        int neuronIdCounter = 0;
        int numInput = 0;
        int numHidden = 0;
        int numOutput = 0;
        int lowerIndex;
        int higherIndex;
        for (SubstrateLayer layer : substrate.getLayers()) {
            lowerIndex = neuronIdCounter;
            Node[] nodes = layer.getNodes();
            for (Node node : nodes) {
                Neuron newNeuron;
                if (node.getType() == NodeType.INPUT) {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.INPUT, Neuron.Activation.LINEAR);
                    numInput++;
                } else if (node.getType() == NodeType.HIDDEN) {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.HIDDEN, Neuron.Activation.SIGMOID);
                    numHidden++;
                } else {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.OUTPUT, Neuron.Activation.SIGMOID);
                    numOutput++;
                }
                nodeMap.put(node, newNeuron);
            }
            higherIndex = neuronIdCounter;
            indices.put(layer, new NeuronIndices(lowerIndex, higherIndex));
        }

        net = new Net(1, numInput, numHidden, numOutput);
        for (Neuron neuron : nodeMap.values()) {
            if (neuron.getType() == Neuron.Type.INPUT) {
                net.addInput(neuron);
            } else if (neuron.getType() == Neuron.Type.HIDDEN) {
                net.addHidden(neuron);
            } else {
                net.addOutput(neuron);
            }
        }


        // inter-layer connections
        int linkIdCounter = 0;
        for (SubstrateInterLayerConnection connection : substrate.getConnections()) {
            int aCPPNOutput = substrate.getConnectionCPPNOutput(connection);

            for (Node nodeFrom : connection.getFrom().getNodes()) {
                for (Node nodeTo : connection.getTo().getNodes()) {
                    Neuron from = nodeMap.get(nodeFrom);
                    Neuron to = nodeMap.get(nodeTo);
                    double weight = 3.0 * aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
                    Link newLink = new Link(linkIdCounter++, weight, from, to);
                    net.addLink(newLink);
                }
            }
        }

        // intra-layer connections
        for (SubstrateLayer layer : substrate.getLayers()) {
            if (layer.hasIntraLayerConnections()) {
                int aCPPNOutput = substrate.getConnectionCPPNOutput(layer);
                for (SubstrateIntraLayerConnection intraLayerConnection : layer.getIntraLayerConnections()) {
                    Neuron from = nodeMap.get(intraLayerConnection.getFrom());
                    Neuron to = nodeMap.get(intraLayerConnection.getTo());
                    double weight = 3.0 * aCPPN.evaluate(aCPPNOutput, intraLayerConnection.getFrom().getCoordinate(), intraLayerConnection.getTo().getCoordinate());
                    Link newLink = new Link(linkIdCounter++, weight, from, to);
                    net.addLink(newLink);
                }
            }
        }

        /*
        //multiply neuron's bias by the number of incoming connections
        for (Link toBiasedNeuron : net.getInputs().get(0).getOutgoing()) {
            Neuron biasedNeuron = toBiasedNeuron.getOut();
//            System.out.print("setting " + toBiasedNeuron + " from:" + toBiasedNeuron.getWeight());
            toBiasedNeuron.setWeight(toBiasedNeuron.getWeight() * (biasedNeuron.getIncoming().size() - 1));
//            System.out.println(" to " + toBiasedNeuron.getWeight());            
        }

//          */
        built = true;
    }

    public Net getNet() throws IllegalStateException {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return net;
    }

    public int getLowerNeuronIndex(SubstrateLayer layer) {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return indices.get(layer).getLower();
    }

    public int getUpperNeuronIndex(SubstrateLayer layer) {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return indices.get(layer).getUpper();
    }
}
