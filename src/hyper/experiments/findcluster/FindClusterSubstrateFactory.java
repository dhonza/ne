package hyper.experiments.findcluster;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.MeshLayer2D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node2D;
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
        BasicSubstrate substrate = new BasicSubstrate(new Node2D(0.0, 0.0, NodeType.BIAS));

        ISubstrateLayer inputLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0, false);
        ISubstrateLayer outputLayer = new MeshLayer2D(NodeType.OUTPUT, numNodesX, numNodesY, 2.0, 2.0, false);

        SubstrateInterLayerConnection inputToOutput = new SubstrateInterLayerConnection(inputLayer, outputLayer);

        substrate.addLayer(inputLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(inputToOutput);
        substrate.complete();
        return substrate;
    }
}