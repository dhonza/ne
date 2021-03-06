package gpaac;

import gp.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 3:54:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractNode implements INode, Cloneable {
    protected INode[] children;
    protected int depth;
    protected long innovation;

    protected AbstractNode(int depth, long innovation, INode[] children) {
        this.depth = depth;
        this.innovation = innovation;
        this.children = children;
    }

    protected AbstractNode() {
        this.depth = -1;
        this.innovation = -1L;
        this.children = new INode[0];
    }

    public INode copy(INode[] children) {
        try {
            AbstractNode c = (AbstractNode) this.clone();
            c.children = children;
            return c;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public INode copySubtree() {
        if (getArity() == 0) {
            return copy(new INode[0]);
        }
        INode[] childrenCopy = new INode[getArity()];
        for (int i = 0; i < getArity(); i++) {
            childrenCopy[i] = children[i].copySubtree();
        }
        return copy(childrenCopy);
    }

    public INode getChild(int idx) {
        if (idx >= getArity()) {
            throw new IndexOutOfBoundsException("Not enough children, arity = " +
                    getArity() + " idx = " + idx + ".");
        }
        return children[idx];
    }

    public void setChild(int idx, INode child) {
        if (idx >= getArity()) {
            throw new IndexOutOfBoundsException("Not enough children, arity = " +
                    getArity() + " idx = " + idx + ".");
        }
        children[idx] = child;
    }

    public INode[] getChildren() {
        if (children == null) {
            System.out.println("a");
        }
        return children.clone();
    }

    public int getArity() {
        return 0;
    }

    public int getDepth() {
        return depth;
    }

    public int computeDepth() {
        return getDepth();
    }

    public long getInnovation() {
        return innovation;
    }

    @Override
    public String toString() {
        if (getArity() == 0) {
            return getName();// + "|" + super.toString() + "|";
        }
        StringBuilder b = new StringBuilder(getName());
        b.append("[");
        for (int i = 0; i < children.length; i++) {
            INode child = children[i];
            b.append(child);
            if (i < children.length - 1) {
                b.append(",");
            }
        }
        b.append("]");
        return b.toString();// + "|" + super.toString() + "|";
    }

    public String innovationToString() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public String toMathematicaExpression() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public double computeAritySum() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public int computeConstants() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public int computeLeaves() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public int computeNodes() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }
}
