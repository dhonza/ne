package gpaac;

import gp.INode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 3:59:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractArbitraryArityNode implements IArbitraryArityNode, Cloneable {
    final private int depth;
    final private long innovation;

    private List<INode> children;
    private List<Double> constants;
    private List<Boolean> lockConstants;

    protected AbstractArbitraryArityNode(int depth, long innovation, INode[] children) {
        this.depth = depth;
        this.innovation = innovation;
        this.children = new ArrayList<INode>();
        this.children.addAll(Arrays.asList(children));
        this.constants = new ArrayList<Double>();
        this.lockConstants = new ArrayList<Boolean>();
        for (int i = 0; i < this.children.size(); i++) {
            this.constants.add(1.0);
            this.lockConstants.add(true);
        }
    }

    protected AbstractArbitraryArityNode() {
        this.depth = -1;
        this.innovation = -1L;
        this.children = new ArrayList<INode>();
        this.children.addAll(Arrays.asList(new INode[getDefaultArity()]));
    }

    public INode copy(INode[] children) {
        try {
            AbstractArbitraryArityNode c = (AbstractArbitraryArityNode) this.clone();
            c.children = new ArrayList<INode>();
            c.children.addAll(Arrays.asList(children));
            c.constants = new ArrayList<Double>(this.constants);
            c.lockConstants = new ArrayList<Boolean>(this.lockConstants);
            return c;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public INode copySubtree() {
        if (getArity() == 0) {
            return copy(null);
        }
        INode[] childrenCopy = new INode[getArity()];
        for (int i = 0; i < getArity(); i++) {
            childrenCopy[i] = children.get(i).copySubtree();
        }
        return copy(childrenCopy);
    }

    public void addChild(INode child) {
        children.add(child);
        constants.add(1.0);
        lockConstants.add(true);
    }

    public void removeChild(int idx) {
        children.remove(idx);
        constants.remove(idx);
        lockConstants.remove(idx);
    }

    public int getNumOfConstants() {
        return getArity();
    }

    public double getConstants(int idx) {
        return constants.get(idx);
    }

    public void setConstant(int idx, double value) {
        if (idx == constants.size()) {
            constants.add(value);
        } else {
            constants.set(idx, value);
        }
    }

    public boolean isLockedConstants(int idx) {
        return lockConstants.get(idx);
    }

    public void setLockedConstants(int idx, boolean value) {
        if (idx == lockConstants.size()) {
            lockConstants.add(value);
        } else {
            lockConstants.set(idx, value);
        }
    }

    public int getArity() {
        return children.size();
    }

    public INode getChild(int idx) {
        return children.get(idx);
    }

    public void setChild(int idx, INode child) {
        if (idx == children.size()) {
            children.add(child);
        } else {
            children.set(idx, child);
        }
    }

    public INode[] getChildren() {
        return children.toArray(new INode[children.size()]);
    }

    public int getDefaultArity() {
        return 2;
    }

    public int getMinArity() {
        return 2;
    }

    public int getDepth() {
        return depth;
    }

    public long getInnovation() {
        return innovation;
    }

    @Override
    public String toString() {
        if (getArity() == 0) {
            return getName();//+  "|" + super.toString() + "|";
        }
        StringBuilder b = new StringBuilder(getName());
        b.append("[");
        for (int i = 0; i < children.size(); i++) {
            INode child = children.get(i);
            b.append(constants.get(i)).append("*").append(child);
            if (i < children.size() - 1) {
                b.append(",");
            }
        }
        b.append("]");
        return b.toString();//+  "|" + super.toString() + "|";
    }

    public String innovationToString() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }

    public String toMathematicaExpression() {
        throw new IllegalStateException("NOT IMPLEMENTED: remove!");
    }
}
