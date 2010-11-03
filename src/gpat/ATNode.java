package gpat;

import gp.TreeInputs;

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

    protected ATLink parentLink;
    protected List<ATLink> childrenLinks;
    protected List<Double> constants;
    protected List<Boolean> constantLocks;

    protected ATNode(int id, int depth) {
        this.id = id;
        this.depth = depth;
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
        return childrenLinks.size();
    }

    public ATNode getChild(int idx) {
        return childrenLinks.get(idx).getTo();
    }

    abstract public String getName();

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
