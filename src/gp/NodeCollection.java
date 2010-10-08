package gp;

import common.RND;
import gp.terminals.Input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 24, 2009
 * Time: 10:27:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeCollection implements Serializable {
    final protected INode[] functions;
    final protected INode[] terminals;
    final protected INode[] all;
    protected Map<Integer, List<INode>> nodesPerArity;

    public NodeCollection(INode[] functions, INode[] terminals, int numOfInputs) {
        this.functions = functions.clone();
        this.terminals = addInputs(terminals, numOfInputs);
        this.all = new INode[this.functions.length + this.terminals.length];
        System.arraycopy(this.functions, 0, all, 0, this.functions.length);
        System.arraycopy(this.terminals, 0, all, this.functions.length, this.terminals.length);
        createNodePerArityMap();
    }

    protected INode[] addInputs(INode[] terminals, int numOfInputs) {
        INode[] allTerminals = new Node[terminals.length + numOfInputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        for (int i = 0; i < numOfInputs; i++) {
            allTerminals[terminals.length + i] = new Input(i);
        }
        return allTerminals;
    }

    protected void createNodePerArityMap() {
        nodesPerArity = new HashMap<Integer, List<INode>>();
        for (INode node : all) {
            List<INode> list = nodesPerArity.get(node.getArity());
            if (list == null) {
                list = new ArrayList<INode>();
                nodesPerArity.put(node.getArity(), list);
            }
            list.add(node);
        }
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
        List<INode> list = nodesPerArity.get(arity);
        if (list == null) {
            throw new IllegalStateException("Nodes of arity " + arity + "not exist.");
        }
        return list.get(RND.getInt(0, list.size() - 1));
    }
}
