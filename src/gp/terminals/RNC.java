package gp.terminals;

import gp.Node;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 7:12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RNC extends Node {
    protected double value = Double.NaN;

    public RNC(double value) {
        this(0, value);
    }


    public RNC(int depth, double value) {
        super(depth, new Node[0]);
        this.value = value;
    }

    private RNC(int depth, Node[] nodes, long innovation, double value) {
        super(depth, nodes, innovation);
        this.value = value;
    }

    public Node create(int depth, Node[] children) {
        return new Constant(depth, value);
    }

    protected Node copy(Node[] children) {
        return new RNC(depth, children, innovation, value);
    }

    public double evaluate(TreeInputs treeInputs) {
        return value;
    }

    public int getArity() {
        return 0;
    }

    @Override
    public String toString() {
        if (Double.isNaN(value)) {
            return "?";
        }
        return Double.toString(value);
    }

    //TODO brackets!

    public String toMathematicaExpression() {
        return Double.toString(value).replace("E", "*10^");
    }
}
