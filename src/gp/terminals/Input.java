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

    public Input(int index, TreeInputs treeInputs) {
        this(0, index);
    }

    private Input(int depth, int index) {
        super(depth, new Node[0]);
        this.index = index;
    }

    private Input(int depth, Node[] nodes, long innovation, int index) {
        super(depth, nodes, innovation);
        this.index = index;
    }

    protected Node create(int depth, Node[] children) {
        return new Input(depth, index);
    }

    protected Node copy(Node[] children) {
        return new Input(depth, children, innovation, index);
    }

    public double evaluate(TreeInputs treeInputs) {
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
