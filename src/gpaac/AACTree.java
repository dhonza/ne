package gpaac;

import common.RND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 6:07:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class AACTree {
    private INode root;

    transient private List<INode> nodes = new ArrayList<INode>();
    transient private Map<INode, INode> ancestors = new HashMap<INode, INode>();

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

    private void populateNodes(INode startNode, INode ancestor) {
        nodes.add(startNode);
        ancestors.put(startNode, ancestor);
        for (int i = 0; i < startNode.getArity(); i++) {
            populateNodes(startNode.getChild(i), startNode);
        }
    }

    private INode replaceAncestors(INode ancestor, INode oldNode, INode newNode) {
        INode newRoot;
        if (ancestor == null) {
            newRoot = newNode;
        } else {
            INode[] newAncestorChildren = new INode[ancestor.getArity()];
            for (int i = 0; i < ancestor.getArity(); i++) {
                if (ancestor.getChild(i) == oldNode) {
                    newAncestorChildren[i] = newNode;
                } else {
                    newAncestorChildren[i] = ancestor.getChild(i);
                }
            }
            INode newAncestor = ancestor.copy(newAncestorChildren);
            newRoot = replaceAncestors(ancestors.get(ancestor), ancestor, newAncestor);
        }
        return newRoot;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void main(String[] args) {
        INode[] functions = new INode[]{new Functions.Times(), new Functions.APlus()};
        INode[] terminals = new INode[]{new Terminals.Input(1), new Terminals.Input(2)};
        NodeCollection nodeCollection = new NodeCollection(functions, terminals);

        RND.initializeTime();
        AACTree tree = AACTree.createRandom(nodeCollection);
        System.out.println(tree);
    }
}
