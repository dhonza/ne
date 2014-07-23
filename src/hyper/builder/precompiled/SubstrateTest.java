package hyper.builder.precompiled;

import hyper.builder.MockWeightEvaluator;
import hyper.substrate.BasicSubstrate;
import hyper.substrate.ISubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.MeshLayer2D;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.node.Node2D;
import hyper.substrate.node.NodeType;

/**
 * Created by drchajan on 22/07/14.
 */
public class SubstrateTest {
    private static ISubstrate complexSubstrate() {
        BasicSubstrate substrate = new BasicSubstrate(new Node2D(0.0, 0.0, NodeType.BIAS));

        ISubstrateLayer i1 = new MeshLayer2D(NodeType.INPUT, 1, 1, 2.0, 2.0, false);
        ISubstrateLayer i2 = new MeshLayer2D(NodeType.INPUT, 2, 1, 2.0, 2.0, false);
        ISubstrateLayer i3 = new MeshLayer2D(NodeType.INPUT, 3, 1, 2.0, 2.0, false);

        ISubstrateLayer h1 = new MeshLayer2D(NodeType.HIDDEN, 2, 1, 2.0, 2.0, true);
        ISubstrateLayer h2 = new MeshLayer2D(NodeType.HIDDEN, 2, 1, 2.0, 2.0, true);
        ISubstrateLayer h3 = new MeshLayer2D(NodeType.HIDDEN, 2, 1, 2.0, 2.0, true);
        ISubstrateLayer h4 = new MeshLayer2D(NodeType.HIDDEN, 2, 1, 2.0, 2.0, true);

        ISubstrateLayer o1 = new MeshLayer2D(NodeType.OUTPUT, 2, 1, 2.0, 2.0, true);
        ISubstrateLayer o2 = new MeshLayer2D(NodeType.OUTPUT, 3, 1, 2.0, 2.0, true);

        substrate.addLayer(i1);
        substrate.addLayer(i2);
        substrate.addLayer(i3);
        substrate.addLayer(h1);
        substrate.addLayer(h2);
        substrate.addLayer(h3);
        substrate.addLayer(h4);
        substrate.addLayer(o1);
        substrate.addLayer(o2);

        substrate.connect(new SubstrateInterLayerConnection(i1, h1)); //2
        substrate.connect(new SubstrateInterLayerConnection(i1, h2)); //2
        substrate.connect(new SubstrateInterLayerConnection(i2, h1)); //4
        substrate.connect(new SubstrateInterLayerConnection(i2, h2)); //4
        substrate.connect(new SubstrateInterLayerConnection(i3, h3)); //6
        substrate.connect(new SubstrateInterLayerConnection(h1, h4)); //4
        substrate.connect(new SubstrateInterLayerConnection(h2, h4)); //4
        substrate.connect(new SubstrateInterLayerConnection(h4, o1)); //4
        substrate.connect(new SubstrateInterLayerConnection(h3, o1)); //4
        substrate.connect(new SubstrateInterLayerConnection(h4, o2)); //6
        substrate.connect(new SubstrateInterLayerConnection(h3, o2)); //6

        substrate.complete();
        return substrate;
    }

    private static ISubstrate simpleSubstrate() {
        BasicSubstrate substrate = new BasicSubstrate(new Node2D(0.0, 0.0, NodeType.BIAS));

        ISubstrateLayer i1 = new MeshLayer2D(NodeType.INPUT, 2, 1, 2.0, 2.0, false);
//        ISubstrateLayer i2 = new MeshLayer2D(NodeType.INPUT, 2, 1, 2.0, 2.0, false);

        ISubstrateLayer h1 = new MeshLayer2D(NodeType.HIDDEN, 3, 1, 2.0, 2.0, true);

        ISubstrateLayer o1 = new MeshLayer2D(NodeType.OUTPUT, 4, 1, 2.0, 2.0, true);
//        ISubstrateLayer o2 = new MeshLayer2D(NodeType.OUTPUT, 1, 1, 2.0, 2.0, false);

        substrate.addLayer(i1);
//        substrate.addLayer(i2);
        substrate.addLayer(h1);
        substrate.addLayer(o1);
//        substrate.addLayer(o2);

        substrate.connect(new SubstrateInterLayerConnection(i1, h1));
//        substrate.connect(new SubstrateInterLayerConnection(i2, h1));
        substrate.connect(new SubstrateInterLayerConnection(h1, o1));
//        substrate.connect(new SubstrateInterLayerConnection(h1, o2));

        substrate.complete();
        return substrate;
    }

    public static void main(String[] args) {
        PrecompiledFeedForwardSubstrateBuilder builder = new PrecompiledFeedForwardSubstrateBuilder(complexSubstrate(), new MockWeightEvaluator());
        builder.build(null);
    }
}
