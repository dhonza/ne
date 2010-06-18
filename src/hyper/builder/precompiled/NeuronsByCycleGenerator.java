package hyper.builder.precompiled;

import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateLayer;
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
    public NeuronsByCycleGenerator(List<PrecompiledFeedForwardSubstrateBuilder.PreviousLayerConnectionContainer> successiveConnections, int numberOfInputs, SubstrateLayer biasLayer) {
        super(successiveConnections, numberOfInputs, biasLayer);
    }

    @Override
    protected void generateLayers(StringBuilder src) {
        src.append("\tdouble[] p = in;\n");
        src.append("\tdouble[] n;\n");
        src.append("\tdouble sum;\n");

        int weightCnt = 0;
        for (int i = 0; i < successiveConnections.size(); i++) {
            SubstrateInterLayerConnection connection = successiveConnections.get(i).connection;
            Node[] tNodes = connection.getTo().getNodes();
            Node[] fNodes = connection.getFrom().getNodes();
            src.append("\tn = new double[").append(tNodes.length).append("];\n");

            for (int t = 0; t < tNodes.length; t++) {
                src.append("\tsum = 0;\n");
                src.append("\tfor(int i = 0; i < " + fNodes.length + "; i++) {");
                src.append("sum += w[" + (weightCnt + i) + "] * p[" + i + "]; }\n ");
                weightCnt += fNodes.length;
                src.append("\tn[").append(t).append("] = a(sum);\n");
            }
        }

    }
}
