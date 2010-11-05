package gpat;

import common.RND;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    final private ATNodeCollection nodeCollection;

    final private List<String> origin;

    private ATNode root;
    //TODO HashSet should suffice
    private LinkedHashMap<Integer, ATNode> nodeGenes;
    private List<ATNode> nodeGeneList;
    private LinkedHashSet<ATLink> linkGenes;
    private List<ATLink> linkGenesList;
    private LinkedHashMap<Integer, ATNode> terminals;
    private List<ATNode> terminalList;

    int maxNodeId = 0;

    private ATTree(ATNodeCollection nodeCollection) {
        this.nodeCollection = nodeCollection;
        nodeGenes = new LinkedHashMap<Integer, ATNode>();
        nodeGeneList = new ArrayList<ATNode>();
        linkGenes = new LinkedHashSet<ATLink>();
        linkGenesList = new ArrayList<ATLink>();
        terminals = new LinkedHashMap<Integer, ATNode>();
        terminalList = new ArrayList<ATNode>();
        origin = new ArrayList<String>();
        for (ATNode terminal : nodeCollection.terminals) {
            ATNode node = terminal.create(++maxNodeId, 0);
            terminals.put(node.getId(), node);
            terminalList.add(node);
        }
    }

    private void addNode(ATNode node) {
        nodeGenes.put(node.getId(), node);
        nodeGeneList.add(node);
    }

    public static ATTree createMinimalSubstrate(ATNodeCollection nodeCollection) {
        ATTree tree = new ATTree(nodeCollection);
        tree.root = new ATFunctions.Plus(++tree.maxNodeId, 1);
        tree.addNode(tree.root);
        tree.origin.add("NEW");
        return tree;
    }

    public void mutateAddLink() {
        ATLink link;
        //TODO or list of possible Links?
        for (int i = 0; i < 10; i++) {
            ATNode from = RND.randomChoice(terminalList);
            ATNode to = RND.randomChoice(nodeGeneList);
            link = new ATLink(from, to, 1L);
            if (!linkGenes.contains(link)) {
                from.setParent(to);
                to.addChild(from);
                linkGenes.add(link);
                linkGenesList.add(link);
                origin.add("ADD_LINK");
                return;
            }
        }
    }

    public void mutateAddNode() {
        if (linkGenesList.size() == 0) {
            return;
        }
        ATLink link = RND.randomChoice(linkGenesList);
        System.out.println("REPLACE LINK: " + link);
        ATNode node = RND.randomChoice(nodeCollection.functions).create(++maxNodeId, -1);
        nodeGenes.put(node.getId(), node);
        nodeGeneList.add(node);

        ATNode from = link.getFrom();
        ATNode to = link.getTo();
        ATLink fromLink = new ATLink(from, node, 1L);
        ATLink toLink = new ATLink(node, to, 1L);
        node.setParent(to);
        node.addChild(from);
        from.setParent(node);
        to.removeChild(from);
        to.addChild(node);
        linkGenes.remove(link);
        linkGenes.add(fromLink);
        linkGenes.add(toLink);
        linkGenesList.remove(link);
        linkGenesList.add(fromLink);
        linkGenesList.add(toLink);
        origin.add("ADD_NODE");
    }

    public void mutateConstants() {
        boolean mutation = false;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                if (!node.isConstantLock(i) &&
                        RND.getDouble() < GPAT.MUTATION_CAUCHY_PROBABILITY) {
                    node.setConstant(i, node.getConstant(i) +
                            GPAT.MUTATION_CAUCHY_POWER * RND.getCauchy());
                    mutation = true;
                }
            }
        }
        if (mutation) {
            origin.add("CONSTANTS");
        }
    }

    public void mutateSwitchConstantLocks() {
        boolean mutation = false;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                if (RND.getDouble() < GPAT.MUTATION_SWITCH_CONSTANT_LOCK) {
                    node.setConstantLock(i, !node.isConstantLock(i));
                    mutation = true;
                }
            }
        }
        if (mutation) {
            origin.add("CONSTANT_LOCKS");
        }
    }

    public ATTree copy() {
        ATTree copy = new ATTree(nodeCollection);
        for (ATNode node : nodeGeneList) {
            ATNode nodeCopy = node.copy();
            copy.nodeGenes.put(nodeCopy.getId(), nodeCopy);
            copy.nodeGeneList.add(nodeCopy);
        }
        copy.root = copy.nodeGenes.get(root.getId());

        for (ATLink link : linkGenesList) {
            ATNode fromNode = copy.terminals.get(link.getFrom().getId());
            if (fromNode == null) {
                fromNode = copy.nodeGenes.get(link.getFrom().getId());
            }
            ATNode toNode = copy.nodeGenes.get(link.getTo().getId());
            fromNode.setParent(toNode);
            toNode.addChild(fromNode);
            ATLink linkCopy = new ATLink(
                    fromNode,
                    toNode,
                    link.getInnovation());
            copy.linkGenes.add(linkCopy);
            copy.linkGenesList.add(linkCopy);
        }

        copy.maxNodeId = maxNodeId;

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" NODES:\n");
        for (ATNode nodeGene : nodeGeneList) {
            builder.append(nodeGene.getId()).append(" ").append(nodeGene.getName()).append('\n');
        }
        builder.append(" LINKS:\n");
        for (ATLink linkGene : linkGenes) {
            builder.append(linkGene).append('\n');
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        ATNode[] functions = new ATNode[]{new ATFunctions.Plus(), new ATFunctions.Times()};
        ATNode[] terminals = new ATNode[]{new ATTerminals.Constant(1.0), new ATTerminals.Constant(2.0)};
        ATNodeCollection nodeCollection = new ATNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        ATTree tree = ATTree.createMinimalSubstrate(nodeCollection);
        System.out.println(tree);
        for (int i = 0; i < 1; i++) {
            tree.mutateAddLink();
            System.out.println(tree);
            tree.mutateAddNode();
            System.out.println(tree);
        }
        ATTree tree2 = tree.copy();
        System.out.println(tree2);
    }
}
