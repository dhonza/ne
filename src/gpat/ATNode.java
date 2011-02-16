package gpat;

import gp.TreeInputs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/16/11
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATNode implements IATNode {
    private int id;
    private IATNodeImpl impl;

    protected List<ATNode> children;
    protected List<Double> constants;

    public ATNode(int id, IATNodeImpl impl) {
        this.id = id;
        this.impl = impl;
        this.children = new ArrayList<ATNode>();
        this.constants = new ArrayList<Double>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public IATNodeImpl getImpl() {
        return impl;
    }

    public void setImpl(IATNodeImpl impl) {
        this.impl = impl;
    }

    public int getArity() {
        return children.size();
    }

    public ATNode getChild(int idx) {
        return children.get(idx);
    }

    public double getConstant(int idx) {
        return constants.get(idx);
    }

    public void setConstant(int idx, double value) {
        constants.set(idx, value);
    }

    public String getName() {
        return impl.getName();
    }

    public void addChild(ATNode child) {
        children.add(child);
        constants.add(1.0);
//        constantLocks.add(true);
    }

    public void replaceChild(int idx, ATNode child) {
        children.set(idx, child);
    }

    public double evaluate(TreeInputs treeInputs) {
        return impl.evaluate(this, treeInputs);
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
