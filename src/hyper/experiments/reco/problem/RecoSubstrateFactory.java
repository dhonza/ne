package hyper.experiments.reco.problem;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.LineLayer1D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node1D;
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
        BasicSubstrate substrate = new BasicSubstrate(new Node1D(0.0, NodeType.BIAS));

        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -2.0, 1.0, false);
        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, -2.0, 1.0, true);

        //for GECCO 2012 review: test of CPPN inputs scaled to -1:1
//        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -1.0, 2.0/(inputNodes - 1.0));
//        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, -1.0, 2.0/(inputNodes - 1.0));

        SubstrateInterLayerConnection inputToOutput = new SubstrateInterLayerConnection(inputLayer, outputLayer);

        substrate.addLayer(inputLayer);
        substrate.addLayer(outputLayer);

        substrate.connect(inputToOutput);
        substrate.complete();
        return substrate;
    }


    public static BasicSubstrate createInputHiddenOutput(int nodes, int hiddenNodes) {
        return createInputHiddenOutput(nodes, hiddenNodes, nodes);
    }

    public static BasicSubstrate createInputHiddenOutput(int inputNodes, int hiddenNodes, int outputNodes) {
        BasicSubstrate substrate = new BasicSubstrate(new Node1D(0.0, NodeType.BIAS));

        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, 1.0, 1.0, false);
        ISubstrateLayer hiddenLayer = new LineLayer1D(NodeType.HIDDEN, hiddenNodes, 1.0, 1.0, true);
        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, 1.0, 1.0, true);

        //for GECCO review: test of CPPN inputs scaled to -1:1
//        ISubstrateLayer inputLayer = new LineLayer1D(NodeType.INPUT, inputNodes, -1.0, 2.0/(inputNodes - 1.0));
//        ISubstrateLayer hiddenLayer = new LineLayer1D(NodeType.HIDDEN, hiddenNodes, -1.0, 2.0/(inputNodes - 1.0));
//        ISubstrateLayer outputLayer = new LineLayer1D(NodeType.OUTPUT, outputNodes, -1.0, 2.0/(inputNodes - 1.0));

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
