package hyper.builder.precompiled;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
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
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 8:16:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrecompiledFeedForwardSubstrateBuilder implements IEvaluableSubstrateBuilder {
    final private ISubstrate substrate;
    final private IWeightEvaluator weightEvaluator;

    private ImmutableList<ISubstrateLayer> inputLayers = null;
    private ImmutableList<ISubstrateLayer> outputLayers = null;
    private ImmutableMultimap<ISubstrateLayer, ISubstrateLayer> layerInputs = null;

    //offsets in input vector
    private ImmutableMap<ISubstrateLayer, Integer> inputOffsets = null;

    //offsets in weight array
    private ImmutableMap<ISubstrateLayer, Integer> biasOffsets = null;
    private ImmutableMap<SubstrateInterLayerConnection, Integer> weightOffsets = null;

    private IPrecompiledFeedForwardStub stub;

    private int weightCount;
    private double[] weights;

    private boolean built = false;

    public PrecompiledFeedForwardSubstrateBuilder(ISubstrate substrate, IWeightEvaluator weightEvaluator) {
        this.substrate = substrate;
        this.weightEvaluator = weightEvaluator;
        prepare();
    }

    private void prepare() {
        findLayerTypes();
        createListOfSuccessiveLayers();
        prepareWeightVector();
        String sourceCode = generateSourceCode();
//        System.out.println(sourceCode);
        compile(sourceCode);
        loadStubClass();
    }

    private void findLayerTypes() {
        Set<ISubstrateLayer> layers = substrate.getLayers();
        ImmutableList.Builder<ISubstrateLayer> inputLayersBuilder = ImmutableList.builder();
        ImmutableList.Builder<ISubstrateLayer> outputLayersBuilder = ImmutableList.builder();
        ImmutableMap.Builder<ISubstrateLayer, Integer> inputOffsetsBuilder = ImmutableMap.builder();

        int inputOffset = 0;
        for (ISubstrateLayer layer : layers) {
            if (layer.hasIntraLayerConnections()) {
                throw new IllegalStateException("No intralayer connections allowed for this substrate builder!");
            }
            if (layer.getNodeType() == NodeType.BIAS) {
                throw new IllegalStateException("No bias layers allowed!");
            } else if (layer.getNodeType() == NodeType.INPUT) {
                inputLayersBuilder.add(layer);
                inputOffsetsBuilder.put(layer, inputOffset);
                inputOffset += layer.getNumber();
            } else if (layer.getNodeType() == NodeType.OUTPUT) {
                outputLayersBuilder.add(layer);
            }
        }
        inputLayers = inputLayersBuilder.build();
        outputLayers = outputLayersBuilder.build();
        inputOffsets = inputOffsetsBuilder.build();

        if (inputLayers.size() == 0) {
            System.out.println("WARNING: none input substrate layer found!");
        }

        if (outputLayers.size() == 0) {
            throw new IllegalStateException("None output substrate layer found!");
        }
    }

    private void createListOfSuccessiveLayers() {
        Set<SubstrateInterLayerConnection> layerConnections = substrate.getConnections();

        ImmutableMultimap.Builder<ISubstrateLayer, ISubstrateLayer> layerInputsBuilder = ImmutableMultimap.builder();

        for (SubstrateInterLayerConnection layerConnection : layerConnections) {
            layerInputsBuilder.put(layerConnection.getTo(), layerConnection.getFrom());
        }

        layerInputs = layerInputsBuilder.build();
    }

    private void prepareWeightVector() {
        ImmutableMap.Builder<ISubstrateLayer, Integer> biasOffsetBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<SubstrateInterLayerConnection, Integer> weightOffsetBuilder = ImmutableMap.builder();

        weightCount = 0;

        //biases stored at start of the weight array in order in which layers were added
        for (ISubstrateLayer layer : substrate.getLayers()) {
            if (layer.isBiased()) {
                biasOffsetBuilder.put(layer, weightCount);
                weightCount += layer.getNumber();
            }
        }

        //inter-layer connections stored after biases in order in which connections were added
        for (SubstrateInterLayerConnection connection : substrate.getConnections()) {
            weightOffsetBuilder.put(connection, weightCount);
            weightCount += connection.getFrom().getNumber() * connection.getTo().getNumber();
        }

        biasOffsets = biasOffsetBuilder.build();
        weightOffsets = weightOffsetBuilder.build();
        System.out.println("PrecompiledFeedForwardSubstrateBuilder: #weights: " + weightCount);
    }


    private String generateSourceCode() {
        StringBuilder src = new StringBuilder();
        DirectedAcyclicGraphGenerator generator = new DirectedAcyclicGraphGenerator(
                substrate.getLayers(),
                inputLayers,
                outputLayers,
                layerInputs,
                biasOffsets,
                weightOffsets,
                inputOffsets
        );
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

        //biases stored at start of the weight array in order in which layers were added
        for (ISubstrateLayer layer : substrate.getLayers()) {
            if (layer.isBiased()) {
                INode nodeFrom = substrate.getBiasNode();
                for (INode nodeTo : layer.getNodes()) {
                    int aCPPNOutput = substrate.getBiasCPPNOutput(layer);
                    weights[cnt++] = weightEvaluator.evaluate(aCPPN, aCPPNOutput, nodeFrom, nodeTo, 1);
                }
                layer.getNumber();
            }
        }

        //inter-layer connections stored after biases in order in which connections were added
        for (SubstrateInterLayerConnection connection : substrate.getConnections()) {
            int incomingLinks = connection.getFrom().getNumber();
            int aCPPNOutput = substrate.getConnectionCPPNOutput(connection);
            for (INode nodeTo : connection.getTo().getNodes()) {
                for (INode nodeFrom : connection.getFrom().getNodes()) {
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
