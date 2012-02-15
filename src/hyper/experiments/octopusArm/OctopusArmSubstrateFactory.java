package hyper.experiments.octopusArm;

import hyper.substrate.BasicSubstrate;
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
public class OctopusArmSubstrateFactory {
    private OctopusArmSubstrateFactory() {
    }

    public static BasicSubstrate createInputHiddenOutputNoBias(int segments, int sensors) {
        BasicSubstrate substrate = new BasicSubstrate();

        //TODO activation functions and ranges does not match exactly Brian Woolley's LidarOnAllSegments2_GE substrate
        ISubstrateLayer inputLayer = new MeshLayer2D(NodeType.INPUT, segments, sensors, 2.0, 2.0);
        ISubstrateLayer hiddenLayer = new MeshLayer2D(NodeType.HIDDEN, segments, 3, 2.0, 2.0);
        ISubstrateLayer outputLayer = new MeshLayer2D(NodeType.OUTPUT, segments, 3, 2.0, 2.0);

        SubstrateInterLayerConnection inputToHidden = new SubstrateInterLayerConnection(inputLayer, hiddenLayer);
        SubstrateInterLayerConnection hiddenToOutput = new SubstrateInterLayerConnection(hiddenLayer, outputLayer);

        substrate.addLayer(inputLayer);
        substrate.addLayer(hiddenLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(inputToHidden);
        substrate.connect(hiddenToOutput);

        substrate.complete();
        return substrate;
    }
}