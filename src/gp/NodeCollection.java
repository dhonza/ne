package gp;

import common.RND;

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
public class NodeCollection {
    final private Node[] functions;
    final private Node[] terminals;
    final private Node[] all;
    final private Map<Integer, List<Node>> nodesPerArity;

    public NodeCollection(Node[] functions, Node[] terminals) {
        this.functions = functions.clone();
        this.terminals = terminals.clone();
        this.all = new Node[this.functions.length + this.terminals.length];
        System.arraycopy(this.functions, 0, all, 0, this.functions.length);
        System.arraycopy(this.terminals, 0, all, this.functions.length, this.terminals.length);
        nodesPerArity = createNodePerArityMap();
    }

    private Map<Integer, List<Node>> createNodePerArityMap() {
        Map<Integer, List<Node>> tempMap = new HashMap<Integer, List<Node>>();
        for (Node node : all) {
            List<Node> list = tempMap.get(node.getArity());
            if (list == null) {
                list = new ArrayList<Node>();
                tempMap.put(node.getArity(), list);
            }
            list.add(node);
        }
        return tempMap;
    }


    public Node getRandomOfAll() {
        return all[RND.getInt(0, all.length - 1)];
    }

    public Node getRandomTerminal() {
        return terminals[RND.getInt(0, terminals.length - 1)];
    }

    public Node getRandomWithArity(int arity) {
        List<Node> list = nodesPerArity.get(arity);
        if (list == null) {
            throw new IllegalStateException("Nodes of arity " + arity + "not exist.");
        }
        return list.get(RND.getInt(0, list.size() - 1));
    }
}
