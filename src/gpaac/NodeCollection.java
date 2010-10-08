package gpaac;

import common.RND;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 24, 2009
 * Time: 10:27:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeCollection implements Serializable {
    final private INode[] functions;
    final private INode[] terminals;
    final private INode[] all;
    final private Map<Integer, List<INode>> nodesPerArity;
    private int maxArity;
    private List<INode> arbitraryArity;

    public NodeCollection(INode[] functions, INode[] terminals) {
        this.functions = functions.clone();
        this.terminals = terminals.clone();
        this.all = new INode[this.functions.length + this.terminals.length];
        System.arraycopy(this.functions, 0, all, 0, this.functions.length);
        System.arraycopy(this.terminals, 0, all, this.functions.length, this.terminals.length);
        nodesPerArity = new HashMap<Integer, List<INode>>();
        createNodePerArityMap();
    }

    private void createNodePerArityMap() {
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

    public INode getRandomOfAll() {
        return all[RND.getInt(0, all.length - 1)];
    }

    public INode getRandomFunction() {
        return functions[RND.getInt(0, functions.length - 1)];
    }

    public INode getRandomTerminal() {
        return terminals[RND.getInt(0, terminals.length - 1)];
    }

    public INode getRandomWithArity(int arity) {
        List<INode> list;
        if (arity > maxArity) {
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
