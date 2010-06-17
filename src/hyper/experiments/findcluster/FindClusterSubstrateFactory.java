package hyper.experiments.findcluster;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.*;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 21, 2009
 * Time: 11:06:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class FindClusterSubstrateFactory {
    private FindClusterSubstrateFactory() {
    }

    public static BasicSubstrate createInputToOutputNoBias(int numNodesX, int numNodesY) {
        BasicSubstrate substrate = new BasicSubstrate();

        SubstrateLayer inputLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0);
        SubstrateLayer outputLayer = new MeshLayer2D(NodeType.OUTPUT, numNodesX, numNodesY, 2.0, 2.0);

        SubstrateInterLayerConnection inputToOutput = new SubstrateInterLayerConnection(inputLayer, outputLayer);

        substrate.addLayer(inputLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(inputToOutput);
        substrate.complete();
        return substrate;
    }
}