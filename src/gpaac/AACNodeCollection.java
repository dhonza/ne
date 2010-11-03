package gpaac;

import common.RND;
import gp.INode;
import gp.NodeCollection;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 24, 2009
 * Time: 10:27:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class AACNodeCollection extends NodeCollection {
    private int maxArity;
    private List<INode> arbitraryArity;

    public AACNodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        super(functions, terminals, numOfInputs);
    }

    @Override
    protected INode[] addInputs(INode[] terminals, int numOfInputs) {
        INode[] allTerminals = new INode[terminals.length + numOfInputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        for (int i = 0; i < numOfInputs; i++) {
            allTerminals[terminals.length + i] = new AACTerminals.Input(i);
        }
        return allTerminals;
    }

    @Override
    protected void createNodePerArityMap() {
        nodesPerArity = new HashMap<Integer, List<INode>>();
        //minimum arity of nodes with arbitrary arity
        Map<Integer, List<INode>> nodesMinArity = new HashMap<Integer, List<INode>>();
        //stores minimum arities of arbitrary arity nodes
        SortedSet<Integer> arities = new TreeSet<Integer>();
        for (INode node : all) {
            if (node instanceof IArbitraryArityNode) {
                int arity = ((IArbitraryArityNode) node).getMinArity();
                List<INode> list = nodesMinArity.get(arity);
                if (list == null) {
                    list = new ArrayList<INode>();
                    nodesMinArity.put(arity, list);
                }
                list.add(node);
                arities.add(arity);
            } else {
                List<INode> list = nodesPerArity.get(node.getArity());
                if (list == null) {
                    list = new ArrayList<INode>();
                    nodesPerArity.put(node.getArity(), list);
                }
                list.add(node);
            }
        }
        if (arities.size() == 0) {
            maxArity = -1;
            return;
        }
        maxArity = arities.last();
        List<INode> equalOrSmallerMinArity = new ArrayList<INode>();
        for (int arity = arities.first(); arity <= maxArity; arity++) {
            List<INode> minArityList = nodesMinArity.get(arity);
            if (minArityList != null) {
                equalOrSmallerMinArity.addAll(minArityList);
            }
            List<INode> list = nodesPerArity.get(arity);
            if (list == null) {
                list = new ArrayList<INode>();
                nodesPerArity.put(arity, list);
            }
            list.addAll(equalOrSmallerMinArity);
        }
        arbitraryArity = equalOrSmallerMinArity;
    }

    @Override
    public INode getRandomWithArity(int arity) {
        List<INode> list;
        if (arity > maxArity && maxArity != -1) {
            list = arbitraryArity;
        } else {
            list = nodesPerArity.get(arity);
        }
        if (list == null) {
            throw new IllegalStateException("Nodes of arity " + arity + "not exist.");
        }
        return list.get(RND.getInt(0, list.size() - 1));
    }
}
