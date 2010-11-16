package gpat;

import common.RND;
import gp.GP;
import gp.TreeInputs;
import gp.terminals.ITerminal;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 1, 2010
 * Time: 6:36:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTree {
    final private ATNodeCollection nodeCollection;
    final private ATInnovationHistory innovationHistory;

    final private List<String> origin;

    private ATNode root;
    //TODO HashSet should suffice

    //stores nodes, uses ids as keys
    private LinkedHashMap<Integer, ATNode> nodeGenes;

    //the same information stored in the list
    private List<ATNode> nodeGeneList;

    //it is possible to have multiple links from single input (terminal)
    //to a given node - this map is used to count them
    //note that ATLink is hashed only using "from" and "to" nodes' ids
    private LinkedHashMap<ATLink, Integer> linkCountMap;

    //list of all links
    private List<ATLink> linkGenesList;

    //map of terminals (inputs), with ids as keys
    private LinkedHashMap<Integer, ATNode> terminals;

    //the same information stored in a list
    private List<ATNode> terminalList;

    //used to count node ids (innovation numbers)
    //starts with id 0 with terminals,
    //ends with the single function from minimal substrate
    private int initialNodeIds = 0;

    private ATTree(ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        this.nodeCollection = nodeCollection;
        this.innovationHistory = innovationHistory;
        nodeGenes = new LinkedHashMap<Integer, ATNode>();
        nodeGeneList = new ArrayList<ATNode>();
        linkCountMap = new LinkedHashMap<ATLink, Integer>();
        linkGenesList = new ArrayList<ATLink>();
        terminals = new LinkedHashMap<Integer, ATNode>();
        terminalList = new ArrayList<ATNode>();
        origin = new ArrayList<String>();

        //terminals have ids assigned started from zero
        for (ATNode terminal : nodeCollection.terminals) {
            ATNode node = terminal.create(initialNodeIds++, 0);
            terminals.put(node.getId(), node);
            terminalList.add(node);
        }
    }

    public List<String> getOrigin() {
        return origin;
    }

    private void addNode(ATNode node) {
        nodeGenes.put(node.getId(), node);
        nodeGeneList.add(node);
    }

    //the minimal substrate has a single function node (plus by default)
    //but without connection from input terminals,
    //this function node has 1 + the id of a last terminal

    public static ATTree createMinimalSubstrate(ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        ATTree tree = new ATTree(nodeCollection, innovationHistory);
        tree.root = new ATFunctions.Plus(tree.initialNodeIds, 1);
        tree.addNode(tree.root);
        tree.origin.add("NEW");
        return tree;
    }

    //check how many connections are already between "from" and "to"
    //return incremented counter increments the counter
    //note, that only links with terminal "from" nodes are
    //added to linkCountMap by mutations

    private int getNextConnectionCount(ATNode from, ATNode to) {
        ATLink prototype = new ATLink(from, to, -1L); //-!L dummy innovation number
        Integer cnt = linkCountMap.get(prototype);
        cnt = cnt == null ? 1 : (cnt + 1); //increment counter
        linkCountMap.put(prototype, cnt); //store it
        return cnt; //return it
    }


    //Inserts "link" to the sorted linkGenesList.

    private void insertLinkGene(ATLink link) {
        //find position for the link
        int pos = Collections.binarySearch(linkGenesList, link);
        if (pos < 0) {//see binarySearch docs
            pos = -pos - 1;
        }
        linkGenesList.add(pos, link);
    }

    public void mutateAddLink() {
        checkInnovationSorted(this);
        //choose a random link to create
        ATLink link;
        //note, that links can start only in terminal (input) nodes
        ATNode from = RND.randomChoice(terminalList);
        ATNode to = RND.randomChoice(nodeGeneList);

        //Check (return counter) if we already have such connection.
        //If such connection does yet not exists -> return 0.
        int cnt = getNextConnectionCount(from, to);

        //Get new link innovation number.
        //Note, that innovation history matches previous appearance
        //only if "from", "to" ids are the same plus the connection
        //counter is the same.
        long innovation = innovationHistory.getLinkInnovation(
                from.getId(), to.getId(), cnt);

        //Finally create the link.
        link = new ATLink(from, to, innovation);

        //Insert link. Link genes must be sorted for later comparisons
        //using innovation numbers.
        insertLinkGene(link);

        //And make corresponding changes in the tree structure.
        from.setParent(to);
        to.addChild(from, innovation);

        origin.add("ADD_LINK");
        checkInnovationSorted(this);
        checkLinkGenesVsNodes(this);
    }

    public void mutateAddNode() {
        //Add node mutation is done by replacing a random link
        //by two links and a node. So we need at least
        //one node.
        if (linkGenesList.size() == 0) {
            return;
        }

        //Get a random link.
        int linkIdx = RND.getIntZero(linkGenesList.size());//we must remember it's index to remove it later
        ATLink link = linkGenesList.get(linkIdx);
        ATNode from = link.getFrom();
        ATNode to = link.getTo();

        //A prototype of a node to add. Just to know what type it will be
        //in advance.
        ATNode nodePrototype = RND.randomChoice(nodeCollection.functions);

        //Get an id (innovation number) for the new node.
        //Note, that we work with trees: once a link is transformed
        //to "fromLink--node--toLink" construct the original link disappears.
        //So, it is sufficient to check only for "from" and "to" ids plus
        //the node type -> there is no need for anything similar to
        // "linkCountMap" used by mutateAddLink().
//        System.out.println("innovationHistory = " + innovationHistory);
        ATInnovationHistory.NodeInnovation innovation =
                innovationHistory.newNodeInnovation(
                        from.getId(),
                        to.getId(),
                        nodePrototype.getClass());

        //If there is already such innovation in the tree, force
        //new innovation numbers

//        boolean notNew = true;//DEBUG

        if (nodeGenes.containsKey(innovation.getNodeId())) {
            innovation = innovationHistory.forceNewNodeInnovation(
                    from.getId(),
                    to.getId(),
                    nodePrototype.getClass());
//            notNew = false;//DEBUG
        }

//        if(notNew && innovation.getToInnovation() <= to.getMaxLinkInnovation()) {//DEBUG
//            System.out.println("Needs sorting");
//        }

        //Now, create the actual node.
        ATNode node = nodePrototype.create(innovation.getNodeId(), -1);

        //Check if the "fromLink" part of the "fromLink--node--toLink" will start
        //in a terminal. If so, we should store it to the innovation history,
        //because there is a possibility of successive mutations creating
        //links parallel to the "fromLink".
        if (from instanceof ITerminal) {
            innovationHistory.storeLinkInnovation(
                    from.getId(),
                    node.getId(),
                    1,//it is the first connection of "from" and "node"
                    innovation.getFromInnovation());
        }

        //Store the node's records.
        addNode(node);

        //Reconnect "node" with "from" and "to".
        node.setParent(to);
        node.addChild(from, innovation.getFromInnovation());
        from.setParent(node);

        //Replace "from" child by the new "node" child,
        //keeping constants and locks.
        to.replaceChild(from, node, link.getInnovation(), innovation.getToInnovation());

        //Correct link counters
        getNextConnectionCount(from, node);
        getNextConnectionCount(node, to);

        //Remove original link from the list.
        linkGenesList.remove(linkIdx);

        //Store new links: "fromLink" and "toLink".
        ATLink fromLink = new ATLink(from, node, innovation.getFromInnovation());
        ATLink toLink = new ATLink(node, to, innovation.getToInnovation());
        //Insert links. Link genes must be sorted for later comparisons
        //using innovation numbers.
        insertLinkGene(fromLink);
        insertLinkGene(toLink);

        origin.add("ADD_NODE");
        checkInnovationSorted(this);
        checkLinkGenesVsNodes(this);
    }

    public void mutateConstants() {
        boolean mutation = false;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                if (!node.isConstantLock(i) &&
                        RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
                    node.setConstant(i, node.getConstant(i) +
                            GP.MUTATION_CAUCHY_POWER * RND.getCauchy());
                    mutation = true;
                }
            }
        }
        if (mutation) {
            origin.add("CONSTANTS");
        }
    }

    public void elite() {
        origin.add("ELITE");
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
        ATTree copy = new ATTree(nodeCollection, innovationHistory);
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
            toNode.addChild(fromNode, link.getInnovation());
            ATLink linkCopy = new ATLink(
                    fromNode,
                    toNode,
                    link.getInnovation());
            copy.linkGenesList.add(linkCopy);
        }

        for (Map.Entry<ATLink, Integer> entry : linkCountMap.entrySet()) {
            ATLink link = entry.getKey();
            ATNode fromNode = copy.terminals.get(link.getFrom().getId());
            if (fromNode == null) {
                fromNode = copy.nodeGenes.get(link.getFrom().getId());
            }
            ATNode toNode = copy.nodeGenes.get(link.getTo().getId());
            ATLink linkCopy = new ATLink(
                    fromNode,
                    toNode,
                    link.getInnovation() //-1L
            );
            copy.linkCountMap.put(linkCopy, entry.getValue());
        }
        return copy;
    }

    public double evaluate(TreeInputs treeInputs) {
        return root.evaluate(treeInputs);
    }

    public double distance(ATTree other) {
        ATTree a = this;
        ATTree b = other;
        int disjoint = 0, excess;
        int common = 0; // how many genes have the same innovation numbers
        double wDif = 0.0; // weight difference
        double actDif = 0.0; // activation function difference

        int i = 0, j = 0;
        ATLink il, jl;

        int iLen = a.linkGenesList.size(), jLen = b.linkGenesList.size();

        while ((i < iLen) && (j < jLen)) {
            il = a.linkGenesList.get(i);
            jl = b.linkGenesList.get(j);
            if (il.getInnovation() == jl.getInnovation()) {
                i++;
                j++;
                common++;
                int iIdx = il.getTo().getIdxForInnovation(il.getInnovation());
                int jIdx = jl.getTo().getIdxForInnovation(jl.getInnovation());
                if (jIdx == -1) {
                    System.out.println("");
                }
                double iConstant = il.getTo().getConstant(iIdx);
                double jConstant = jl.getTo().getConstant(jIdx);
                wDif += Math.abs(iConstant - jConstant);
            } else if (il.getInnovation() > jl.getInnovation()) {
                disjoint++;
                j++;
            } else {
                disjoint++;
                i++;
            }
        }
        if (i < iLen) {
            excess = iLen - i;
        } else {
            excess = jLen - j;
        }
        //TODO different for large genes: see neat.GenomeDistance
        //TODO involve locks?

        double weights = (GPAT.DISTANCE_C3 * wDif) / common;
        if (Double.isNaN(weights)) {
            weights = 0.0;
        }
        double distance = GPAT.DISTANCE_C1 * excess + GPAT.DISTANCE_C2 * disjoint + weights;
        return distance;
    }

    /**
     * Just debug checking.
     */
    private static void checkInnovationSorted(ATTree tree) {
        //DEBUG
        for (int i = 1; i < tree.linkGenesList.size(); i++) {
            ATLink link = tree.linkGenesList.get(i);
            ATLink prevLink = tree.linkGenesList.get(i - 1);
            if (link.getInnovation() <= prevLink.getInnovation()) {
                throw new IllegalStateException("Unsorted link lists.\n" + tree);
            }
        }
    }

    private static void checkLinkGenesVsNodes(ATTree tree) {
        //DEBUG
        for (int i = 0; i < tree.linkGenesList.size(); i++) {
            ATLink link = tree.linkGenesList.get(i);
            long innovation = link.getInnovation();
            int pos = link.getTo().getIdxForInnovation(innovation);
            if (pos < 0) {
                throw new IllegalStateException("linkGenesList does not match node id : " +
                        link.getTo().getId());
            }
        }
    }

    @Override
    public String toString() {
        ///*
        StringBuilder builder = new StringBuilder(" NODES:\n");
        for (ATNode nodeGene : nodeGeneList) {
            builder.append(nodeGene.getId()).append(" ").append(nodeGene.getName()).append('\n');
        }
        builder.append(" LINKS:\n");
        for (ATLink linkGene : linkGenesList) {
            builder.append(linkGene).append('\n');
        }
        builder.append(" EXPRESSION:\n").append(root.toMathematicaExpression()).append('\n');
        return builder.toString();
        //*/
//        return root.toMathematicaExpression();
    }

    public static void main(String[] args) {
        ATNode[] functions = new ATNode[]{new ATFunctions.Plus(), new ATFunctions.Times()};
        ATNode[] terminals = new ATNode[]{new ATTerminals.Constant(1.0), new ATTerminals.Constant(2.0)};
        ATNodeCollection nodeCollection = new ATNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        ATTree tree = ATTree.createMinimalSubstrate(nodeCollection, new ATInnovationHistory(terminals.length + 1));
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
