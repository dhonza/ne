package gpaac;

import common.RND;
import gp.INode;
import gp.NodeCollection;
import gp.TreeInputs;

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

    private static class AncestorInfo {
        private AncestorInfo(INode ancestor, int childIdx) {
            this.ancestor = ancestor;
            this.childIdx = childIdx;
        }

        public INode ancestor;
        public int childIdx;
    }

    transient private List<INode> nodes = new ArrayList<INode>();
    transient private Map<INode, AncestorInfo> ancestors = new HashMap<INode, AncestorInfo>();

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

    public double evaluate(TreeInputs treeInputs) {
        return root.evaluate(treeInputs);
    }

    public AACTree mutateNode(NodeCollection nodeCollection) {
        AACTree mutated = copy();
        mutated.populateNodes();

        int mutationPoint = RND.getInt(0, mutated.nodes.size() - 1);

        INode mutatedNode = mutated.nodes.get(mutationPoint);
        INode mutatedNodeAncestor = mutated.ancestors.get(mutatedNode).ancestor;
        int mutatedNodeAncestorChildIdx = mutated.ancestors.get(mutatedNode).childIdx;
        INode prototype = nodeCollection.getRandomWithArity(mutatedNode.getArity());

        if (prototype.getClass() == mutatedNode.getClass()) {//no change at all
            return this;
        }

        INode newNode = prototype.create(mutatedNode.getDepth(), mutatedNode.getChildren());

        if (mutatedNodeAncestor == null) {
            mutated.root = newNode;
        } else {
            mutatedNodeAncestor.setChild(mutatedNodeAncestorChildIdx, newNode);
        }
        return mutated;
    }

    public AACTree mutateSubtree(NodeCollection nodeCollection) {
        AACTree mutated = copy();
        mutated.populateNodes();

        int mutationPoint = RND.getInt(0, mutated.nodes.size() - 1);

        INode mutatedNode = mutated.nodes.get(mutationPoint);
        INode mutatedNodeAncestor = mutated.ancestors.get(mutatedNode).ancestor;
        int mutatedNodeAncestorChildIdx = mutated.ancestors.get(mutatedNode).childIdx;
        INode newSubtree = createRandomSubtree(nodeCollection, mutatedNode.getDepth());

        if (mutatedNodeAncestor == null) {
            mutated.root = newSubtree;
        } else {
            mutatedNodeAncestor.setChild(mutatedNodeAncestorChildIdx, newSubtree);
        }
        return mutated;
    }

    private void populateNodes() {
        nodes.clear();
        ancestors.clear();
        populateNodes(root, null, 0);
    }

    private void populateNodes(INode startNode, INode ancestor, int childIdx) {
        nodes.add(startNode);
        ancestors.put(startNode, new AncestorInfo(ancestor, childIdx));
        for (int i = 0; i < startNode.getArity(); i++) {
            populateNodes(startNode.getChild(i), startNode, i);
        }
    }

    private AACTree copy() {
        AACTree copy = new AACTree();
        copy.root = root.copySubtree();
        return copy;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void main(String[] args) {
        INode[] functions = new INode[]{new Functions.Times(), new Functions.APlus()};
        INode[] terminals = new INode[]{};
        AACNodeCollection nodeCollection = new AACNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        AACTree tree = AACTree.createRandom(nodeCollection);
        System.out.println(tree);
        AACTree tree2 = tree.mutateNode(nodeCollection);
        System.out.println(tree2);
        AACTree tree3 = tree.mutateSubtree(nodeCollection);
        System.out.println(tree3);

    }
}
