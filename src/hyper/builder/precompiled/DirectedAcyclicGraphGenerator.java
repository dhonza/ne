package hyper.builder.precompiled;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import common.net.linked.Neuron;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2010
 * Time: 10:13:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectedAcyclicGraphGenerator {
    private final ImmutableSet<ISubstrateLayer> layers;
    private final ImmutableList<ISubstrateLayer> inputLayers;
    private final ImmutableList<ISubstrateLayer> outputLayers;
    private final ImmutableMultimap<ISubstrateLayer, ISubstrateLayer> layerInputs;
    ImmutableMap<ISubstrateLayer, Integer> biasOffsets;
    ImmutableMap<SubstrateInterLayerConnection, Integer> weightOffsets;
    ImmutableMap<ISubstrateLayer, Integer> inputOffsets;

    private ImmutableMap<ISubstrateLayer, Integer> layerOrderMap = null;

    private int numberOfInputs;
    private int numberOfOutputs;

    public DirectedAcyclicGraphGenerator(
            ImmutableSet<ISubstrateLayer> layers,
            ImmutableList<ISubstrateLayer> inputLayers,
            ImmutableList<ISubstrateLayer> outputLayers,
            ImmutableMultimap<ISubstrateLayer, ISubstrateLayer> layerInputs,
            ImmutableMap<ISubstrateLayer, Integer> biasOffsets,
            ImmutableMap<SubstrateInterLayerConnection, Integer> weightOffsets,
            ImmutableMap<ISubstrateLayer, Integer> inputOffsets) {
        this.layers = layers;
        this.inputLayers = inputLayers;
        this.outputLayers = outputLayers;
        this.layerInputs = layerInputs;
        this.biasOffsets = biasOffsets;
        this.weightOffsets = weightOffsets;
        this.inputOffsets = inputOffsets;

        for (ISubstrateLayer inputLayer : inputLayers) {
            numberOfInputs += inputLayer.getNumber();
        }
        for (ISubstrateLayer outputLayer : outputLayers) {
            numberOfOutputs += outputLayer.getNumber();
        }

        int i = 0;
        ImmutableMap.Builder<ISubstrateLayer, Integer> layerOrderMapBuilder = ImmutableMap.builder();
        for (ISubstrateLayer layer : layers) {
            layerOrderMapBuilder.put(layer, i++);
        }
        layerOrderMap = layerOrderMapBuilder.build();
    }

    protected void generateHeader(StringBuilder src) {
        src.append("import java.util.Arrays;\n\n");
        src.append("public class PrecompiledStub implements common.net.precompiled.IPrecompiledFeedForwardStub {\n");
        src.append("\t double[] w; //weights\n");
        src.append("\t double[][] a; //neuron activities for each layer\n");
        src.append("\t double[] in; //input vector\n");
        src.append("\t double[] out; //output vector\n");
        src.append("\t boolean[] e; //layer evaluated flag\n\n");

        src.append("public PrecompiledStub() {\n");
        src.append("\ta = new double[" + layers.size() + "][];\n");
        int i = 0;
        for (ISubstrateLayer layer : layers) {
            if (layer.getNodeType() == NodeType.INPUT && inputLayers.size() == 1) {
                i++;
                continue;
            }
            src.append("\ta[" + i + "] = new double[" + layer.getNumber() + "];\n");
            i++;
        }
        src.append("\tout = new double[" + numberOfOutputs + "];\n");
        src.append("\te = new boolean[" + layers.size() + "];\n");
        src.append("}\n\n");

        src.append("public double[] propagate(double b, double[] in, double[] w) { //ignoring b value (always 1)\n");
        src.append("\tthis.w = w;\n");
        src.append("\tthis.in = in;\n");
        src.append("\tArrays.fill(e, false); //no layer evaluated\n");

        if (outputLayers.size() == 1) {
            src.append("\tout = layer" + layerOrderMap.get(outputLayers.get(0)) + "();\n");
        } else {
            int off = 0;
            for (ISubstrateLayer outputLayer : outputLayers) {
                int layerId = layerOrderMap.get(outputLayer);
                int size = outputLayer.getNumber();
                src.append("\tSystem.arraycopy(layer" + layerId + "(), 0, out, " + off + ", " + size + ");\n");
                off += size;
            }
        }
        src.append("\treturn out;\n");
        src.append("}\n\n");
    }

    protected void generateLayers(StringBuilder src) {
        for (ISubstrateLayer layer : layers) {
            generateLayer(src, layer);
        }
    }

    protected void generateLayer(StringBuilder src, ISubstrateLayer layer) {
        int layerId = layerOrderMap.get(layer);
        int layerSize = layer.getNumber();
        src.append("public double[] layer" + layerId + "() { //" + getLayerInfo(layer) + "\n");
        src.append("\tif(e[" + layerId + "]) return a[" + layerId + "]; else e[" + layerId + "] = true; //layer already evaluated\n\n");
        if (layer.getNodeType() == NodeType.INPUT) {
            if (inputLayers.size() == 1) {
                src.append("\ta[" + layerId + "] = in;\n");
            } else {
                int off = inputOffsets.get(layer);
                src.append("\tSystem.arraycopy(in, " + off + ", a[" + layerId + "], 0, " + layerSize + ");\n");
            }
            src.append("\treturn a[" + layerId + "];\n");
        } else {//hidden, output
            src.append("\tdouble[] c = a[" + layerId + "]; //current layer activities\n");
            if (layer.isBiased()) {
                int off = biasOffsets.get(layer);
                src.append("\tSystem.arraycopy(w, " + off + ", c, 0, " + layerSize + "); //copy biases\n");
            } else {
                src.append("\tArrays.fill(a[" + layerId + "], 0); //not biased - zero\n");
            }
            src.append("\tdouble[] p; //previous layer activities\n");
            src.append("\tint wc;\n");
            src.append("\tint off;\n\n");
            for (ISubstrateLayer fromLayer : layerInputs.get(layer)) {
                int fromLayerId = layerOrderMap.get(fromLayer);
                int fromLayerSize = fromLayer.getNumber();
                src.append("\tp = layer" + fromLayerId + "(); //" + getLayerInfo(fromLayer) + "\n");
                src.append("\twc = 0;\n");
                src.append("\toff = " + weightOffsets.get(new SubstrateInterLayerConnection(fromLayer, layer)) + ";\n");
                src.append("\tfor(int t = 0; t < " + layerSize + "; t++) {\n");
                src.append("\t\tfor(int f = 0; f < " + fromLayerSize + "; f++) {\n");
                src.append("\t\t\tc[t] += p[f] * w[off + (wc++)];\n");
                src.append("\t\t}\n");
                src.append("\t}\n\n");
            }

            src.append("\tfor(int t = 0; t < " + layerSize + "; t++) {\n");
            src.append("\t\tc[t] = " + getActivationFunctionName(layer.getNodeActivationFunction()) + "(c[t]);\n");
            src.append("\t}\n\n");
            src.append("\treturn c;\n");
        }
        src.append("}\n\n");
    }

    protected static String getLayerInfo(ISubstrateLayer layer) {
        return layer.getNodeType() + ", nodes: " + layer.getNumber() + ", " + (layer.isBiased() ? "biased" : "not biased") + ", " + layer.getNodeActivationFunction();
    }

    protected static String getActivationFunctionName(Neuron.Activation n) {
        switch (n) {
            case SIGMOID:
                return "a";
            case SIGMOID_ALPHA1:
                return "a1";
            case BIPOLAR_SIGMOID_ALPHA1:
                return "a1b";
            default:
                throw new IllegalStateException("Not yet implemented for " + n + "!");
        }
    }

    protected void generateHelperMethods(StringBuilder src) {
        src.append("public double a(double s) {\n");
        src.append("\treturn 1/(1+Math.exp(-4.924273 * s));\n");
        src.append("}\n\n");

        src.append("public double a1(double s) {\n");
        src.append("\treturn 1/(1+Math.exp(-s));\n");
        src.append("}\n\n");

        src.append("public double a1b(double s) {\n");
        src.append("\treturn 2.0/(1+Math.exp(-s)) - 1.0;\n");
        src.append("}\n\n");

        src.append("public int getNumberOfInputs() {\n");
        src.append("\treturn ").append(numberOfInputs).append(";\n");
        src.append("}\n\n");
    }

    protected void generateFooter(StringBuilder src) {
        src.append("public int getNumberOfLayers() {\n");
        src.append("\treturn " + layers.size() + ";\n");
        src.append("}\n\n");

        src.append("public double[] getActivities(int l) {\n");
        src.append("\treturn a[l];\n");
        src.append("}\n\n");

        src.append("}\n");
    }
}
