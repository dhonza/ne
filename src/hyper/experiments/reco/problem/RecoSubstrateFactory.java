package hyper.experiments.reco.problem;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.BiasLayer1D;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.LineLayer1D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 21, 2009
 * Time: 11:06:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class RecoSubstrateFactory {
    private RecoSubstrateFactory() {
    }

    public static BasicSubstrate createInputToOutput(int nodes) {
        return createInputToOutput(nodes, nodes);
    }

    public static BasicSubstrate createInputToOutput(int inputNodes, int outputNodes) {
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer biasLayer = new BiasLayer1D(0.0);
        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -2.0, 1.0);
        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, -2.0, 1.0);

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


    public static BasicSubstrate createInputHiddenOutput(int nodes, int hiddenNodes) {
        return createInputHiddenOutput(nodes, hiddenNodes, nodes);
    }

    public static BasicSubstrate createInputHiddenOutput(int inputNodes, int hiddenNodes, int outputNodes) {
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer biasLayer = new BiasLayer1D(0.0);
        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, 1.0, 1.0);
        ISubstrateLayer hiddenLayer = new LineLayer1D(NodeType.HIDDEN, hiddenNodes, 1.0, 1.0);
        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, 1.0, 1.0);

        SubstrateInterLayerConnection biasToHidden = new SubstrateInterLayerConnection(biasLayer, hiddenLayer);
        SubstrateInterLayerConnection inputToHidden = new SubstrateInterLayerConnection(inputLayer, hiddenLayer);
        SubstrateInterLayerConnection biasToOutput = new SubstrateInterLayerConnection(biasLayer, outputLayer);
        SubstrateInterLayerConnection hiddenToOutput = new SubstrateInterLayerConnection(hiddenLayer, outputLayer);

        substrate.addLayer(biasLayer);
        substrate.addLayer(inputLayer);
        substrate.addLayer(hiddenLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(biasToHidden);
        substrate.connect(inputToHidden);
        substrate.connect(biasToOutput);
        substrate.connect(hiddenToOutput);
        substrate.complete();
        return substrate;
    }
}
