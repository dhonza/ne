package hyper.builder;

import common.net.INet;
import common.net.cascade.ActivationFunctionSigmoid;
import common.net.cascade.NeuralNetwork;
import common.net.linked.Neuron;
import hyper.cppn.ICPPN;
import hyper.substrate.ISubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node;
import hyper.substrate.node.NodeType;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 3, 2010
 * Time: 11:41:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class CascadeNetBuilder implements IEvaluableSubstrateBuilder {
    class PreviousLayerConnectionContainer {
        //TODO same code as in  PrecompiledFeedForward
        // bias connection to given layer
        public SubstrateInterLayerConnection bias;
        // connection from previous layer to given layer
        public SubstrateInterLayerConnection connection;

        private PreviousLayerConnectionContainer(SubstrateInterLayerConnection bias, SubstrateInterLayerConnection connection) {
            this.bias = bias;
            this.connection = connection;
        }
    }

    final private ISubstrate substrate;
    final private IWeightEvaluator weightEvaluator;

    private ISubstrateLayer inputLayer = null;
    private ISubstrateLayer biasLayer = null;
    private List<PreviousLayerConnectionContainer> successiveConnections = null;
    private int[] layers;
    private double[] weights;

    private int numberOfInputs;

    private boolean built = false;

    private NeuralNetwork net;

    public CascadeNetBuilder(ISubstrate substrate, IWeightEvaluator weightEvaluator) {
        this.substrate = substrate;
        this.weightEvaluator = weightEvaluator;
        prepare();
    }

    private void prepare() {
        findBiasAndInputLayers();
        createListOfSuccessiveLayers();
//        prepareWeightVector();
        prepareNetAndLayers();
    }

    private void findBiasAndInputLayers() {
        //TODO same code as in  PrecompiledFeedForward
        Set<ISubstrateLayer> layers = substrate.getLayers();
        inputLayer = null;
        biasLayer = null;
        boolean foundInput = false;
        boolean foundBias = false;
        for (ISubstrateLayer layer : layers) {
            if (layer.hasIntraLayerConnections()) {
                throw new IllegalStateException("No intralayer connections allowed for this substrate builder!");
            }
            if (layer.getNodeType() == NodeType.BIAS) {
                if (foundBias) {
                    throw new IllegalStateException("Found more than one bias substrate layer!");
                }
                foundBias = true;
                biasLayer = layer;
            } else if (layer.getNodeType() == NodeType.INPUT) {
                if (foundInput) {
                    throw new IllegalStateException("Found more than one input substrate layer!");
                }
                foundInput = true;
                inputLayer = layer;
            }
        }
        if (!foundBias) {
            System.out.println("WARNING: None bias substrate layer found!");
        }
        if (!foundInput) {
            throw new IllegalStateException("None input substrate layer found!");
        }
        numberOfInputs = inputLayer.getNumber();
    }

    private void createListOfSuccessiveLayers() {
        //TODO same code as in  PrecompiledFeedForward
        Set<SubstrateInterLayerConnection> layerConnections = substrate.getConnections();
        Map<ISubstrateLayer, SubstrateInterLayerConnection> layerConnectionMap = new HashMap<ISubstrateLayer, SubstrateInterLayerConnection>();
        Map<ISubstrateLayer, SubstrateInterLayerConnection> biasConnectionMap = new HashMap<ISubstrateLayer, SubstrateInterLayerConnection>();
        for (SubstrateInterLayerConnection layerConnection : layerConnections) {
            //ignore all bias connections, build only fully connected neural networks with all
            //non-input layers biased
            if (layerConnection.getFrom() != biasLayer) {
                layerConnectionMap.put(layerConnection.getFrom(), layerConnection);
            } else {
                biasConnectionMap.put(layerConnection.getTo(), layerConnection);
            }
        }
        ISubstrateLayer currentLayer = inputLayer;
        successiveConnections = new ArrayList<PreviousLayerConnectionContainer>();
        for (int i = 0; i < layerConnectionMap.size(); i++) {
            ISubstrateLayer nextLayer = layerConnectionMap.get(currentLayer).getTo();
            successiveConnections.add(new PreviousLayerConnectionContainer(biasConnectionMap.get(nextLayer), layerConnectionMap.get(currentLayer)));
            currentLayer = nextLayer;
        }
    }

    private void prepareNetAndLayers() {
        int numOfLayers;
        if (biasLayer == null) {
            numOfLayers = substrate.getLayers().size();
        } else {
            numOfLayers = substrate.getLayers().size() - 1;
        }
        layers = new int[numOfLayers];
        layers[0] = inputLayer.getNumber();
        for (int i = 0; i < successiveConnections.size(); i++) {
            layers[i + 1] = successiveConnections.get(i).connection.getTo().getNumber();
        }

        try {
            NeuralNetwork net = new NeuralNetwork(layers, biasLayer != null, new ActivationFunctionSigmoid());
            weights = new double[net.getNumLinks()];
        } catch (Exception e) {
            throw new IllegalStateException("Problems with Cascade Net creation.");
        }
    }

    public ISubstrate getSubstrate() {
        return substrate;
    }

    public void build(ICPPN aCPPN) {
        //TODO same code as in  PrecompiledFeedForward
        int cnt = 0;
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            for (Node nodeTo : successiveConnection.connection.getTo().getNodes()) {
                if (nodeTo.getActivationFunction() != Neuron.Activation.SIGMOID) {
                    throw new IllegalStateException("Only Neuron.Activation.SIGMOID supported by cascade networks!");
                }
                //number of incoming links
                int incomingLinks = successiveConnection.connection.getFrom().getNodes().length;
                //bias
                if (successiveConnection.bias != null) {
                    int aCPPNOutput = substrate.getConnectionCPPNOutput(successiveConnection.bias);
                    weights[cnt++] = weightEvaluator.evaluate(aCPPN, aCPPNOutput, successiveConnection.bias.getFrom().getNodes()[0],
                            nodeTo, incomingLinks);
                }
                //all connections to neuron
                int aCPPNOutput = substrate.getConnectionCPPNOutput(successiveConnection.connection);
                for (Node nodeFrom : successiveConnection.connection.getFrom().getNodes()) {
                    weights[cnt++] = weightEvaluator.evaluate(aCPPN, aCPPNOutput, nodeFrom, nodeTo, incomingLinks);
                }
            }
        }

        built = true;
    }

    public INet getNet() {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }

        try {
            net = new NeuralNetwork(layers, biasLayer != null, new ActivationFunctionSigmoid());
            net.setSynapseWeights(weights);
        } catch (Exception e) {
            throw new IllegalStateException("Problems with Cascade Net creation.");
        }

        return net;
    }

}
