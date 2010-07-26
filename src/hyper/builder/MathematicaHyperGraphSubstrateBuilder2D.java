package hyper.builder;

import common.mathematica.MathematicaUtils;
import hyper.cppn.ICPPN;
import hyper.substrate.ISubstrate;
import hyper.substrate.layer.ISubstrateLayer;
import hyper.substrate.layer.SubstrateInterLayerConnection;
import hyper.substrate.layer.SubstrateIntraLayerConnection;
import hyper.substrate.node.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 14, 2009
 * Time: 12:34:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaHyperGraphSubstrateBuilder2D implements ISubstrateBuilder {
    final private ISubstrate substrate;

    private boolean built = false;
    private String mathematicaExpression;

    public MathematicaHyperGraphSubstrateBuilder2D(ISubstrate substrate) {
        if (substrate.getMaxDimension() > 1) {
            throw new IllegalArgumentException("The substrate is > 1D.");
        }
        this.substrate = substrate;
    }

    public ISubstrate getSubstrate() {
        return substrate;
    }

    public void build(ICPPN aCPPN) {
        StringBuilder vertices = new StringBuilder();
        StringBuilder coords = new StringBuilder();

        Map<Node, Integer> indexMap = new HashMap<Node, Integer>();
        int counter = 1;


        double layerLevel = 0.0;
        coords.append("coords := {");
        for (Iterator<ISubstrateLayer> itLayer = substrate.getLayers().iterator(); itLayer.hasNext();) {
            ISubstrateLayer layer = itLayer.next();
            Node[] nodes = layer.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                Node node = nodes[i];
                coords.append(counter + "->{" + node.getCoordinate() + "," + layerLevel + "}");
                if (itLayer.hasNext() || i != (nodes.length - 1)) {
                    coords.append(", ");
                }
                indexMap.put(node, counter++);
            }
            layerLevel += 3.0;
        }
        coords.append("}\n");

        vertices.append("vertices := {");

        // inter-layer connections
        boolean printComma = false;
        for (Iterator<SubstrateInterLayerConnection> itConnection = substrate.getConnections().iterator(); itConnection.hasNext();) {
            SubstrateInterLayerConnection connection = itConnection.next();
            int aCPPNOutput = substrate.getConnectionCPPNOutput(connection);
            Node[] fromNodes = connection.getFrom().getNodes();
            Node[] toNodes = connection.getTo().getNodes();
            for (int i = 0; i < fromNodes.length; i++) {
                for (int j = 0; j < toNodes.length; j++) {
                    double weight = 1.0 * aCPPN.evaluate(aCPPNOutput, fromNodes[i].getCoordinate(), toNodes[j].getCoordinate());
                    vertices.append("{" + indexMap.get(fromNodes[i]) + "->" + indexMap.get(toNodes[j]) + ", " + MathematicaUtils.toMathematica(weight) + "}");
                    printComma = true;
                    if (i != (fromNodes.length - 1) || j != (toNodes.length - 1) || itConnection.hasNext()) {
                        vertices.append(", ");
                    }
                }
            }

        }

        // intra-layer connections
        int layerCounter = 0;
        for (ISubstrateLayer layer : substrate.getLayers()) {
            if (layer.hasIntraLayerConnections()) {
                int aCPPNOutput = substrate.getConnectionCPPNOutput(layer);
                int connectionCounter = 0;
                for (SubstrateIntraLayerConnection connection : layer.getIntraLayerConnections()) {
                    if (printComma) {
                        vertices.append(", ");
                        printComma = false;
                    }
                    Node fromNode = connection.getFrom();
                    Node toNode = connection.getTo();
                    double weight = 1.0 * aCPPN.evaluate(aCPPNOutput, fromNode.getCoordinate(), toNode.getCoordinate());
                    vertices.append("{" + indexMap.get(fromNode) + "->" + indexMap.get(toNode) + ", " + MathematicaUtils.toMathematica(weight) + "}");

                    if (layerCounter != (substrate.getLayers().size() - 1) || connectionCounter != (layer.getIntraLayerConnections().length - 1)) {
                        vertices.append(", ");
                    }
                    connectionCounter++;
                }
            }
            layerCounter++;
        }

        vertices.append("}\n");
        mathematicaExpression = vertices.append(coords).toString();
/*
GraphPlot[vertices,  VertexLabeling -> True,
 VertexCoordinateRules -> coords, EdgeLabeling -> All,
 EdgeRenderingFunction -> ({Arrowheads[.015],
     Thickness[Abs[#3]/100.0],
     If [#3 > 0, {Red, Arrow[#, 0.05]}, {Blue, Arrow[#, 0.05]}],
     Inset[#3, Mean[#1], Automatic, Automatic, #[[1]] - #[[2]],
      Background -> White]} &)]
 */
        built = true;
    }

    public String getMathematicaExpression() {
        if (!built) {
            throw new IllegalStateException("Network not built yet.");
        }
        return mathematicaExpression;
    }
}
