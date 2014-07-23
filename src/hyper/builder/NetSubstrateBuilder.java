package hyper.builder;

import common.net.INet;
import common.net.linked.Link;
import common.net.linked.Net;
import common.net.linked.Neuron;
import hyper.cppn.ICPPN;
import hyper.substrate.ISubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateIntraLayerConnection;
import hyper.substrate.node.INode;
import hyper.substrate.node.NodeType;

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
public class NetSubstrateBuilder implements IEvaluableSubstrateBuilder {
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

    final private ISubstrate substrate;
    final private IWeightEvaluator weightEvaluator;

    private Map<ISubstrateLayer, NeuronIndices> indices;

    private boolean built = false;
    private Net net;

    public NetSubstrateBuilder(ISubstrate substrate, IWeightEvaluator weightEvaluator) {
        this.substrate = substrate;
        this.weightEvaluator = weightEvaluator;
        throw new IllegalStateException("CORRECT bias layer behavior!");
    }

    public ISubstrate getSubstrate() {
        return substrate;
    }

    public void build(ICPPN aCPPN) {
        //TODO checks
        indices = new HashMap<ISubstrateLayer, NeuronIndices>();
        Map<INode, Neuron> nodeMap = new LinkedHashMap<INode, Neuron>();
        int neuronIdCounter = 0;
        int numInput = 0;
        int numHidden = 0;
        int numOutput = 0;
        int lowerIndex;
        int higherIndex;
        for (ISubstrateLayer layer : substrate.getLayers()) {
            lowerIndex = neuronIdCounter;
            INode[] nodes = layer.getNodes();
            for (INode node : nodes) {
                Neuron newNeuron;
                if (node.getType() == NodeType.INPUT || node.getType() == NodeType.BIAS) {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.INPUT, node.getActivationFunction());
                    numInput++;
                } else if (node.getType() == NodeType.HIDDEN) {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.HIDDEN, node.getActivationFunction());
                    numHidden++;
                } else {
                    newNeuron = new Neuron(neuronIdCounter++, Neuron.Type.OUTPUT, node.getActivationFunction());
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

            for (INode nodeFrom : connection.getFrom().getNodes()) {
                for (INode nodeTo : connection.getTo().getNodes()) {
                    Neuron from = nodeMap.get(nodeFrom);
                    Neuron to = nodeMap.get(nodeTo);
                    //number of incoming links
                    int incomingLinks = connection.getFrom().getNodes().length;
                    //commented out because this code prevented from use of bias neurons
//                    if (incomingLinks == 1) {
//                        throw new IllegalStateException("Incorrect!!! incomingLinks");
//                    }
                    double weight = weightEvaluator.evaluate(aCPPN, aCPPNOutput, nodeFrom, nodeTo, incomingLinks);
                    if (weight == 0.0) {
                        continue;
                    }
//                    System.out.println(this.toString() + ": "  + nodeFrom.getCoordinate() + "--->" + nodeTo.getCoordinate() + " = " + weight);
                    Link newLink = new Link(linkIdCounter++, weight, from, to);
                    net.addLink(newLink);
                }
            }
        }
//        System.out.println(this.toString() + "--------------END CPPN");

        // intra-layer connections
        for (ISubstrateLayer layer : substrate.getLayers()) {
            if (layer.hasIntraLayerConnections()) {
                int aCPPNOutput = substrate.getConnectionCPPNOutput(layer);
                for (SubstrateIntraLayerConnection intraLayerConnection : layer.getIntraLayerConnections()) {
                    Neuron from = nodeMap.get(intraLayerConnection.getFrom());
                    Neuron to = nodeMap.get(intraLayerConnection.getTo());
                    //number of incoming links
                    int incomingLinks = layer.getNumber();
                    double weight = weightEvaluator.evaluate(aCPPN, aCPPNOutput, intraLayerConnection.getFrom(), intraLayerConnection.getTo(), incomingLinks);
                    if (weight == 0.0) {
                        continue;
                    }
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

    public INet getNet() throws IllegalStateException {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return net;
    }

    public int getLowerNeuronIndex(ISubstrateLayer layer) {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return indices.get(layer).getLower();
    }

    public int getUpperNeuronIndex(ISubstrateLayer layer) {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return indices.get(layer).getUpper();
    }
}
