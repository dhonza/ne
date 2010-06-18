package hyper.builder.precompiled;

import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
import hyper.substrate.node.Node;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2010
 * Time: 9:58:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class NoCyclesGenerator {
    protected List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections = null;
    protected int numberOfInputs;
    protected SubstrateLayer biasLayer = null;

    public NoCyclesGenerator(List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections, int numberOfInputs, SubstrateLayer biasLayer) {
        this.successiveConnections = successiveConnections;
        this.numberOfInputs = numberOfInputs;
        this.biasLayer = biasLayer;
    }

    protected void generateHeader(StringBuilder src) {
        src.append("public class PrecompiledStub implements common.net.precompiled.IPrecompiledFeedForwardStub {\n");
        src.append("public double[] propagate(double b, double[] in, double[] w) {\n");
    }

    protected void generateLayers(StringBuilder src) {
        src.append("\tdouble[] p = in;\n");
        src.append("\tdouble[] n;\n");

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

    protected void generateFooter(StringBuilder src) {
        src.append("\treturn n;\n}\n\n");
        src.append("public double a(double s) {\n");
        src.append("\treturn 1/(1+Math.exp(-4.924273 * s));\n");
        src.append("}\n");
        src.append("public int getNumberOfInputs() {\n");
        src.append("\treturn ").append(numberOfInputs).append(";\n");
        src.append("}\n}\n");
    }
}
