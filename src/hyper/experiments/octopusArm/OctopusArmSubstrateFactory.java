package hyper.experiments.octopusArm;

import common.net.linked.Neuron;
import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.CartesianSheet;
import hyper.substrate.layer.ISubstrateLayer;
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

        ISubstrateLayer inputLayer = new CartesianSheet(segments, sensors, NodeType.INPUT, Neuron.Activation.LINEAR);
        ISubstrateLayer hiddenLayer = new CartesianSheet(segments, 3, NodeType.HIDDEN, Neuron.Activation.BIPOLAR_SIGMOID_ALPHA1);
        ISubstrateLayer outputLayer = new CartesianSheet(segments, 3, NodeType.OUTPUT, Neuron.Activation.SIGMOID_ALPHA1);

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