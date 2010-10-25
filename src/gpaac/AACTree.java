package gpaac;

import common.RND;
import gp.GP;
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

    private List<String> origin = new ArrayList<String>();

    private static class AncestorInfo {
        private AncestorInfo(INode ancestor, int childIdx) {
            this.ancestor = ancestor;
            this.childIdx = childIdx;
        }

        public INode ancestor;
        public int childIdx;
    }

    transient private List<INode> nodes = null;
    transient private List<IArbitraryArityNode> arbitraryArityNodes = null;
    transient private List<INode> randomNodes = null;
    transient private Map<INode, AncestorInfo> ancestors = null;


    public List<String> getOrigin() {
        return origin;
    }

    public void resetOrigin() {
        origin.clear();
    }

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

    public double distance(AACTree other) {
        //TODO implement!!!
        return 0.0;
    }

    public AACTree mutateNode(NodeCollection nodeCollection) {
        AACTree mutated = copy();

        int mutationPoint = RND.getInt(0, mutated.nodes.size() - 1);

        INode mutatedNode = mutated.nodes.get(mutationPoint);
        INode mutatedNodeAncestor = mutated.ancestors.get(mutatedNode).ancestor;
        int mutatedNodeAncestorChildIdx = mutated.ancestors.get(mutatedNode).childIdx;
        INode prototype = nodeCollection.getRandomWithArity(mutatedNode.getArity());

        if (prototype.getClass() == mutatedNode.getClass()) {//no change at all
            this.origin.add("NO");
            return mutated;
        }

        INode newNode = prototype.create(mutatedNode.getDepth(), mutatedNode.getChildren());

        if (mutatedNodeAncestor == null) {
            mutated.root = newNode;
        } else {
            mutatedNodeAncestor.setChild(mutatedNodeAncestorChildIdx, newNode);
        }
        mutated.origin.add("NODE");
        return mutated;
    }

    public AACTree mutateSubtree(NodeCollection nodeCollection) {
        AACTree mutated = copy();

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
        mutated.origin.add("SUBTREE");
        return mutated;
    }

    public AACTree mutateAddChild(NodeCollection nodeCollection) {
        AACTree mutated = copy();
        return mutated;
    }

    public AACTree mutateConstants() {
        AACTree mutated = copy();
        for (IArbitraryArityNode node : mutated.arbitraryArityNodes) {
            for (int i = 0; i < node.getNumOfConstants(); i++) {
                if (!node.isLockedConstants(i) &&
                        RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
                    node.setConstant(i, node.getConstants(i) +
                            GP.MUTATION_CAUCHY_POWER * RND.getCauchy());
                }
            }
        }
        for (INode node : mutated.randomNodes) {
            if (RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
                Terminals.Random randomNode = (Terminals.Random) node;
                randomNode.setValue(randomNode.getValue() +
                        GP.MUTATION_CAUCHY_POWER * RND.getCauchy());
            }
        }
        mutated.origin.add("CAUCHY");
        return mutated;
    }

    public AACTree mutateReplaceConstants() {
        AACTree mutated = copy();
        for (IArbitraryArityNode node : mutated.arbitraryArityNodes) {
            for (int i = 0; i < node.getNumOfConstants(); i++) {
                if (!node.isLockedConstants(i) &&
                        RND.getDouble() < GPAAC.MUTATION_REPLACE_CONSTANTS) {
                    node.setConstant(i,
                            RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE));
                }
            }
        }
        for (INode node : mutated.randomNodes) {
            if (RND.getDouble() < GPAAC.MUTATION_REPLACE_CONSTANTS) {
                Terminals.Random randomNode = (Terminals.Random) node;
                randomNode.setValue(RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE));
            }
        }
        mutated.origin.add("REPLACE");
        return mutated;
    }

    public AACTree mutateSwitchConstantLock() {
        AACTree mutated = copy();
        for (IArbitraryArityNode node : mutated.arbitraryArityNodes) {
            for (int i = 0; i < node.getNumOfConstants(); i++) {
                if (RND.getDouble() < GPAAC.MUTATION_SWITCH_CONSTANT_LOCK) {
                    node.setLockedConstants(i, !node.isLockedConstants(i));
                    if (node.isLockedConstants(i)) {
//                        node.setConstant(i, 1.0);
                    }
                }
            }
        }
        mutated.origin.add("LOCK");
        return mutated;
    }

    private void populateNodes() {
        nodes = new ArrayList<INode>();
        arbitraryArityNodes = new ArrayList<IArbitraryArityNode>();
        randomNodes = new ArrayList<INode>();
        ancestors = new HashMap<INode, AncestorInfo>();
        populateNodes(root, null, 0);
    }

    private void populateNodes(INode startNode, INode ancestor, int childIdx) {
        nodes.add(startNode);
        if (startNode instanceof IArbitraryArityNode) {
            arbitraryArityNodes.add((IArbitraryArityNode) startNode);
        }
        if (startNode instanceof Terminals.Random) {
            randomNodes.add(startNode);
        }
        ancestors.put(startNode, new AncestorInfo(ancestor, childIdx));
        for (int i = 0; i < startNode.getArity(); i++) {
            populateNodes(startNode.getChild(i), startNode, i);
        }
    }

    private AACTree copy() {
        AACTree copy = new AACTree();
        copy.root = root.copySubtree();
        copy.origin = new ArrayList<String>(origin);
        copy.populateNodes();
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
