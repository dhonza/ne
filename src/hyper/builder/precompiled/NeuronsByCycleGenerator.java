package hyper.builder.precompiled;

import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2010
 * Time: 10:13:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class NeuronsByCycleGenerator {
    protected List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections = null;
    protected int numberOfInputs;
    protected ISubstrateLayer biasLayer = null;
    int weightCnt = 0;

    public NeuronsByCycleGenerator(List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections, int numberOfInputs, ISubstrateLayer biasLayer) {
        this.successiveConnections = successiveConnections;
        this.numberOfInputs = numberOfInputs;
        this.biasLayer = biasLayer;
    }

    protected void generateHeader(StringBuilder src) {
        src.append("public class PrecompiledStub implements common.net.precompiled.IPrecompiledFeedForwardStub {\n");
        src.append("\t double[] w;\n");
        src.append("\t double[] in;\n");
        src.append("\t double[] p;\n");
        src.append("\t double[][] a;\n");

        src.append("public double[] propagate(double b, double[] in, double[] w) {\n");
        src.append("\tthis.w = w;\n");
        src.append("\tthis.in = in;\n");
        src.append("\tp = in;\n");
        src.append("\ta = new double[" + successiveConnections.size() + "][];\n");
        src.append("\tdouble[] n = in;\n");
        for (int i = 0; i < successiveConnections.size(); i++) {
            src.append("\tn = layer" + i + "(b, n);\n");
        }
        src.append("\treturn n;\n");
        src.append("}\n\n");
    }

    protected void generateLayers(StringBuilder src) {
        weightCnt = 0;
        for (int i = 0; i < successiveConnections.size(); i++) {
            generateLayer(src, i);
        }
    }

    protected void generateLayer(StringBuilder src, int i) {
        src.append("private double[] layer" + i + "(double b, double[] in) {\n");
        src.append("\tthis.p = in;\n");
        src.append("\tdouble[] n;\n");
        SubstrateInterLayerConnection connection = successiveConnections.get(i).connection;
        Node[] tNodes = connection.getTo().getNodes();
        Node[] fNodes = connection.getFrom().getNodes();
        src.append("\tn = new double[").append(tNodes.length).append("];\n");

        for (int t = 0; t < tNodes.length; t++) {
            src.append("\tn[").append(t).append("] = " + getActivationFunction(tNodes[t]) + "(");
            if (biasLayer != null) {
                src.append("w[").append(weightCnt++).append("]*b + ");
            }
            src.append("s(" + fNodes.length + ", " + weightCnt + "));\n");
            weightCnt += fNodes.length;
        }
        src.append("\ta[" + i + "] = n;\n");
        src.append("\treturn n;\n}\n\n");
    }


    protected static String getActivationFunction(Node n) {
        switch (n.getActivationFunction()) {
            case SIGMOID:
                return "a";
            case SIGMOID_ALPHA1:
                return "a1";
            case BIPOLAR_SIGMOID_ALPHA1:
                return "a1b";
            default:
                throw new IllegalStateException("Not yet implemented!");
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

        src.append("public double s(int n, int wc) {\n");
        src.append("\tdouble sum = 0;\n");
        src.append("\tfor(int i = 0; i < n; i++) {");
        src.append("sum += w[(wc + i)] * p[i]; }\n");
        src.append("\treturn sum;\n");
        src.append("}\n\n");
    }

    protected void generateFooter(StringBuilder src) {
        src.append("public int getNumberOfLayers() {\n");
        src.append("\treturn " + successiveConnections.size() + ";\n");
        src.append("}\n\n");

        src.append("public double[] getActivities(int l) {\n");
        src.append("\tif(l == 0) return in;\n");
        src.append("\telse return a[l-1];\n");
        src.append("}\n\n");

        src.append("}\n");
    }
}
