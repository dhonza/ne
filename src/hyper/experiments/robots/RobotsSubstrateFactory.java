package hyper.experiments.robots;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.*;
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
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer biasLayer = new BiasLayer1D(0.0);
        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -Math.PI / 2, Math.PI / (inputNodes - 1));
        ISubstrateLayer outputLayer = new LineLayerFullyConnected1D(NodeType.OUTPUT, hiddenOutputNodes, -Math.PI / 2, Math.PI / (hiddenOutputNodes - 1));

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
