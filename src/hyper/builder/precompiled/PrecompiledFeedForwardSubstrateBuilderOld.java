package hyper.builder.precompiled;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import common.net.INet;
import common.net.precompiled.IPrecompiledFeedForwardStub;
import common.net.precompiled.PrecompiledFeedForwardNet;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.IWeightEvaluator;
import hyper.cppn.ICPPN;
import hyper.substrate.ISubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.INode;
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
public class PrecompiledFeedForwardSubstrateBuilderOld implements IEvaluableSubstrateBuilder {
    final private ISubstrate substrate;
    final private IWeightEvaluator weightEvaluator;

    private ImmutableList<ISubstrateLayer> inputLayers = null;
    private ISubstrateLayer biasLayer = null;
    private ImmutableList<PreviousLayerConnectionContainer> successiveConnections = null;

    private IPrecompiledFeedForwardStub stub;
    private double[] weights;
    private int weightCount;
    private int numberOfInputs;

    private boolean built = false;

    public PrecompiledFeedForwardSubstrateBuilderOld(ISubstrate substrate, IWeightEvaluator weightEvaluator) {
        this.substrate = substrate;
        this.weightEvaluator = weightEvaluator;
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
        Set<ISubstrateLayer> layers = substrate.getLayers();
        ImmutableList.Builder<ISubstrateLayer> inputLayersBuilder = ImmutableList.builder();

        biasLayer = null;
        boolean foundBias = false;
        for (ISubstrateLayer layer : layers) {
            if (layer.hasIntraLayerConnections()) {
                throw new IllegalStateException("No intralayer connections allowed for this substrate builder!");
            }
            if (layer.getNodeType() == NodeType.BIAS) {
                if (foundBias) {
                    throw new IllegalStateException("Bias layers are not supported!");
                }
                foundBias = true;
                biasLayer = layer;
            } else if (layer.getNodeType() == NodeType.INPUT) {
                inputLayersBuilder.add(layer);
                numberOfInputs += layer.getNumber();
            }
        }
        inputLayers = inputLayersBuilder.build();

        if (!foundBias) {
            System.out.println("WARNING: None bias substrate layer found!");
        }
        if (inputLayers.size() == 0) {
            throw new IllegalStateException("None input substrate layer found!");
        }
    }

    private void createListOfSuccessiveLayers() {
        Set<SubstrateInterLayerConnection> layerConnections = substrate.getConnections();
        ArrayListMultimap<ISubstrateLayer, SubstrateInterLayerConnection> layerConnectionMap = ArrayListMultimap.create();
        Map<ISubstrateLayer, SubstrateInterLayerConnection> biasConnectionMap = new HashMap<>();
        for (SubstrateInterLayerConnection layerConnection : layerConnections) {
            //ignore all bias connections, build only fully connected neural networks with all
            //non-input layers biased
            if (layerConnection.getFrom() != biasLayer) {
                layerConnectionMap.put(layerConnection.getFrom(), layerConnection);
            } else {
                biasConnectionMap.put(layerConnection.getTo(), layerConnection);
            }
        }

        ImmutableList.Builder<PreviousLayerConnectionContainer> successiveConnectionsBuilder = ImmutableList.builder();
        Queue<ISubstrateLayer> layerQueue = Queues.newArrayDeque(inputLayers);

        while (!layerQueue.isEmpty()) {
            ISubstrateLayer currentLayer = layerQueue.remove();
            System.out.println("PrecompiledFeedForwardSubstrateBuilder: current layer type: " + currentLayer.getNodeType() + " size: " + currentLayer.getNumber());
            List<SubstrateInterLayerConnection> next = layerConnectionMap.get(currentLayer);
            for (SubstrateInterLayerConnection connection : next) {
                ISubstrateLayer nextLayer = connection.getTo();
                successiveConnectionsBuilder.add(new PreviousLayerConnectionContainer(biasConnectionMap.get(nextLayer), connection));
                layerQueue.add(nextLayer);
            }
        }
        successiveConnections = successiveConnectionsBuilder.build();
    }

    private void prepareWeightVector() {
        weightCount = 0;
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            //bias
            if (successiveConnection.bias != null) {
                weightCount += successiveConnection.bias.getTo().getNodes().length;
            }
            //all connections to neuron
            weightCount += successiveConnection.connection.getFrom().getNodes().length *
                    successiveConnection.connection.getTo().getNodes().length;
        }
//        weights = new double[weightCount];
        System.out.println("PrecompiledFeedForwardSubstrateBuilder: # of weights: " + weightCount);
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
                "-classpath", ".:out/production/Ne:build:ne.jar",
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

    public ISubstrate getSubstrate() {
        return substrate;
    }

    public void build(ICPPN aCPPN) {
        int cnt = 0;
        weights = new double[weightCount];
        for (PreviousLayerConnectionContainer successiveConnection : successiveConnections) {
            for (INode nodeTo : successiveConnection.connection.getTo().getNodes()) {
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
                for (INode nodeFrom : successiveConnection.connection.getFrom().getNodes()) {
                    weights[cnt++] = weightEvaluator.evaluate(aCPPN, aCPPNOutput, nodeFrom, nodeTo, incomingLinks);
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
