package hyper.builder;

import common.net.INet;
import common.net.precompiled.IPrecompiledFeedForwardStub;
import common.net.precompiled.PrecompiledFeedForwardNet;
import hyper.cppn.CPPN;
import hyper.substrate.Substrate;
import hyper.substrate.layer.IBias;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
import hyper.substrate.node.Node;
import hyper.substrate.node.NodeType;

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
    private class PreviousLayerConnectionContainer {
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
        generateHeader(src);
        generateInputs(src);
        generateLayers(src);
        generateFooter(src);
        return src.toString();
    }

    private void generateHeader(StringBuilder src) {
        src.append("public class PrecompiledStub implements common.net.precompiled.IPrecompiledFeedForwardStub {\n");
        src.append("public double[] propagate(double b, double[] in, double[] w) {\n");
    }

    private void generateInputs(StringBuilder src) {
        src.append("\tdouble[] p = in;\n");
        src.append("\tdouble[] n;\n");
    }

    private void generateLayers(StringBuilder src) {
        int weightCnt = 0;
        for (int i = 0; i < successiveConnections.size(); i++) {
            SubstrateInterLayerConnection connection = successiveConnections.get(i).connection;
            Node[] tNodes = connection.getTo().getNodes();
            Node[] fNodes = connection.getFrom().getNodes();
            src.append("\tn = new double[").append(tNodes.length).append("];\n");
            for (int t = 0; t < tNodes.length; t++) {
                src.append("\tn[").append(t).append("] = a(");
                if (biasLayer != null) {
                    src.append("w[").append(weightCnt++).append("]*b + ");
                }
                for (int f = 0; f < fNodes.length - 1; f++) {
                    src.append("w[").append(weightCnt++).append("]*p[").append(f).append("] + ");
                }
                src.append("w[").append(weightCnt++).append("]*p[").append(fNodes.length - 1).append("]);\n");
            }
            src.append("\tp = n;\n");
        }
    }

    private void generateFooter(StringBuilder src) {
        src.append("\treturn n;\n}\n\n");
        src.append("public double a(double s) {\n");
        src.append("\treturn 1/(1+Math.exp(-4.924273 * s));\n");
        src.append("}\n");
        src.append("public int getNumberOfInputs() {\n");
        src.append("\treturn ").append(numberOfInputs).append(";\n");
        src.append("}\n}\n");
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
                "-classpath", ".:out/production/Ne",
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
}
