package gpat;

import common.RND;
import gp.TreeInputs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
    final ATInnovationHistory innovationHistory;

    final private List<String> origin;

    private ATNode root;

    //stores nodes, uses ids as keys
    private LinkedHashMap<Integer, ATNode> nodeGenes;

    //the same information stored in the list
    private List<ATNode> nodeGeneList;

    //list of all links
    private List<ATLinkGene> linkGenesList;

    //map of terminals (inputs), with ids as keys
    private LinkedHashMap<Integer, ATNode> terminals;

    //the same information stored in a list
    private List<ATNode> terminalList;

    //used to count node ids (innovation numbers)
    //starts with id 0 from terminals,
    //ends with a single function of a minimal substrate
    private int initialNodeIds = 0;
    private int numOfConstants = 0;

    private ATTree(ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        this.nodeCollection = nodeCollection;
        this.innovationHistory = innovationHistory;
        nodeGenes = new LinkedHashMap<Integer, ATNode>();
        nodeGeneList = new ArrayList<ATNode>();

        linkGenesList = new ArrayList<ATLinkGene>();
        terminals = new LinkedHashMap<Integer, ATNode>();
        terminalList = new ArrayList<ATNode>();
        origin = new ArrayList<String>();

        //terminals have ids assigned starting from zero
        for (ATNodeImpl terminal : nodeCollection.terminals) {
            ATNode node = new ATNode(initialNodeIds++, terminal, 0);
            terminals.put(node.getId(), node);
            terminalList.add(node);
        }
    }

    public List<String> getOrigin() {
        return origin;
    }

    public double getAverageArity() {
        //TODO slow!
        int numOfInnerNodes = getNumOfNodes() - getNumOfLeaves();
        double aritySum = root.computeAritySum();
        if (numOfInnerNodes == 0) {
            return 0.0;
        } else {
            return aritySum / numOfInnerNodes;
        }
    }

    public int getNumOfConstants() {
        //this returns the number of all constants including those not used (hasConstants() -> false).
//        return numOfConstants;
        //this returns only used constants
        return root.computeUsedConstants();
    }

    public int getDepth() {
        return root.computeDepth();
    }

    public int getNumOfLeaves() {
        return root.computeLeaves();
    }

    public int getNumOfNodes() {
        return root.computeNodes();
    }

    private void addNode(ATNode node) {
        nodeGeneList.add(node);
        nodeGenes.put(node.getId(), node);
    }

    //Inserts "link" to the sorted linkGenesList.
    private void insertLinkGene(ATLinkGene link) {
        //find position for the link
        int pos = Collections.binarySearch(linkGenesList, link);
        if (pos < 0) {//see binarySearch docs
            pos = -pos - 1;
        }
        linkGenesList.add(pos, link);
    }


    //the minimal substrate has a single function node (plus by default)
    //but without connection from input terminals,
    //this function node has 1 + the id of a last terminal
    public static ATTree createMinimalSubstrate(ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        ATTree tree = new ATTree(nodeCollection, innovationHistory);
//        tree.root = new ATNode(tree.initialNodeIds, nodeCollection.randomFunction(), nodeCollection.terminals.length);
        tree.root = new ATNode(tree.initialNodeIds, new ATFunctions.Plus(), nodeCollection.terminals.length);
        tree.addNode(tree.root);
        tree.origin.add("NEW");
        return tree;
    }

    public static ATTree createMinimalSubstrateWithInputs(ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        ATTree tree = new ATTree(nodeCollection, innovationHistory);
        tree.root = new ATNode(tree.initialNodeIds, nodeCollection.randomFunction(), nodeCollection.terminals.length);
        tree.addNode(tree.root);
//        tree.mutateAddLink();
//        tree.mutateAddLink();
//        tree.mutateInsertRoot();
//        tree.mutateAddLink();
//        tree.mutateAddLink();
//        tree.mutateConstants();
        tree.origin.add("NEW");
        return tree;
    }

    public void mutateAddLink() {
        //choose a random link to be created
        ATRandomLink.FreeLink freeLink = ATRandomLink.getFreeRandomLink(nodeCollection, nodeGeneList);
        if (freeLink == null) {
            origin.add("ADD_LINK_FAILED");
            return;
        }

        //note, that links can start only in terminal (input) nodes
//        int terminalIdx = RND.getIntZero(terminalList.size());
        int terminalIdx = freeLink.getTerminalIdx();
        ATNode from = terminalList.get(terminalIdx);
//        ATNode to = RND.randomChoice(nodeGeneList);
        ATNode to = freeLink.getTo();

        //increment the number of connection from the very same terminal
        to.incTerminalsConnected(terminalIdx);

        //add the child to its parent node
        to.addChild(from);

        //get innovation number
//        long linkInnovationNumber = innovationHistory.getLinkInnovation(from.getId(), to.getId(), countSame);
        long linkInnovationNumber = innovationHistory.getLinkInnovation(from.getId(), to.getId(), to.getTerminalsConnected(terminalIdx));

        ATLinkGene link = new ATLinkGene(from, to, linkInnovationNumber, to.getArity() - 1);

        //Insert link. Link genes must be sorted for later comparisons
        //using innovation numbers.
        insertLinkGene(link);

        //number of constants increased by 1 for new link
        numOfConstants++;

        origin.add("ADD_LINK");
        checkTree(this);
    }

    public void mutateAddNode() {
        //Add node mutation is done by replacing a random link
        //by two links and a node. So we need at least
        //one link.
        if (linkGenesList.size() == 0) {
            return;
        }

        //Get a random link.
        int linkIdx = RND.getIntZero(linkGenesList.size());//we must remember it's index to remove it later
        ATLinkGene link = linkGenesList.get(linkIdx);
        ATNode from = link.getFrom();
        ATNode to = link.getTo();

        //A prototype of a node to add. Just to know what newRootType it will be
        //in advance.
        ATNodeImpl nodePrototype = nodeCollection.randomFunction();

        //Get an id (innovation number) for the new node.
        //Note, that we work with trees: once a link is transformed
        //to "fromLink--node--toLink" construct the original link disappears.
        ATInnovationHistory.NodeInnovation innovation =
                innovationHistory.getNodeInnovation(
                        link,
                        nodePrototype.getClass());

        //Now, create the actual node.
        ATNode node = new ATNode(innovation.getNodeId(), nodePrototype, nodeCollection.terminals.length);

        //Store the node's records.
        addNode(node);

        //Reconnect "node" with "from" and "to".
        node.addChild(from);
        to.replaceChild(link.getToChildrenIdx(), node);

        //Remove original link from the list.
        linkGenesList.remove(linkIdx);

        //Store new links: "fromLink" and "toLink".
        ATLinkGene fromLink = new ATLinkGene(from, node, innovation.getFromInnovation(), 0);
        ATLinkGene toLink = new ATLinkGene(node, to, innovation.getToInnovation(), link.getToChildrenIdx());
        //Insert links. Link genes must be sorted for later comparisons
        //using innovation numbers.
        insertLinkGene(fromLink);
        insertLinkGene(toLink);

        if (from.isTerminal()) {
            node.incTerminalsConnected(from.getId()); //note, id is same as terminal index
        }

        //number of constants increased by 1 for the new link connecting to the new node
        numOfConstants++;

        origin.add("ADD_NODE");
        checkTree(this);
    }

    public void mutateInsertRoot() {
        //Choose a new function for he node.
        ATNodeImpl nodePrototype = nodeCollection.randomFunction();

        ATInnovationHistory.RootInnovation innovation =
                innovationHistory.getInsertRootInnovation(
                        root.getId(),
                        nodePrototype.getClass());

        //Now, create the new root.
        ATNode newRoot = new ATNode(innovation.getRootId(), nodePrototype, nodeCollection.terminals.length);

        //Store the node's records.
        addNode(newRoot);

        //make connections
        newRoot.addChild(root);

        ATLinkGene link = new ATLinkGene(root, newRoot, innovation.getFromInnovation(), 0);
        insertLinkGene(link);

        //number of constants increased by 1 for connection from old root to the new.
        numOfConstants++;

        //finally point to the newly created root
        root = newRoot;

        origin.add("INSERT_ROOT");
        checkTree(this);
    }

    public void mutateSwitchNode() {
        if (nodeGeneList.size() == 0) {
            return;
        }

        //Get random node.
        ATNode node = RND.randomChoice(nodeGeneList);

        //Choose a new function for he node.
        ATNodeImpl nodePrototype = nodeCollection.randomFunction();

        node.setImpl(nodePrototype);

        origin.add("SWITCH_NODE");
    }

    public void mutateConstants() {
        boolean mutation = false;
        for (ATNode node : nodeGeneList) {
            mutation = node.mutate();
        }
        if (mutation) {
            origin.add("CONSTANTS");
        }
    }

