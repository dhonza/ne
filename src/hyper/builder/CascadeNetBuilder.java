package hyper.builder;

import common.net.INet;
import common.net.cascade.ActivationFunctionSigmoid;
import common.net.cascade.NeuralNetwork;
import hyper.cppn.CPPN;
import hyper.substrate.Substrate;
import hyper.substrate.layer.IBias;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
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
public class CascadeNetBuilder implements EvaluableSubstrateBuilder {
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

    final private Substrate substrate;
    final private WeightEvaluator weightEvaluator;

    private SubstrateLayer inputLayer = null;
    private SubstrateLayer biasLayer = null;
    private List<PreviousLayerConnectionContainer> successiveConnections = null;
    private int[] layers;
    private double[] weights;

    private int numberOfInputs;

    private boolean built = false;

    private NeuralNetwork net;

    public CascadeNetBuilder(Substrate substrate, WeightEvaluator weightEvaluator) {
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
        Set<SubstrateLayer> layers = substrate.getLayers();
        inputLayer = null;
        biasLayer = null;
        boolean foundInput = false;
        boolean foundBias = false;
        for (SubstrateLayer layer : layers) {
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
        Map<SubstrateLayer, SubstrateInterLayerConnection> layerConnectionMap = new HashMap<SubstrateLayer, SubstrateInterLayerConnection>();
        Map<SubstrateLayer, SubstrateInterLayerConnection> biasConnectionMap = new HashMap<SubstrateLayer, SubstrateInterLayerConnection>();
        for (SubstrateInterLayerConnection layerConnection : layerConnections) {
            //ignore all bias connections, build only fully connected neural networks with all
            //non-input layers biased
            if (layerConnection.getFrom() != biasLayer) {
                layerConnectionMap.put(layerConnection.getFrom(), layerConnection);
            } else {
                biasConnectionMap.put(layerConnection.getTo(), layerConnection);
            }
        }
        SubstrateLayer currentLayer = inputLayer;
        successiveConnections = new ArrayList<PreviousLayerConnectionContainer>();
        for (int i = 0; i < layerConnectionMap.size(); i++) {
            SubstrateLayer nextLayer = layerConnectionMap.get(currentLayer).getTo();
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

    public Substrate getSubstrate() {
        return substrate;
    }

    public void build(CPPN aCPPN) {
        //TODO same code as in  PrecompiledFeedForward
        int cnt = 0;
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            for (Node nodeTo : successiveConnection.connection.getTo().getNodes()) {
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
