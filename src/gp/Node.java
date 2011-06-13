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

    public double computeAritySum() {
        double aritySum = getArity();
        for (INode child : nodes) {
            aritySum += child.computeAritySum();
        }
        return aritySum;
    }

    public int computeDepth() {
        int depth = 0;
        for (INode child : nodes) {
            int childDepth = child.computeDepth() + 1;
            depth = depth < childDepth ? childDepth : depth;
        }
        return depth;
    }

    public int computeConstants() {
        int num = 0;
        for (INode child : nodes) {
            num += child.computeConstants();
        }
        return num;
    }

    public int computeLeaves() {
        if (nodes.length == 0) {
            return 1;
        }
        int leaves = 0;
        for (INode child : nodes) {
            leaves += child.computeLeaves();
        }
        return leaves;
    }

    public int computeNodes() {
        int num = 1;
        for (INode child : nodes) {
            num += child.computeNodes();
        }
        return num;
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
