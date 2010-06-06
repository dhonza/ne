package gp;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Node implements Serializable {
    final protected Node[] nodes;
    final protected int depth;
    final protected long innovation;

    protected Node() {
        depth = 0;
        nodes = new Node[getArity()];
        innovation = -1;
    }

    protected Node(int depth, Node[] nodes) {
        this(depth, nodes, InnovationCounter.getInstance().getNext());
    }

    protected Node(int depth, Node[] nodes, long innovation) {
        this.depth = depth;
        this.nodes = nodes.clone();
        this.innovation = innovation;
    }

    public long getInnovation() {
        return innovation;
    }

    abstract protected Node create(int depth, Node[] children);

    abstract protected Node copy(Node[] children);

    abstract public double evaluate();

    abstract public int getArity();

    public Node[] getChildren() {
        return nodes.clone();
    }

    public int getDepth() {
        return depth;
    }

    public String innovationToString() {
        String s = innovation + " ";
        for (Node child : nodes) {
            s += child.innovationToString();
        }
        return s;
    }

    public abstract String toMathematicaExpression();
}
