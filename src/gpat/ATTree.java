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
    LinkedHashSet<ATNode> nodeGenes;
    List<ATNode> nodeGeneList;
    LinkedHashSet<ATLink> linkGenes;

    int maxNodeId = 0;

    private ATTree() {
        nodeGenes = new LinkedHashSet<ATNode>();
        nodeGeneList = new ArrayList<ATNode>();
        linkGenes = new LinkedHashSet<ATLink>();
    }

    private void addNode(ATNode node) {
        nodeGenes.add(node);
        nodeGeneList.add(node);
    }

    public static ATTree createMinimalSubstrate(ATNodeCollection nodeCollection) {
        ATTree tree = new ATTree();
        ATNode rootNode = new ATFunctions.Plus(++tree.maxNodeId, 1);
        tree.addNode(rootNode);
        return tree;
    }

    public void mutateAddLink(ATNodeCollection nodeCollection) {
        ATNode from = nodeCollection.randomTerminal();
        ATNode to = RND.randomChoice(nodeGeneList);
        ATLink link = new ATLink(from, to, 1L);
        linkGenes.add(link);
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
        ATNode[] functions = new ATNode[]{new ATFunctions.Plus(), new ATFunctions.Plus()};
        ATNode[] terminals = new ATNode[]{new ATTerminals.Constant(1.0), new ATTerminals.Constant(2.0)};
        ATNodeCollection nodeCollection = new ATNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        ATTree tree = ATTree.createMinimalSubstrate(nodeCollection);
        System.out.println(tree);
        for (int i = 0; i < 4; i++) {
            tree.mutateAddLink(nodeCollection);
            System.out.println(tree);
        }
    }
}
