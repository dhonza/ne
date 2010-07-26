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
public class NeuronsByCycleGenerator extends NoCyclesGenerator {
    public NeuronsByCycleGenerator(List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections, int numberOfInputs, ISubstrateLayer biasLayer) {
        super(successiveConnections, numberOfInputs, biasLayer);
    }

    @Override
    protected void generateLayers(StringBuilder src) {
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
                src.append("s(" + fNodes.length + ", " + weightCnt + "));\n");
                weightCnt += fNodes.length;
            }
            src.append("\tp = n;\n");
        }
        src.append("\treturn n;\n}\n\n");
    }

    @Override
    protected void generateHelperMethods(StringBuilder src) {
        super.generateHelperMethods(src);
        src.append("public double s(int n, int wc) {\n");
        src.append("\tdouble sum = 0;\n");
        src.append("\tfor(int i = 0; i < n; i++) {");
        src.append("sum += w[(wc + i)] * p[i]; }\n");
        src.append("\treturn sum;\n");
        src.append("}\n\n");
    }
}
