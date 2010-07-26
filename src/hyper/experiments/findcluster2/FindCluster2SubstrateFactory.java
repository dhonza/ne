package hyper.experiments.findcluster2;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.BiasLayer2D;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.MeshLayer2D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 21, 2009
 * Time: 11:06:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class FindCluster2SubstrateFactory {
    private FindCluster2SubstrateFactory() {
    }

    public static BasicSubstrate createInputToOutput(int resolution) {
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer biasLayer = new BiasLayer2D(0, 0);
        ISubstrateLayer inputLayer = new MeshLayer2D(NodeType.INPUT, resolution, resolution, 2.0, 2.0);
        ISubstrateLayer outputLayer = new MeshLayer2D(NodeType.OUTPUT, resolution, resolution, 2.0, 2.0);

        SubstrateInterLayerConnection biasToOutput = new SubstrateInterLayerConnection(biasLayer, outputLayer);
        SubstrateInterLayerConnection inputToOutput = new SubstrateInterLayerConnection(inputLayer, outputLayer);

        substrate.addLayer(biasLayer);
        substrate.addLayer(inputLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(biasToOutput);
        substrate.connect(inputToOutput);
        substrate.complete();
        return substrate;
    }
}