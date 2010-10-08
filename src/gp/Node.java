package gp;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Node implements INode, Serializable {
    final protected INode[] nodes;
    final protected int depth;
    final protected long innovation;

    protected Node() {
        depth = 0;
        nodes = new Node[getArity()];
        innovation = -1;
    }

    protected Node(int depth, INode[] nodes) {
        this(depth, nodes, InnovationCounter.getInstance().getNext());
    }

    protected Node(int depth, INode[] nodes, long innovation) {
        this.depth = depth;
        this.nodes = nodes.clone();
        this.innovation = innovation;
    }

    public long getInnovation() {
        return innovation;
    }

    public abstract INode create(int depth, INode[] children);

    abstract public INode copy(INode[] children);

    public INode copySubtree() {
        throw new IllegalStateException("NOT YET IMPLEMENTED: not needed for GP and GEP!");
    }

    public void setChild(int idx, INode child) {
        nodes[idx] = child;
    }

    public INode getChild(int idx) {
        return nodes[idx];
    }

    abstract public double evaluate(TreeInputs treeInputs);

    abstract public int getArity();

    public INode[] getChildren() {
        return nodes.clone();
    }

    public int getDepth() {
        return depth;
    }

    public String innovationToString() {
        String s = innovation + " ";
        for (INode child : nodes) {
            s += child.innovationToString();
        }
        return s;
    }

    public abstract String toMathematicaExpression();
}
