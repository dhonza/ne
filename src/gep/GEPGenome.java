package gep;

import common.RND;
import gp.Node;
import gp.NodeCollection;
import gp.TreeInputs;
import gp.functions.Add;
import gp.functions.Multiply;
import gp.terminals.Constant;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 4:54:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEPGenome {
    private Node root = null;

    private Node[] headTail;
    private int[] dc;
    private double[] constants;

    private int ptr;

    public static GEPGenome createRandom(NodeCollection nodeCollection) {
        GEPGenome tree = new GEPGenome();

        tree.headTail = new Node[GEP.HEAD_TAIL];
        tree.headTail[0] = nodeCollection.getRandomFunction(); //always function in the root
        for (int i = 1; i < GEP.HEAD; i++) {
            tree.headTail[i] = nodeCollection.getRandomOfAll();
        }
        for (int i = GEP.HEAD; i < GEP.HEAD_TAIL; i++) {
            tree.headTail[i] = nodeCollection.getRandomTerminal();
        }

        tree.dc = new int[GEP.DC];
        for (int i = 0; i < GEP.DC; i++) {
            tree.dc[i] = RND.getInt(0, GEP.C_SIZE - 1);
        }

        tree.constants = new double[GEP.C_SIZE];
        for (int i = 0; i < GEP.C_SIZE; i++) {
            tree.constants[i] = RND.getDouble(-GEP.C_SIZE, GEP.C_SIZE);
        }

        return tree;
    }

    public double evaluate(TreeInputs treeInputs) {
        buildTree();
        return root.evaluate(treeInputs);
    }

    private void buildTree() {
        if (root == null) {
            ptr = 0;
            root = buildTree(headTail[ptr++], 0);
        }
    }

    private Node buildTree(Node node, int depth) {
        Node[] children = new Node[node.getArity()];
        for (int i = 0; i < node.getArity(); i++) {
            children[i] = headTail[ptr++];
        }
        for (int i = 0; i < node.getArity(); i++) {
            children[i] = buildTree(children[i], depth + 1);
        }
        return node.create(depth, children);
    }

    public GEPGenome mutate(NodeCollection nodeCollection) {
//        nodes.clear();
//        ancestors.clear();

//        populateNodes(root, null);
//        int mutationPoint = RND.getInt(0, nodes.size() - 1);
//
//        Node mutatedNode = nodes.get(mutationPoint);
//        Node mutatedNodeAncestor = ancestors.get(mutatedNode);
//        Node prototype = nodeCollection.getRandomWithArity(mutatedNode.getArity());
//
//        if (prototype.getClass() == mutatedNode.getClass()) {//no change at all
//            return this;
//        }
//
//        Node newNode = prototype.create(mutatedNode.getDepth(), mutatedNode.getChildren());

        GEPGenome mutated = new GEPGenome();
//        mutated.root = replaceAncestors(mutatedNodeAncestor, mutatedNode, newNode);
        return mutated;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < GEP.HEAD_TAIL; i++) {
            builder.append(headTail[i]);
            if (i == GEP.HEAD - 1) {
                builder.append('|');
            } else if (i != GEP.HEAD_TAIL - 1) {
                builder.append(',');
            }
        }
        builder.append('|');
        for (int i = 0; i < GEP.DC; i++) {
            builder.append(dc[i]);
            if (i != GEP.DC - 1) {
                builder.append(',');
            }
        }
        builder.append(" {");
        for (int i = 0; i < GEP.C_SIZE; i++) {
            builder.append(constants[i]);
            if (i != GEP.C_SIZE - 1) {
                builder.append(',');
            }

        }
        builder.append("}");
        return builder.toString();
    }

    public static void main(String[] args) {
        Node[] functions = new Node[]{new Add(), new Multiply()};
        Node[] terminals = new Node[]{new Constant(1)};
        GEP.initGenomes(functions);
        NodeCollection c = new NodeCollection(functions, terminals);
        RND.initializeTime();
        GEPGenome g = createRandom(c);
        System.out.println(g);
        g.buildTree();
        System.out.println(g.root);
    }
}
