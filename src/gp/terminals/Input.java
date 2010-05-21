package gp.terminals;

import gp.Node;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 20, 2009
 * Time: 2:06:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Input extends Node {
    final private int index;
    transient final private TreeInputs treeInputs;

    public Input(int index, TreeInputs treeInputs) {
        this(0, index, treeInputs);
    }

    private Input(int depth, int index, TreeInputs treeInputs) {
        super(depth, new Node[0]);
        this.index = index;
        this.treeInputs = treeInputs;
    }

    private Input(int depth, Node[] nodes, long innovation, int index, TreeInputs treeInputs) {
        super(depth, nodes, innovation);
        this.index = index;
        this.treeInputs = treeInputs;
    }

    protected Node create(int depth, Node[] children) {
        return new Input(depth, index, treeInputs);
    }

    protected Node copy(Node[] children) {
        return new Input(depth, children, innovation, index, treeInputs);
    }

    public double evaluate() {
        return treeInputs.get(index);
    }

    public int getArity() {
        return 0;
    }

    @Override
    public String toString() {
        return "x" + index;
    }

    public String toMathematicaExpression() {
        return "x" + index;
    }
}
