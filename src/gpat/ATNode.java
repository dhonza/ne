package gpat;

import common.mathematica.MathematicaUtils;
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
    private ATNodeImpl impl;

    protected List<ATNode> children;
    protected List<Double> constants;

    //the number of terminals EVER connected to this node, even when the terminal is disconnected by mutateAddNode it
    // stays here
    protected int[] terminalsConnected;

    public ATNode(int id, ATNodeImpl impl, int numOfTerminals) {
        this.id = id;
        this.impl = impl;
        this.children = new ArrayList<ATNode>();
        this.constants = new ArrayList<Double>();
        this.terminalsConnected = new int[numOfTerminals];
    }

    public ATNode(ATNode node) {
        this(node.getId(), node.getImpl(), node.terminalsConnected.length);
        this.constants = new ArrayList<Double>(node.constants);
        this.terminalsConnected = node.terminalsConnected.clone();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ATNodeImpl getImpl() {
        return impl;
    }

    public void setImpl(ATNodeImpl impl) {
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

    public double getConstantForLinkGene(ATLinkGene linkGene) {
        //TODO: these integrity teste
//        if (linkGene.getTo().getId() != getId()) {
//            throw new IllegalStateException("Not incoming linkGene supplied!");
//        }
//        if (children.size() <= linkGene.getToChildrenIdx()) {
//            throw new IndexOutOfBoundsException();
//        }
        return getConstant(linkGene.getToChildrenIdx());
    }

    public void setConstant(int idx, double value) {
        constants.set(idx, value);
    }

    public String getName() {
        return impl.getName();
    }

    public boolean hasConstants() {
        return impl.hasConstants();
    }

    public boolean isTerminal() {
        return impl.isTerminal();
    }

    public int repeatConstant() {
        return impl.repeatConstant();
    }

    public int repeatInput() {
        return impl.repeatInput();
    }

    public int getTerminalsConnected(int idx) {
        return terminalsConnected[idx];
    }

    public void incTerminalsConnected(int idx) {
        terminalsConnected[idx]++;
    }

    public void addChild(ATNode child) {
        children.add(child);
        constants.add(1.0);
//        constantLocks.add(true);
    }

    public void replaceChild(int idx, ATNode child) {
        children.set(idx, child);
    }

    public double computeAritySum() {
        double aritySum = getArity();
        for (ATNode child : children) {
            aritySum += child.computeAritySum();
        }
        return aritySum;
    }

    public int computeDepth() {
        int depth = 0;
        for (ATNode child : children) {
            int childDepth = child.computeDepth() + 1;
            depth = depth < childDepth ? childDepth : depth;
        }
        return depth;
    }

    public int computeLeaves() {
        if (children.size() == 0) {
            return 1;
        }
        int leaves = 0;
        for (ATNode child : children) {
            leaves += child.computeLeaves();
        }
        return leaves;
    }

    public int computeNodes() {
        int nodes = 1;
        for (ATNode child : children) {
            nodes += child.computeNodes();
        }
        return nodes;
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
                if (hasConstants()) {
                    b.append(constants.get(i)).append("*");
                }
                b.append(child.toMathematicaExpression());
                if (i < children.size() - 1) {
                    b.append(",");
                }
            }
            b.append("]");
            return b.toString();
        }
    }

    public String listOfConnectedTerminals() {
        return MathematicaUtils.arrayToMathematica(terminalsConnected);
    }
}