/* TODO check version with constant locks
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


    public void mutateSwitchConstantLocks() {
        boolean mutation = false;
        for (ATNode2 node : nodeGeneList) {
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
*/

    public void elite() {
        origin.add("ELITE");
    }

    public ATTree copy() {
        ATTree copy = new ATTree(nodeCollection, innovationHistory);
        copy.numOfConstants = numOfConstants;
        for (ATNode node : nodeGeneList) {
            ATNode nodeCopy = new ATNode(node);
            copy.nodeGenes.put(nodeCopy.getId(), nodeCopy);
            copy.nodeGeneList.add(nodeCopy);
        }

        copy.root = copy.nodeGenes.get(root.getId());

        for (ATLinkGene link : linkGenesList) {
            ATNode fromNode = copy.terminals.get(link.getFrom().getId());
            if (fromNode == null) {
                fromNode = copy.nodeGenes.get(link.getFrom().getId());
            }
            ATNode toNode = copy.nodeGenes.get(link.getTo().getId());
            toNode.addChild(fromNode);
            ATLinkGene linkCopy = new ATLinkGene(
                    fromNode,
                    toNode,
                    link.getInnovation(), link.getToChildrenIdx());
            copy.linkGenesList.add(linkCopy);
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
        ATLinkGene il, jl;

        int iLen = a.linkGenesList.size(), jLen = b.linkGenesList.size();

        while ((i < iLen) && (j < jLen)) {
            il = a.linkGenesList.get(i);
            jl = b.linkGenesList.get(j);
            if (il.getInnovation() == jl.getInnovation()) {
                i++;
                j++;
                common++;
                double iConstant = il.getTo().getConstantForLinkGene(il);
                double jConstant = jl.getTo().getConstantForLinkGene(jl);
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
        //TODO should be proportional to to length of a longer genome!!!
        double distance = GPAT.DISTANCE_C1 * excess + GPAT.DISTANCE_C2 * disjoint + weights;
        return distance;
    }

    boolean limitStructure() {
        return getDepth() > 5 || getNumOfConstants() > 10;
    }


    @Override
    public String toString() {
        ///*
        StringBuilder builder = new StringBuilder(" NODES:\n");
        for (ATNode nodeGene : nodeGeneList) {
            builder.append(nodeGene.getId()).append(" ").append(nodeGene.getName()).append(" CT:").append(nodeGene.listOfConnectedTerminals()).append('\n');
        }
        builder.append(" LINKS:\n");
        for (ATLinkGene linkGene : linkGenesList) {
            builder.append(linkGene).append(" C:").append(linkGene.getTo().getConstantForLinkGene(linkGene)).append('\n');
        }
        builder.append(" EXPRESSION:\n").append(root.toMathematicaExpression()).append('\n');
        return builder.toString();
        //*/
//        return root.toMathematicaExpression();
    }

    public String toMathematicaExpression() {
        return root.toMathematicaExpression();
    }

    public static void main(String[] args) {
        ATNodeImpl[] functions = new ATNodeImpl[]{new ATFunctions.Plus(), new ATFunctions.Times()};
        ATNodeImpl[] terminals = new ATNodeImpl[]{new ATTerminals.Constant(1.0), new ATTerminals.Constant(-1.0)};
        ATNodeCollection nodeCollection = new ATNodeCollection(functions, terminals, 2);

        RND.initializeTime();
        ATTree tree = ATTree.createMinimalSubstrate(nodeCollection, new ATInnovationHistory(2 + terminals.length + 1));
        System.out.println(tree);
        System.out.println(tree.innovationHistory);
        for (int i = 0; i < 9; i++) {
            tree.mutateAddLink();
            System.out.println(tree);
            System.out.println(tree.innovationHistory);
        }
        for (int i = 0; i < 1; i++) {
            tree.mutateAddNode();
            System.out.println(tree);
            System.out.println(tree.innovationHistory);
        }
//        ATTree tree2 = tree.copy();
//        System.out.println(tree2);
    }

    private static void checkTree(ATTree tree) {
        //DEBUG
        //checkInnovationSorted
        for (int i = 1; i < tree.linkGenesList.size(); i++) {
            ATLinkGene link = tree.linkGenesList.get(i);
            ATLinkGene prevLink = tree.linkGenesList.get(i - 1);
            if (link.getInnovation() == prevLink.getInnovation()) {
                throw new IllegalStateException("Duplicates in link lists.\n" + tree);
            }
            if (link.getInnovation() < prevLink.getInnovation()) {
                throw new IllegalStateException("Unsorted link lists.\n" + tree);
            }
        }
        //checkNumberOfConstants
        int cnt = 0;
        for (ATNode node : tree.nodeGeneList) {
            cnt += node.getArity();
        }
        if (cnt != tree.numOfConstants) {
            throw new IllegalStateException("The number of constants does not match: " + cnt + " vs. " + tree.getNumOfConstants() + "\n" + tree);
        }


//        for (ATLinkGene link : tree.linkGenesList) {
//            if (link.getInnovation() == 0 && link.getToChildrenIdx() > 0) {
//                throw new IllegalStateException("Innovation 0 as child > #0.\n" + tree);
//            }
//        }
    }

    public double[] getConstants() {
        double[] constants = new double[getNumOfConstants()];
        int cnt = 0;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                constants[cnt++] = node.getConstant(i);
            }
        }
        return constants;
    }

    public void setConstants(double[] constants) {
        if (constants.length != getNumOfConstants()) {
            throw new IllegalStateException("The number of constants does not match.");
        }
        int cnt = 0;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                node.setConstant(i, constants[cnt++]);
            }
        }

    }
}
