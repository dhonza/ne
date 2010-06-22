package hyper.builder.precompiled;

import common.net.INet;
import common.net.precompiled.IPrecompiledFeedForwardStub;
import common.net.precompiled.PrecompiledFeedForwardNet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.substrate.Substrate;
import hyper.substrate.layer.IBias;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
import hyper.substrate.node.Node;
import hyper.substrate.node.NodeType;
import org.apache.commons.lang.ArrayUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 8:16:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrecompiledFeedForwardSubstrateBuilder implements EvaluableSubstrateBuilder {
    class PreviousLayerConnectionContainer {
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

    private SubstrateLayer inputLayer = null;
    private SubstrateLayer biasLayer = null;
    private List<PreviousLayerConnectionContainer> successiveConnections = null;

    private IPrecompiledFeedForwardStub stub;
    private double[] weights;
    private int numberOfInputs;

    private boolean built = false;

    public PrecompiledFeedForwardSubstrateBuilder(Substrate substrate) {
        this.substrate = substrate;
        prepare();
    }

    private void prepare() {
        findBiasAndInputLayers();
        createListOfSuccessiveLayers();
        prepareWeightVector();
        String sourceCode = generateSourceCode();
//        System.out.println(sourceCode);
        compile(sourceCode);
        loadStubClass();
    }

    private void findBiasAndInputLayers() {
        Set<SubstrateLayer> layers = substrate.getLayers();
        inputLayer = null;
        biasLayer = null;
        boolean foundInput = false;
        boolean foundBias = false;
        for (SubstrateLayer layer : layers) {
            if (layer.hasIntraLayerConnections()) {
                throw new IllegalStateException("No intralayer connections allowed for this substrate builder!");
            }
            if (layer instanceof IBias) {
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

    private void prepareWeightVector() {
        int weightCount = 0;
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            //bias
            if (successiveConnection.bias != null) {
                weightCount += successiveConnection.bias.getTo().getNodes().length;
            }
            //all connections to neuron
            weightCount += successiveConnection.connection.getFrom().getNodes().length *
                    successiveConnection.connection.getTo().getNodes().length;
        }
        weights = new double[weightCount];
    }

    private String generateSourceCode() {
        StringBuilder src = new StringBuilder();
//        NoCyclesGenerator generator = new NoCyclesGenerator(successiveConnections, numberOfInputs, biasLayer);
        NeuronsByCycleGenerator generator = new NeuronsByCycleGenerator(successiveConnections, numberOfInputs, biasLayer);
        generator.generateHeader(src);
        generator.generateLayers(src);
        generator.generateHelperMethods(src);
        generator.generateFooter(src);
        return src.toString();
    }



    private void compile(String sourceCode) {
        try {
            BufferedWriter fs = new BufferedWriter(new FileWriter("PrecompiledStub.java"));
            fs.write(sourceCode);
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int ret = compiler.run(null, System.out, System.err,
                "-classpath", ".:out/production/Ne:build",
                "PrecompiledStub.java"
        );
        if (ret != 0) {
            throw new IllegalStateException("Unable to compile!");
        }
    }

    private void loadStubClass() {
        try {
            URL url = new File(".").toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader loader = new URLClassLoader(urls);
            Class cls = loader.loadClass("PrecompiledStub");
            stub = (IPrecompiledFeedForwardStub) cls.newInstance();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Substrate getSubstrate() {
        return substrate;
    }

    public void build(CPPN aCPPN) {
        int cnt = 0;
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            for (Node nodeTo : successiveConnection.connection.getTo().getNodes()) {
                //bias
                if (successiveConnection.bias != null) {
                    int aCPPNOutput = substrate.getConnectionCPPNOutput(successiveConnection.bias);
                    weights[cnt++] = 3.0 * aCPPN.evaluate(aCPPNOutput, successiveConnection.bias.getFrom().getNodes()[0].getCoordinate(),
                            nodeTo.getCoordinate());
                }
                //all connections to neuron
                int aCPPNOutput = substrate.getConnectionCPPNOutput(successiveConnection.connection);
                for (Node nodeFrom : successiveConnection.connection.getFrom().getNodes()) {
                    weights[cnt++] = 3.0 * aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
                }
            }
        }
        built = true;
    }

    public INet getNet() throws IllegalStateException {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return new PrecompiledFeedForwardNet(stub, weights.clone());
    }

    @Override
    public String toString() {
        return "WEIGHTS: " + ArrayUtils.toString(weights);
    }
}
