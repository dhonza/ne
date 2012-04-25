package gpat;

import common.ListHelper;
import common.RND;
import gp.GP;
import gp.TreeInputs;

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
    final ATInnovationHistory innovationHistory;

    final private List<String> origin;

    ATNode root;

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
        int c = root.computeUsedConstants();
        return c;
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

    public ATNode getRoot() {
        return root;
    }

    public List<ATLinkGene> getLinkGenesList() {
        return linkGenesList;
    }

    public int getNumOfInputs() {
        return nodeCollection.getNumOfInputs();
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
        tree.root = new ATNode(tree.initialNodeIds, nodeCollection.newFunctionByName("plus"), nodeCollection.terminals.length);
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

    public void mutateStructure() {
        boolean limitStructure = limitStructure();
        if (RND.getDouble() < GPAT.MUTATION_ADD_LINK && !limitStructure) {
            mutateAddLink();
            limitStructure = limitStructure();
        }
        if (RND.getDouble() < GPAT.MUTATION_ADD_NODE && !limitStructure) {
            mutateAddNode();
            limitStructure = limitStructure();
        }
        if (RND.getDouble() < GPAT.MUTATION_INSERT_ROOT && !limitStructure) {
            mutateInsertRoot();
        }
        if (RND.getDouble() < GPAT.MUTATION_PRUNE_SUBTREE) {
            mutatePruneSubtree();
        }
        if (RND.getDouble() < GPAT.MUTATION_SWITCH_NODE) {
            mutateSwitchNode();
        }
        if (RND.getDouble() < GPAT.MUTATION_SWITCH_LEAF) {
            mutateSwitchLeaf();
        }
    }

    public void mutateAddLink() {
        mutateAddLink(nodeGeneList);

        origin.add("ADD_LINK");
        checkTree(this);
    }

    private void mutateAddLink(List<ATNode> nodeGeneList) {
        //choose a random link to be created
        ATRandomLink.FreeLink freeLink = ATRandomLink.getFreeRandomLink(nodeCollection, nodeGeneList);
        if (freeLink == null) {
            origin.add("GET_FREE_LINK_FAILED");
            return;
        }
        //note, that links can start only in terminal (input) nodes
//        int terminalId = RND.getIntZero(terminalList.size());
        int terminalId = freeLink.getTerminalId();
        ATNode from = terminalList.get(terminalId);
//        ATNode to = RND.randomChoice(nodeGeneList);
        ATNode to = freeLink.getTo();

        //increment the number of connections from the very same terminal
        to.incTerminalsConnected(terminalId);

        //add the child to its parent node
        to.addChild(from);

        //get innovation number
        long linkInnovationNumber = innovationHistory.getLinkInnovation(from.getId(), to.getId(), to.getTerminalsConnected(terminalId));

        ATLinkGene link = new ATLinkGene(from, to, linkInnovationNumber, to.getArity() - 1);

        //Insert link. Link genes must be sorted for later comparisons
        //using innovation numbers.
        insertLinkGene(link);

        //number of constants increased by 1 for new link
        numOfConstants++;
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
            //At this place I had to decide whether to also decrease terminal counter for the "to" node.
            //The decision was NO. The terminal counter should carry information of MAXIMUM connection count of
            //selected terminal and inner node. For example:
            //1) We start with the following (only interesting ling genes):
            //  1(x0) --> 3(plus) IN: 3
            //  1(x0) --> 3(plus) IN: 4
            //2) Add node mutation removes the link with innovation no. 3, therefore we have:
            //  1(x0) --> 3(plus) IN: 4
            //3) Now add link will add a new innovation no. 23:
            //  1(x0) --> 3(plus) IN: 4
            //  1(x0) --> 3(plus) IN: 23
            //Innovation number 3 can get back by means of crossover (if implemented). The idea is, that the original link
            //no.3 was REPLACED by a new link-node-link structure.
//            to.decTerminalsConnected(from.getId()); //decrease the terminal counter for the original parent node
        }

        //number of constants increased by 1 for the new link connecting to the new node
        numOfConstants++;

        origin.add("ADD_NODE");

//        System.out.println("F: " + from.getId() + " TO: " + to.getId() + " NEW: " + node.getId());
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
        //Get a random node.
        ATNode node = RND.randomChoice(nodeGeneList);

        //Choose a new function for the node.
        ATNodeImpl nodePrototype = nodeCollection.randomFunction(node.maxArity());

        node.setImpl(nodePrototype);

        origin.add("SWITCH_NODE");
        checkTree(this);
    }

    public void mutateSwitchLeaf() {
        //Get a random node and its leaf.
        ATRandomLeaf.FreeLeaf freeLeaf = ATRandomLeaf.getFreeRandomLink(nodeCollection, nodeGeneList);
        if (freeLeaf == null) {
            origin.add("SWITCH_LEAF_FAILED");
            return;
        }

        int terminalIdx = freeLeaf.getTerminalIdx();
        int targetTerminalId = freeLeaf.getTargetTerminalId();
        ATNode from = terminalList.get(targetTerminalId);
        ATNode to = freeLeaf.getTo();

        //increment the number of connections for the new type of the terminal
        to.incTerminalsConnected(targetTerminalId);

        //replace the original terminal
        to.replaceChild(terminalIdx, from);

        //now we have to replace "from" in LinkGene
        //find it first
        for (int i = 0, linkGenesListSize = linkGenesList.size(); i < linkGenesListSize; i++) {
            ATLinkGene linkGene = linkGenesList.get(i);
            if (linkGene.getTo().getId() == to.getId() && linkGene.getToChildrenIdx() == terminalIdx) {
                //we found it
                //we have to create a new because it is immutable
                ATLinkGene newlinkGene = new ATLinkGene(
                        from, //replace from
                        linkGene.getTo(), //keep all other unmodified
                        linkGene.getInnovation(), //no change to innovation (at least for this version of GPAT)
                        linkGene.getToChildrenIdx());
                //finally replace the old LinkGene by the new
                linkGenesList.set(i, newlinkGene);
                break;
            }
        }

        origin.add("SWITCH_LEAF");
        checkTree(this);
    }

    public void mutatePruneSubtree() {
        //Get a random node. All it's children with their subtrees will be removed.
        List<ATNode> nodesToChooseFrom = new ArrayList<ATNode>(nodeGeneList);
//        nodesToChooseFrom.remove(root);
        if (nodesToChooseFrom.isEmpty()) {
            return;
        }
        ATNode parentNode = RND.randomChoice(nodesToChooseFrom);

        //Find all non-terminal children of the parent node.
        List<ATNode> nodesToRemove = new ArrayList<ATNode>(nodeGeneList.size());
        for (ATNode child : parentNode.children) {
            if (!child.isTerminal()) {
                collectNodesFromTree(child, nodesToRemove);
            }
        }

        //Remove them from nodeGeneList.
        nodeGeneList.removeAll(nodesToRemove);

        //From nodeGene map.
        for (ATNode node : nodesToRemove) {
            nodeGenes.remove(node.getId());
            numOfConstants -= node.getArity();
        }

        //Add also the parent node to the temporary list as whe will remove all LinkGenes with "to" ATNode
        //in this list
        nodesToRemove.add(parentNode);

        for (Iterator<ATLinkGene> iterator = linkGenesList.iterator(); iterator.hasNext(); ) {
            ATLinkGene linkGene = iterator.next();
            if (nodesToRemove.contains(linkGene.getTo())) {
                iterator.remove();
            }
        }

        //Finally, let's remove all parent node's children and constants.
        numOfConstants -= parentNode.getArity();
        parentNode.removeAllChildren();

        //Add a single connection to a terminal.
//        List<ATNode> parentNodeList = new ArrayList<ATNode>();
//        parentNodeList.add(parentNode);
//        mutateAddLink(parentNodeList);

        origin.add("PRUNE_SUBTREE");

        checkTree(this);
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

    public void mutateSwitchLocks() {
        boolean mutation = false;
        for (ATNode node : nodeGeneList) {
            for (int i = 0; i < node.getArity(); i++) {
                if (RND.getDouble() < GPAT.MUTATION_SWITCH_CONSTANT_LOCK) {
//                    node.setLocked(i, !node.isLocked(i));//SWITCH
                    node.setLocked(i, false);//SWITCH ON
                    mutation = true;
                }
            }
        }
        if (mutation) {
            origin.add("CONSTANT_LOCKS");
        }
    }


    public void elite() {
        origin.add("ELITE");
    }

    public ATTree copy() {
        ATTree copy = new ATTree(nodeCollection, innovationHistory);
        copy.numOfConstants = numOfConstants;
        for (ATNode node : nodeGeneList) {
            ATNode nodeCopy = new ATNode(node);
            //Note, this shallow copy will be immediately replaced by deep copy.
            //Just to ensure the right size of the ArrayList.
//            if (node.getArity() > 0 && node.getConstant(0) != 1.0) {
//                System.out.println("RRRRURRRRR");
//            }
            for (ATNode children : node.children) {
                nodeCopy.children.add(children);
            }
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
            toNode.replaceChild(link.getToChildrenIdx(), fromNode);
            ATLinkGene linkCopy = new ATLinkGene(
                    fromNode,
                    toNode,
                    link.getInnovation(), link.getToChildrenIdx());
            copy.linkGenesList.add(linkCopy);
        }

        if (GPAT.CHECK_INTEGRITY && !this.toString().equals(copy.toString())) {
            throw new IllegalStateException("Copy does not match original by String: " + copy.toString() +
                    " vs. " + this.toString() + ".");
        }
        checkTree(copy);
        return copy;
    }

    public double evaluate(TreeInputs treeInputs) {
        return root.evaluate(treeInputs);
    }

    boolean limitStructure() {
//        return getDepth() > 5 || getNumOfConstants() > 10;
//        return getDepth() > 6 || getNumOfConstants() > 20;
        return getDepth() > GP.MAX_DEPTH || getNumOfConstants() > GPAT.MAX_CONSTANTS || getNumOfNodes() > GPAT.MAX_NODES;
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
        if (!GPAT.CHECK_INTEGRITY) {
            return;
        }

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
            throw new IllegalStateException("The number of constants does not match: " + cnt + " vs. " + tree.numOfConstants + "\n" + tree);
        }
        //checkNumberOfChildrenVsConstants
        for (ATNode node : tree.nodeGeneList) {
            if (node.children.size() != node.constants.size()) {
                throw new IllegalStateException("The number of children does not match the number of constants: " +
                        node.children.size() + " vs. " + node.constants.size() + "\n" + tree);
            }
        }
        //checkTerminalsConnected
        int numOfTerminals = tree.nodeCollection.terminals.length;
        for (ATNode node : tree.nodeGeneList) {
            int[] testArray = new int[numOfTerminals];
            for (int i = 0; i < numOfTerminals; i++) {
                testArray[i] = node.getTerminalsConnected(i);//get the actual number of terminalsConnected
            }
            for (int i = 0; i < node.getArity(); i++) {
                ATNode child = node.getChild(i);
                if (child.isTerminal()) {
                    testArray[child.getId()]--;//id is the same as terminal index
                }
            }
            for (int i = 0; i < numOfTerminals; i++) {
                if (testArray[i] < 0) {
                    throw new IllegalStateException("The expected number of connected terminals is lower than the actual number found in tree. " +
                            "Expected: " + node.getTerminalsConnected(i) +
                            ", actual: " + (node.getTerminalsConnected(i) - testArray[i]) +
                            " terminal at index: " + i + ".");
                }
            }
        }
        //checkLinkGeneChildrenIdx
        for (ATLinkGene link : tree.linkGenesList) {
            ATNode fromNode = link.getFrom();
            ATNode toNode = link.getTo();
            ATNode fromNodeInTree = toNode.getChild(link.getToChildrenIdx());
            if (fromNode.isTerminal() && (!fromNode.getName().equals(fromNodeInTree.getName()))) {
                throw new IllegalStateException("Not matching ChildrenIdx: " + link.getToChildrenIdx() +
                        " from node name: " + fromNode.getName() +
                        " from node in tree name: " + fromNodeInTree.getName() + ".");
            }
        }
        //checkNodesGenesAgainstNodesInTheTree
        List<ATNode> collectedNodeList = new ArrayList<ATNode>(tree.nodeGeneList.size());
        collectNodesFromTree(tree.root, collectedNodeList);
        List<ATNode> nodeGeneListCopy = new ArrayList<ATNode>(tree.nodeGeneList);
        Collections.sort(collectedNodeList);
        Collections.sort(nodeGeneListCopy);
        if (collectedNodeList.size() != nodeGeneListCopy.size()) {
            throw new IllegalStateException("Number of nodes in the tree does not match the number in nodeGeneList: " +
                    collectedNodeList.size() + " vs. " + nodeGeneListCopy.size() + ".\n" +
                    ListHelper.asStringInLines(collectedNodeList) +
                    "\nvs.\n\n" +
                    ListHelper.asStringInLines(nodeGeneListCopy));
        }
        if (collectedNodeList.size() != tree.nodeGenes.size()) {
            throw new IllegalStateException("Number of nodes in the tree does not match the number in nodeGenes map: " +
                    collectedNodeList.size() + " vs. " + tree.nodeGenes.size() + ".");
        }
        for (int i = 0, collectedNodeListSize = collectedNodeList.size(); i < collectedNodeListSize; i++) {
            ATNode collectedNode = collectedNodeList.get(i);
            ATNode treeNode = nodeGeneListCopy.get(i);
            if (!collectedNode.equals(treeNode)) {
                throw new IllegalStateException("Collected node does not match the nodeGeneList node:\n" +
                        ListHelper.asStringInLines(collectedNodeList) +
                        "\nvs.\n\n" +
                        ListHelper.asStringInLines(nodeGeneListCopy));
            }
            if (!tree.nodeGenes.containsKey(collectedNode.getId())) {
                throw new IllegalStateException("Collected node not in nodeGenes map: " + collectedNode + ".");
            }
            if (!collectedNode.equals(tree.nodeGenes.get(collectedNode.getId()))) {
                throw new IllegalStateException("Collected node does not match the node in nodeGenes map: " +
                        collectedNode + " vs. " + tree.nodeGenes.get(collectedNode.getId()) + ".");
            }
        }
        //checkLinkGenesAgainstLinksInTheTree
        List<ATLinkGene> collectedLinkGenesList = new ArrayList<ATLinkGene>(tree.linkGenesList.size());
        collectLinkGenesFromTree(tree.root, collectedLinkGenesList);
        List<ATLinkGene> linkGeneListCopy = new ArrayList<ATLinkGene>(tree.linkGenesList);
        Collections.sort(collectedLinkGenesList, ATLinkGene.NoInnovationComparator.getInstance());
        Collections.sort(linkGeneListCopy, ATLinkGene.NoInnovationComparator.getInstance());
        if (collectedLinkGenesList.size() != linkGeneListCopy.size()) {
            throw new IllegalStateException("Number of LikGenes in the tree does not match the number in linkGeneList: " +
                    collectedLinkGenesList.size() + " vs. " + linkGeneListCopy.size() + ".\n" +
                    ListHelper.asStringInLines(collectedLinkGenesList) +
                    "\nvs.\n\n" +
                    ListHelper.asStringInLines(linkGeneListCopy));
        }
        for (int i = 0, collectedLinkGenesListSize = collectedLinkGenesList.size(); i < collectedLinkGenesListSize; i++) {
            ATLinkGene a = collectedLinkGenesList.get(i);
            ATLinkGene b = linkGeneListCopy.get(i);
            if ((a.getFrom().getId() != b.getFrom().getId()) ||
                    (a.getTo().getId() != b.getTo().getId()) ||
                    (a.getToChildrenIdx() != b.getToChildrenIdx())) {
                throw new IllegalStateException("Collected LinkGene does not match the linkGeneList:\n" +
                        ListHelper.asStringInLines(collectedLinkGenesList) +
                        "\nvs.\n\n" +
                        ListHelper.asStringInLines(linkGeneListCopy));
            }

        }
        //checkMaxChildren
        for (ATNode node : tree.nodeGeneList) {
            if (node.getArity() > node.maxArity()) {
                throw new IllegalStateException("Node has higher arity than maxArity: " +
                        node.getArity() + " vs. " + node.maxArity() + ".");
            }
        }
    }

    private static void collectNodesFromTree(ATNode start, List<ATNode> nodes) {
        nodes.add(start);
        for (ATNode child : start.children) {
            if (!child.isTerminal()) {
                collectNodesFromTree(child, nodes);
            }
        }
    }

    private static void collectLinkGenesFromTree(ATNode start, List<ATLinkGene> linkGenes) {
        List<ATNode> children = start.children;
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            ATNode child = children.get(i);
            ATLinkGene treeLinkGene = new ATLinkGene(child, start, -1, i);
            linkGenes.add(treeLinkGene);
            collectLinkGenesFromTree(child, linkGenes);
        }
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
