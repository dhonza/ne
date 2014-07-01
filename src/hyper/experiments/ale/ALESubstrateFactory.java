package hyper.experiments.ale;

import hyper.substrate.BasicSubstrate;
import hyper.substrate.layer.BiasLayer2D;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.MeshLayer2D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 4:05 PM
 */
public class ALESubstrateFactory {
    private ALESubstrateFactory() {
    }

    public static BasicSubstrate createGrayDirectionOnly(int numNodesX, int numNodesY, boolean singleAxis) {
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer biasLayer = new BiasLayer2D(0.0, 0.0);
        ISubstrateLayer inputGrayLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0);
        ISubstrateLayer hiddenLayer = new MeshLayer2D(NodeType.HIDDEN, numNodesX / 2, numNodesY / 2, 2.0, 2.0);
        ISubstrateLayer outputDirectionLayer = null;
        if (singleAxis) {
            outputDirectionLayer = new MeshLayer2D(NodeType.OUTPUT, 3, 1, 2.0, 2.0);
        } else {
            outputDirectionLayer = new MeshLayer2D(NodeType.OUTPUT, 3, 3, 2.0, 2.0);
        }

        SubstrateInterLayerConnection inputGrayToHidden = new SubstrateInterLayerConnection(inputGrayLayer, hiddenLayer);
        SubstrateInterLayerConnection hiddenToOutputDirection = new SubstrateInterLayerConnection(hiddenLayer, outputDirectionLayer);
        SubstrateInterLayerConnection biasToHidden = new SubstrateInterLayerConnection(biasLayer, hiddenLayer);
        SubstrateInterLayerConnection biasToOutputDirection = new SubstrateInterLayerConnection(biasLayer, outputDirectionLayer);

        substrate.addLayer(biasLayer);
        substrate.addLayer(inputGrayLayer);
        substrate.addLayer(hiddenLayer);
        substrate.addLayer(outputDirectionLayer);

        substrate.connect(inputGrayToHidden);
        substrate.connect(hiddenToOutputDirection);
        substrate.connect(biasToHidden);
        substrate.connect(biasToOutputDirection);

        substrate.complete();
        return substrate;
    }

    public static BasicSubstrate create(int numNodesX, int numNodesY) {
        BasicSubstrate substrate = new BasicSubstrate();

        ISubstrateLayer inputRedLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0);
        ISubstrateLayer inputGreenLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0);
        ISubstrateLayer inputBlueLayer = new MeshLayer2D(NodeType.INPUT, numNodesX, numNodesY, 2.0, 2.0);

        ISubstrateLayer hiddenLayer = new MeshLayer2D(NodeType.HIDDEN, numNodesX, numNodesY, 2.0, 2.0);

        ISubstrateLayer outputDirectionLayer = new MeshLayer2D(NodeType.OUTPUT, 3, 3, 2.0, 2.0);
        ISubstrateLayer outputFireLayer = new MeshLayer2D(NodeType.OUTPUT, 1, 1, 2.0, 2.0);


        SubstrateInterLayerConnection inputRedToHidden = new SubstrateInterLayerConnection(inputRedLayer, hiddenLayer);
        SubstrateInterLayerConnection inputGreenToHidden = new SubstrateInterLayerConnection(inputGreenLayer, hiddenLayer);
        SubstrateInterLayerConnection inputBlueToHidden = new SubstrateInterLayerConnection(inputBlueLayer, hiddenLayer);

        SubstrateInterLayerConnection hiddenToOutputDirection = new SubstrateInterLayerConnection(hiddenLayer, outputDirectionLayer);
        SubstrateInterLayerConnection hiddenToOutputFire = new SubstrateInterLayerConnection(hiddenLayer, outputFireLayer);

        substrate.addLayer(inputRedLayer);
        substrate.addLayer(inputGreenLayer);
        substrate.addLayer(inputBlueLayer);
        substrate.addLayer(hiddenLayer);
        substrate.addLayer(outputDirectionLayer);
        substrate.addLayer(outputFireLayer);

        substrate.connect(inputRedToHidden);
        substrate.connect(inputGreenToHidden);
        substrate.connect(inputBlueToHidden);
        substrate.connect(hiddenToOutputDirection);
        substrate.connect(hiddenToOutputFire);

        substrate.complete();
        return substrate;
    }
}
