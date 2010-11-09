package gpat;

import gp.TreeInputs;

import java.util.ArrayList;
import java.util.Collections;
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
    protected List<Long> incomingLinkInnovations; //children are sorted by incoming link innovation numbers
    protected List<Double> constants;
    protected List<Boolean> constantLocks;

    protected ATNode(int id, int depth) {
        this.id = id;
        this.depth = depth;
        children = new ArrayList<ATNode>();
        incomingLinkInnovations = new ArrayList<Long>();
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

    private void switchChildren(int one, int two) {
        ATNode tnode = children.get(one);
        Long tincomingLinkInnovation = incomingLinkInnovations.get(one);
        Double tconstant = constants.get(one);
        Boolean tconstantLock = constantLocks.get(one);

        children.set(one, children.get(two));
        incomingLinkInnovations.set(one, incomingLinkInnovations.get(two));
        constants.set(one, constants.get(two));
        constantLocks.set(one, constantLocks.get(two));

        children.set(two, tnode);
        incomingLinkInnovations.set(two, tincomingLinkInnovation);
        constants.set(two, tconstant);
        constantLocks.set(two, tconstantLock);
    }

    private void insertSortNode(ATNode child, long incomingLinkInnovation) {
        int pos = Collections.binarySearch(incomingLinkInnovations, incomingLinkInnovation);
                
    }

    public void addChild(ATNode child, long incomingLinkInnovation) {
        if (children.size() > 0 && incomingLinkInnovation <= incomingLinkInnovations.get(children.size() - 1)) {
            System.out.println("ZZZZZZZZ");
        }
        children.add(child);
        incomingLinkInnovations.add(incomingLinkInnovation);
        constants.add(1.0);
        constantLocks.add(true);
    }

    public void replaceChild(ATNode oldChild, ATNode newChild, long newIncomingLinkInnovation) {
        int idx = children.indexOf(oldChild);
        children.set(idx, newChild);
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

    public String toMathematicaExpression() {
        if (getArity() == 0) {
            return getName();
        } else {
            StringBuilder b = new StringBuilder(getName());
            b.append("[");
            for (int i = 0; i < children.size(); i++) {
                ATNode child = children.get(i);
                b.append(constants.get(i)).append("*").append(child.toMathematicaExpression());
                if (i < children.size() - 1) {
                    b.append(",");
                }
            }
            b.append("]");
            return b.toString();
        }
    }
}
