package gpat;

import gp.TreeInputs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 3, 2010
 * Time: 2:57:01 PM
 * To change this template use File | Settings | File Templates.
 */
abstract public class ATNode {
    final private int id;
    final private int depth;

    protected ATNode parent;
    protected List<ATNode> children;
    protected List<Double> constants;
    protected List<Boolean> constantLocks;

    protected ATNode(int id, int depth) {
        this.id = id;
        this.depth = depth;
        children = new ArrayList<ATNode>();
        constants = new ArrayList<Double>();
        constantLocks = new ArrayList<Boolean>();
    }

    public ATNode() {
        this.id = -1;
        this.depth = -1;
    }

    public int getId() {
        return id;
    }

    public int getDepth() {
        return depth;
    }

    public int getArity() {
        return children.size();
    }

    public void setParent(ATNode parent) {
        this.parent = parent;
    }

    public ATNode getChild(int idx) {
        return children.get(idx);
    }

    public void addChild(ATNode child) {
        children.add(child);
        constants.add(1.0);
        constantLocks.add(true);
    }

    public void removeChild(ATNode child) {
        int idx = children.indexOf(child);
        children.remove(idx);
        constants.remove(idx);
        constantLocks.remove(idx);
    }

    public double getConstant(int idx) {
        return constants.get(idx);
    }

    public void setConstant(int idx, double value) {
        constants.set(idx, value);
    }

    public boolean isConstantLock(int idx) {
        return constantLocks.get(idx);
    }

    public void setConstantLock(int idx, boolean lock) {
        constantLocks.set(idx, lock);
    }

    abstract public String getName();

    abstract public ATNode create(int id, int depth);

    public ATNode copy() {
        return create(id, depth);
    }

    abstract public double evaluate(TreeInputs treeInputs);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ATNode)) return false;

        ATNode atNode = (ATNode) o;

        if (id != atNode.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
