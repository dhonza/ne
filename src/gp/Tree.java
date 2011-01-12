package gp;

import common.RND;
import gp.terminals.Random;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 6:21:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tree implements Serializable {
    private INode root;

    private String origin = "NEW";

    transient private List<INode> nodes = new ArrayList<INode>();
    transient private Map<INode, INode> ancestors = new HashMap<INode, INode>();

    public INode getRoot() {
        return root;
    }

    public String getOrigin() {
        return origin;
    }

    public static Tree createRandom(NodeCollection nodeCollection) {
        Tree tree = new Tree();
        tree.root = createRandomSubtree(nodeCollection, 0);
        return tree;
    }

    private static INode createRandomSubtree(NodeCollection nodeCollection, int startDepth) {
        INode node;
        INode choice;
        if (startDepth < GP.MAX_DEPTH) {
            choice = nodeCollection.getRandomOfAll();
        } else {
            choice = nodeCollection.getRandomTerminal();
        }
        int arity = choice.getArity();
        INode[] children = new Node[arity];
        for (int i = 0; i < arity; i++) {
            children[i] = createRandomSubtree(nodeCollection, startDepth + 1);
        }

        node = choice.create(startDepth, children);

        return node;
    }

    public double evaluate(TreeInputs treeInputs) {
        return root.evaluate(treeInputs);
    }

    public Tree mutateNode(NodeCollection nodeCollection) {
        clear();

        populateNodes(root, null);
        int mutationPoint = RND.getInt(0, nodes.size() - 1);

        INode mutatedNode = nodes.get(mutationPoint);
        INode mutatedNodeAncestor = ancestors.get(mutatedNode);
        INode prototype = nodeCollection.getRandomWithArity(mutatedNode.getArity());

        if (prototype.getClass() == mutatedNode.getClass()) {//no change at all
            this.origin = "NO";
            return this;
        }

        INode newNode = prototype.create(mutatedNode.getDepth(), mutatedNode.getChildren());

        Tree mutated = new Tree();
        mutated.origin = "NODE";
        mutated.root = replaceAncestors(mutatedNodeAncestor, mutatedNode, newNode);
        return mutated;
    }

    public Tree mutateSubtree(NodeCollection nodeCollection) {
        clear();

        populateNodes(root, null);
        int mutationPoint = RND.getInt(0, nodes.size() - 1);

        INode mutatedNode = nodes.get(mutationPoint);
        INode mutatedNodeAncestor = ancestors.get(mutatedNode);
        INode newSubtree;

        Tree mutated = new Tree();

        if (mutatedNode instanceof Random && RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
            mutated.origin = "CAUCHY";
            newSubtree = ((Random) mutatedNode).localMutate();
        } else {
            mutated.origin = "SUBTREE";
            newSubtree = createRandomSubtree(nodeCollection, mutatedNode.getDepth());
        }
//        System.out.println("(" + mutatedNode + " -> " + newSubtree + ")");

        mutated.root = replaceAncestors(mutatedNodeAncestor, mutatedNode, newSubtree);
//        System.out.println(mutated);
//        System.out.println("-------------------");
        return mutated;
    }

    private void populateNodes(INode startNode, INode ancestor) {
        nodes.add(startNode);
        ancestors.put(startNode, ancestor);
        for (INode child : startNode.getChildren()) {
            populateNodes(child, startNode);
        }
    }

    private INode replaceAncestors(INode ancestor, INode oldNode, INode newNode) {
        INode newRoot;
        if (ancestor == null) {
            newRoot = newNode;
        } else {
            INode[] newAncestorChildren = ancestor.getChildren();
            for (int i = 0; i < newAncestorChildren.length; i++) {
                if (newAncestorChildren[i] == oldNode) {
                    newAncestorChildren[i] = newNode;
                    break;
                }
            }
            INode newAncestor = ancestor.copy(newAncestorChildren);
            newRoot = replaceAncestors(ancestors.get(ancestor), ancestor, newAncestor);
        }
        return newRoot;
    }

    public double distance(Tree other) {
        Set<Long> innovationsA = new HashSet<Long>();
        populateInnovations(innovationsA, this.root);
        Set<Long> innovationsB = new HashSet<Long>();
        populateInnovations(innovationsB, other.root);
        double mean = (innovationsA.size() + innovationsB.size()) / 2.0;
        innovationsA.retainAll(innovationsB);
        return 1.0 - (double) innovationsA.size() / mean;
    }

    private void populateInnovations(Set<Long> innovationList, INode startNode) {
        innovationList.add(startNode.getInnovation());
        for (INode child : startNode.getChildren()) {
            populateInnovations(innovationList, child);
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public String toMathematicaExpression() {
        return root.toMathematicaExpression();
    }

    public String innovationToString() {
        return root.innovationToString();
    }

    private void clear() {
        if (nodes != null) {
            nodes.clear();
        } else {
            nodes = new ArrayList<INode>();
        }
        if (ancestors != null) {
            ancestors.clear();
        } else {
            ancestors = new HashMap<INode, INode>();
        }
    }
}
