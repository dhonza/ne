package gp;

import common.RND;
import common.mathematica.MathematicaUtils;
import gp.terminals.ITerminal;
import gp.terminals.Random;
import gpat.ATNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private NodeCollection nodeCollection;

    transient private List<INode> nodes = new ArrayList<INode>();
    transient private Map<INode, INode> ancestors = new HashMap<INode, INode>();

    public INode getRoot() {
        return root;
    }

    public String getOrigin() {
        return origin;
    }

    public Tree(NodeCollection nodeCollection) {
        this.nodeCollection = nodeCollection;
    }

    public static Tree createRandom(NodeCollection nodeCollection) {
        Tree tree = new Tree(nodeCollection);
        if (GP.FULL_INIT) {
            tree.root = createRandomSubtreeFull(nodeCollection, 0);
        } else {
        tree.root = createRandomSubtree(nodeCollection, 0);
        }
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

    private static INode createRandomSubtreeFull(NodeCollection nodeCollection, int startDepth) {
        INode node;
        INode choice;
        if (startDepth < GP.MAX_DEPTH) {
            choice = nodeCollection.getRandomFunction();
        } else {
            choice = nodeCollection.getRandomTerminal();
        }
        int arity = choice.getArity();
        INode[] children = new Node[arity];
        for (int i = 0; i < arity; i++) {
            children[i] = createRandomSubtreeFull(nodeCollection, startDepth + 1);
        }

        node = choice.create(startDepth, children);

        return node;
    }

    public double evaluate(TreeInputs treeInputs) {
        return root.evaluate(treeInputs);
    }

    public Tree copy() {
        clear();

        Tree mutated = new Tree(nodeCollection);
        mutated.origin = "ELITE";
        mutated.root = root.copySubtree();
        return mutated;
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

        Tree mutated = new Tree(nodeCollection);
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

        Tree mutated = new Tree(nodeCollection);

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

    @Override
    public String toString() {
        return root.toString();
    }

    public String toMathematicaExpression() {
        return root.toMathematicaExpression();
    }

    public String toMathematicaExpressionFull() {
        StringBuffer b = new StringBuffer();
        StringBuffer l = new StringBuffer();
        b.append("{NODES->{");
        populateNodes(root, null);


        List<INode> allNodes = new ArrayList<INode>();
//        for (int i = 0; i < nodeCollection.getTerminals().length; i++) {
//            if (!(nodeCollection.getTerminals()[i] instanceof Random)) {
//                allNodes.add(nodeCollection.getTerminals()[i]);
//            }
//        }

        for (int i = 0; i < nodes.size(); i++) {
//            if (!(nodes.get(i) instanceof ITerminal) || (nodes.get(i) instanceof Random)) {
            allNodes.add(nodes.get(i));
//            }
        }

        Map<INode, Integer> nodeToId = new HashMap<INode, Integer>();
        for (int i = 0, allNodesSize = allNodes.size(); i < allNodesSize; i++) {
            INode node = allNodes.get(i);
            nodeToId.put(node, i);
        }

        int rootId = 0;
        for (int i = 0, allNodesSize = allNodes.size(); i < allNodesSize; i++) {
            INode node = allNodes.get(i);
            b.append("{");
            b.append(i + ",");
            b.append("\"" + ((node instanceof ITerminal) ? node.toMathematicaExpression() : node.getName()) + "\"");
            b.append("}");

            if (node == root) {
                rootId = i;
            }

            if (i < allNodesSize - 1) {
                b.append(",");
            }
            for (int j = 0; j < node.getArity(); j++) {
                l.append("{");
                if (nodeToId.get(node.getChild(j)) == null) {
                    System.out.println("node = " + node);
                    System.out.println("nodeToId = " + nodeToId.get(node.getChild(j)));
                }
                int fromId = nodeToId.get(node.getChild(j));
                int toId = nodeToId.get(node);
                double weight = 1;
                boolean active = false;
                l.append(fromId + "->" + toId + "," + MathematicaUtils.toMathematica(weight) + "," + MathematicaUtils.toMathematica(active));
                l.append("}");
                l.append(",");
            }

        }
        b.append("},LINKS->{");
        if (l.length() > 0) {
            b.append(l.substring(0, l.length() - 1));
        }
        b.append("},ROOT->" + rootId);
        b.append(",DEPTH->" + getDepth());
        b.append("}");
        return b.toString();
    }

    public String toMathematicaExpressionSizes() {
        StringBuffer b = new StringBuffer();
        b.append("{NODES->");
        b.append(getNumOfNodes());
        b.append(",CONSTANTS->");
        b.append(getNumOfConstants());
        b.append(",DEPTH->");
        b.append(getDepth());
        b.append("}");
        return b.toString();
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

    public double getAverageArity() {
        //TODO slow!
        double aritySum = root.computeAritySum();
        int nodesMinusLeaves = getNumOfNodes() - getNumOfLeaves();
        if (nodesMinusLeaves == 0) {
            return 0.0;
        }
        return aritySum / nodesMinusLeaves;
    }

    public int getNumOfInputs() {
        return nodeCollection.getNumOfInputs();
    }

    public int getDepth() {
        return root.computeDepth();
    }

    public int getNumOfConstants() {
        return root.computeConstants();
    }

    public int getNumOfLeaves() {
        return root.computeLeaves();
    }

    public int getNumOfNodes() {
        return root.computeNodes();
    }
}
