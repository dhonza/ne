package gep;

import common.RND;
import gp.Node;
import gp.NodeCollection;
import gp.TreeInputs;
import gp.functions.Add;
import gp.functions.Multiply;
import gp.terminals.RNC;

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
    private int constPtr;

    private GEPGenome() {
    }

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
            tree.constants[i] = RND.getDouble(-GEP.CONSTANT_AMPLITUDE, GEP.CONSTANT_AMPLITUDE);
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
            constPtr = 0;
            root = buildTree(headTail[ptr++], 0);
        }
    }

    private Node buildTree(Node node, int depth) {
        Node[] children = new Node[node.getArity()];
        if (node instanceof RNC) {
            double value = constants[dc[constPtr++]];
            return new RNC(depth, value);
        }
        for (int i = 0; i < node.getArity(); i++) {
            children[i] = headTail[ptr++];
        }
        for (int i = 0; i < node.getArity(); i++) {
            children[i] = buildTree(children[i], depth + 1);
        }
        return node.create(depth, children);
    }

    public GEPGenome mutate(NodeCollection nodeCollection) {
        GEPGenome mutated = this.clone();
        for (int i = 0; i < GEP.HEAD_TAIL; i++) {
            if (RND.getDouble() < GEP.MUTATION_HEADTAIL_RATE) {
                if (i < GEP.HEAD) {
                    mutated.headTail[i] = nodeCollection.getRandomOfAll();
                } else {
                    mutated.headTail[i] = nodeCollection.getRandomTerminal();
                }
            }
        }

        for (int i = 0; i < GEP.DC; i++) {
            if (RND.getDouble() < GEP.MUTATION_DC_RATE) {
                mutated.dc[i] = RND.getInt(0, GEP.C_SIZE - 1);
            }
        }

        for (int i = 0; i < GEP.C_SIZE; i++) {
            if (RND.getDouble() < GEP.MUTATION_CAUCHY_PROBABILITY) {
                mutated.constants[i] += GEP.MUTATION_CAUCHY_POWER * RND.getCauchy();
            }
        }
        return mutated;
    }

    @Override
    public String toString() {
        buildTree();
        return toStringKarva() + "\n" + root.toString();
    }

    public String toStringKarva() {
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

    protected GEPGenome clone() {
        GEPGenome tree = new GEPGenome();

        tree.headTail = this.headTail.clone();
        tree.dc = this.dc.clone();
        tree.constants = this.constants.clone();

        return tree;
    }

    public static void main(String[] args) {
        Node[] functions = new Node[]{new Add(), new Multiply()};
        Node[] terminals = new Node[]{new RNC()};
        GEP gep = new GEP(null, functions, terminals);
        NodeCollection c = new NodeCollection(functions, terminals);
        RND.initializeTime();
        GEPGenome g = createRandom(c);
        System.out.println(g.toStringKarva());
        System.out.println(g);
        GEPGenome g2 = g.mutate(c);
        System.out.println(g2.toStringKarva());
        System.out.println(g2);
    }
}
