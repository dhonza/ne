package gpat;

import common.RND;
import common.mathematica.MathematicaUtils;
import gp.GP;
import gp.TreeInputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/16/11
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATNode implements IATNode, Comparable<ATNode> {
    public static class Constant {
        double value;
        boolean lock;

        public Constant(double value) {
            this.value = value;
            this.lock = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Constant constant = (Constant) o;

            if (lock != constant.lock) return false;
            if (Double.compare(constant.value, value) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (lock ? 1 : 0);
            return result;
        }

        protected Constant copy() {
            Constant c = new Constant(value);
            c.lock = lock;
            return c;
        }
    }

    private int id;
    private ATNodeImpl impl;

    protected List<ATNode> children;
    protected List<Constant> constants;

    //The number of terminals EVER connected to this node, even when the terminal is disconnected by mutateAddNode it
    //stays here. See incTerminalsConnected for further explanation.
    protected int[] terminalsConnected;

    public ATNode(int id, ATNodeImpl impl, int numOfTerminals) {
        this.id = id;
        this.impl = impl;
        this.children = new ArrayList<ATNode>();
        this.constants = new ArrayList<Constant>();
        this.terminalsConnected = new int[numOfTerminals];
    }

    public ATNode(ATNode node) {
        this(node.getId(), node.getImpl(), node.terminalsConnected.length);
        this.constants = new ArrayList<Constant>();
        for (Constant constant : node.constants) {
            this.constants.add(constant.copy());
        }
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
        return constants.get(idx).value;
    }

    public boolean isActive(int idx) {
        return impl.isActive(this, idx);
    }

    public boolean isLocked(int idx) {
        return constants.get(idx).lock;
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
        constants.get(idx).value = value;
    }

    public void setLocked(int idx, boolean lock) {
        constants.get(idx).lock = lock;
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

    public int maxArity() {
        return impl.maxArity();
    }

    public int getTerminalsConnected(int idx) {
        return terminalsConnected[idx];
    }

    /**
     * Increases a number of a given terminal connections to this node throughout evolution. Note, that there is
     * no decrease method as the number in terminalsConnected[] does not carry information of actually connected terminals
     * but rather the number of connections through the whole evolution of the genome. This should help to track
     * structural innovations.
     *
     * @param idx index of a terminal, ATNodeCollection
     */
    public void incTerminalsConnected(int idx) {
        terminalsConnected[idx]++;
    }

    public void addChild(ATNode child) {
        children.add(child);
        constants.add(new Constant(1.0));
    }

    public void replaceChild(int idx, ATNode child) {
        children.set(idx, child);
    }

    public void removeAllChildren() {
        children.clear();
        constants.clear();
        Arrays.fill(terminalsConnected, 0);
    }

    public double computeAritySum() {
        double aritySum = getArity();
        for (ATNode child : children) {
            aritySum += child.computeAritySum();
        }
        return aritySum;
    }

    public int computeUsedConstants() {
        if (impl instanceof ATFunctions.Random) {
            return 1;
        }
        int sum = hasConstants() ? getArity() : 0;
        for (ATNode child : children) {
            if (child.getImpl() instanceof ATTerminals.ConstantMarker) {
                sum++;
                //ConstantMarker is a leaf, so we don't have to descend
            } else {
                sum += child.computeUsedConstants();
            }
        }
        return sum;
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

    public boolean mutate() {
        boolean mutation = false;
        for (int i = 0; i < getArity(); i++) {
            if (RND.getDouble() < GP.MUTATION_CAUCHY_PROBABILITY) {
                setConstant(i, getConstant(i) + GP.MUTATION_CAUCHY_POWER * RND.getCauchy());
                mutation = true;
            }
            if (RND.getDouble() < GPAT.MUTATION_REPLACE_CONSTANT) {
                setConstant(i, RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE));
                mutation = true;
            }
        }
        return mutation;
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
                    b.append(MathematicaUtils.toMathematica(constants.get(i).value)).append("*");
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

    public int compareTo(ATNode other) {
        if (this.getId() < other.getId()) {
            return -1;
        }
        if (this.getId() > other.getId()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ATNode atNode = (ATNode) o;

        if (id != atNode.id) return false;
        if (children != null ? !children.equals(atNode.children) : atNode.children != null) return false;
        if (constants != null ? !constants.equals(atNode.constants) : atNode.constants != null) return false;
        if (impl != null ? !impl.equals(atNode.impl) : atNode.impl != null) return false;
        if (!Arrays.equals(terminalsConnected, atNode.terminalsConnected)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (impl != null ? impl.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (constants != null ? constants.hashCode() : 0);
        result = 31 * result + (terminalsConnected != null ? Arrays.hashCode(terminalsConnected) : 0);
        return result;
    }

    @Override
    public String toString() {
        return getId() + "(" + getName() + ") #CH: " + children.size() + " TC: " + MathematicaUtils.arrayToMathematica(terminalsConnected);
    }
}
