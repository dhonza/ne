package gp;

import common.RND;
import gp.terminals.Random;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 6:21:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tree {
    private Node root;

    transient private List<Node> nodes = new ArrayList<Node>();
    transient private Map<Node, Node> ancestors = new HashMap<Node, Node>();

    public Node getRoot() {
        return root;
    }

    public static Tree createRandom(NodeCollection nodeCollection) {
        Tree tree = new Tree();
        tree.root = createRandomSubtree(nodeCollection, 0);
        return tree;
    }

    private static Node createRandomSubtree(NodeCollection nodeCollection, int startDepth) {
        Node node;
        Node choice;
        if (startDepth < GP.MAX_DEPTH) {
            choice = nodeCollection.getRandomOfAll();
        } else {
            choice = nodeCollection.getRandomTerminal();
        }
        int arity = choice.getArity();
        Node[] children = new Node[arity];
        for (int i = 0; i < arity; i++) {
            children[i] = createRandomSubtree(nodeCollection, startDepth + 1);
        }

        node = choice.create(startDepth, children);

        return node;
    }

    public double evaluate() {
        return root.evaluate();
    }

    public Tree mutateNode(NodeCollection nodeCollection) {
        nodes.clear();
        ancestors.clear();

        populateNodes(root, null);
        int mutationPoint = RND.getInt(0, nodes.size() - 1);

        Node mutatedNode = nodes.get(mutationPoint);
        Node mutatedNodeAncestor = ancestors.get(mutatedNode);
        Node prototype = nodeCollection.getRandomWithArity(mutatedNode.getArity());

        if (prototype.getClass() == mutatedNode.getClass()) {//no change at all
            return this;
        }

        Node newNode = prototype.create(mutatedNode.getDepth(), mutatedNode.getChildren());

        Tree mutated = new Tree();
        mutated.root = replaceAncestors(mutatedNodeAncestor, mutatedNode, newNode);
        return mutated;
    }

    public Tree mutateSubtree(NodeCollection nodeCollection) {
//        System.out.println("-------------------");
//        System.out.println(this);

        nodes.clear();
        ancestors.clear();

        populateNodes(root, null);
        int mutationPoint = RND.getInt(0, nodes.size() - 1);

        Node mutatedNode = nodes.get(mutationPoint);
        Node mutatedNodeAncestor = ancestors.get(mutatedNode);
        Node newSubtree;
        if (mutatedNode instanceof Random && RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
            newSubtree = ((Random) mutatedNode).localMutate();
        } else {
            newSubtree = createRandomSubtree(nodeCollection, mutatedNode.getDepth());
        }
//        System.out.println("(" + mutatedNode + " -> " + newSubtree + ")");

        Tree mutated = new Tree();
        mutated.root = replaceAncestors(mutatedNodeAncestor, mutatedNode, newSubtree);
//        System.out.println(mutated);
//        System.out.println("-------------------");
        return mutated;
    }

    private void populateNodes(Node startNode, Node ancestor) {
        nodes.add(startNode);
        ancestors.put(startNode, ancestor);
        for (Node child : startNode.getChildren()) {
            populateNodes(child, startNode);
        }
    }

    private Node replaceAncestors(Node ancestor, Node oldNode, Node newNode) {
        Node newRoot;
        if (ancestor == null) {
            newRoot = newNode;
        } else {
            Node[] newAncestorChildren = ancestor.getChildren();
            for (int i = 0; i < newAncestorChildren.length; i++) {
                if (newAncestorChildren[i] == oldNode) {
                    newAncestorChildren[i] = newNode;
                    break;
                }
            }
            Node newAncestor = ancestor.copy(newAncestorChildren);
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

    private void populateInnovations(Set<Long> innovationList, Node startNode) {
        innovationList.add(startNode.getInnovation());
        for (Node child : startNode.getChildren()) {
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
}
