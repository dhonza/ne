package gpaac;

import common.RND;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 6:07:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class AACTree {
    private INode root;

    public static AACTree createRandom(NodeCollection nodeCollection) {
        AACTree tree = new AACTree();
        tree.root = createRandomSubtree(nodeCollection, 0);
        return tree;
    }

    private static INode createRandomSubtree(NodeCollection nodeCollection, int startDepth) {
        INode node;
        INode choice;
        if (startDepth < GPAAC.MAX_DEPTH) {
            choice = nodeCollection.getRandomOfAll();
        } else {
            choice = nodeCollection.getRandomTerminal();
        }
        int arity = choice.getArity();
        INode[] children = new INode[arity];
        for (int i = 0; i < arity; i++) {
            children[i] = createRandomSubtree(nodeCollection, startDepth + 1);
        }

        node = choice.create(startDepth, children);

        return node;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void main(String[] args) {
        INode[] functions = new INode[]{new Functions.Multiply(), new Functions.APlus()};
        INode[] terminals = new INode[]{new Terminals.Input(1), new Terminals.Input(2)};
        NodeCollection nodeCollection = new NodeCollection(functions, terminals);

        RND.initializeTime();
        AACTree tree = AACTree.createRandom(nodeCollection);
        System.out.println(tree);
    }
}
