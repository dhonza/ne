package hyper.experiments.robots;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.LineLayer1D;
import hyper.substrate.layer.LineLayerFullyConnected1D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node1D;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RobotsSubstrateFactory {
    private RobotsSubstrateFactory() {
    }

    public static BasicSubstrate create(int inputNodes, int hiddenOutputNodes) {
        BasicSubstrate substrate = new BasicSubstrate(new Node1D(0.0, NodeType.BIAS));

        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -Math.PI / 2, Math.PI / (inputNodes - 1), false);
        ISubstrateLayer outputLayer = new LineLayerFullyConnected1D(NodeType.OUTPUT, hiddenOutputNodes, -Math.PI / 2, Math.PI / (hiddenOutputNodes - 1), true);

        SubstrateInterLayerConnection inputToOutput = new SubstrateInterLayerConnection(inputLayer, outputLayer);

        substrate.addLayer(inputLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(inputToOutput);
        substrate.complete();
        return substrate;
    }
}
