package gpat;

import common.RND;
import gp.INode;
import gp.NodeCollection;
import gp.terminals.ITerminal;
import gpaac.AACNodeCollection;
import gpaac.Functions;
import gpaac.IArbitraryArityNode;
import gpaac.Terminals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 1, 2010
 * Time: 6:36:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTree {
    IArbitraryArityNode root;

    List<IArbitraryArityNode> nodeGenes;
    List<ATLink> possibleLinks;

    public static ATTree createMinimalSubstrate(NodeCollection nodeCollection) {
        ATTree tree = new ATTree();
        tree.root = new Functions.APlus(1, 1L, new INode[0]);
        tree.nodeGenes = new ArrayList<IArbitraryArityNode>();
        tree.nodeGenes.add(tree.root);
        tree.possibleLinks = new ArrayList<ATLink>();
        for (INode from : nodeCollection.getTerminals()) {
            ATLink link = new ATLink(from, tree.root, 1L);
            tree.possibleLinks.add(link);
        }
        return tree;
    }

    public ATTree mutateAddLink(NodeCollection nodeCollection) {
        ATTree mutated = copy();
        INode from = nodeCollection.getRandomTerminal();
        IArbitraryArityNode[] possibleTo = mutated.nodeGenes.toArray(new IArbitraryArityNode[0]);
        IArbitraryArityNode to = RND.randomChoice(possibleTo);
        final int arity = to.getArity();
        to.setChild(arity, from);
        to.setConstant(arity, 1.0);
        to.setLockedConstants(arity, true);
        return mutated;
    }

    private ATTree copy() {
        ATTree copy = new ATTree();
        copy.root = (IArbitraryArityNode) root.copySubtree();
        copy.populateNodes();
        return copy;
    }

    private void populateNodes() {
        nodeGenes = new ArrayList<IArbitraryArityNode>();
        populateNodes(root);
    }

    private void populateNodes(IArbitraryArityNode startNode) {
        nodeGenes.add(startNode);
        for (int i = 0; i < startNode.getArity(); i++) {
            final INode child = startNode.getChild(i);
            if (!(child instanceof ITerminal)) {
                populateNodes((IArbitraryArityNode) child);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(root.toString()).append("\n N");
        for (IArbitraryArityNode nodeGene : nodeGenes) {
            builder.append(nodeGene).append('\n');
        }
        for (ATLink possibleLink : possibleLinks) {
            builder.append(possibleLink).append('\n');
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        INode[] functions = new INode[]{new Functions.APlus(), new Functions.ASin()};
        INode[] terminals = new INode[]{new Terminals.Constant(1.0), new Terminals.Constant(2.0)};
        AACNodeCollection nodeCollection = new AACNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        ATTree tree = ATTree.createMinimalSubstrate(nodeCollection);
        System.out.println(tree);
        ATTree tree2 = tree.mutateAddLink(nodeCollection);
        System.out.println(tree2);
        for (int i = 0; i < 4; i++) {
            ATTree tree3 = tree2.mutateAddLink(nodeCollection);
            System.out.println(tree3);
        }

    }
}
