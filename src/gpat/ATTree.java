package gpat;

import common.RND;

import java.util.ArrayList;
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

    private ATNode root;
    //TODO HashSet should suffice
    private LinkedHashSet<ATNode> nodeGenes;
    private List<ATNode> nodeGeneList;
    private LinkedHashSet<ATLink> linkGenes;
    private List<ATLink> linkGenesList;
    private List<ATNode> terminals;

    int maxNodeId = 0;

    private ATTree(ATNodeCollection nodeCollection) {
        this.nodeCollection = nodeCollection;
        nodeGenes = new LinkedHashSet<ATNode>();
        nodeGeneList = new ArrayList<ATNode>();
        linkGenes = new LinkedHashSet<ATLink>();
        linkGenesList = new ArrayList<ATLink>();
        terminals = new ArrayList<ATNode>();
        for (ATNode terminal : nodeCollection.terminals) {
            ATNode node = terminal.create(++maxNodeId, 0);
            terminals.add(node);
        }
    }

    private void addNode(ATNode node) {
        nodeGenes.add(node);
        nodeGeneList.add(node);
    }

    public static ATTree createMinimalSubstrate(ATNodeCollection nodeCollection) {
        ATTree tree = new ATTree(nodeCollection);
        tree.root = new ATFunctions.Plus(++tree.maxNodeId, 1);
        tree.addNode(tree.root);
        return tree;
    }

    public void mutateAddLink() {
        ATLink link;
        //TODO or list of possible Links?
        for (int i = 0; i < 10; i++) {
            ATNode from = RND.randomChoice(terminals);
            ATNode to = RND.randomChoice(nodeGeneList);
            link = new ATLink(from, to, 1L);
            if (!linkGenes.contains(link)) {
                from.setParent(to);
                to.addChild(from);
                linkGenes.add(link);
                linkGenesList.add(link);
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
        nodeGenes.add(node);
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
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(" NODES:\n");
        for (ATNode nodeGene : nodeGenes) {
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
        for (int i = 0; i < 5; i++) {
            tree.mutateAddLink();
            System.out.println(tree);
            tree.mutateAddNode();
            System.out.println(tree);
        }
    }
}
