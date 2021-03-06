package gp.terminals;

import gp.INode;
import gp.Node;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:23:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class Constant extends Node implements ITerminal {
    protected double value;

    public Constant(double value) {
        this(0, value);
    }

    public Constant(int depth, double value) {
        super(depth, new Node[0]);
        this.value = value;
    }

    private Constant(int depth, INode[] nodes, long innovation, double value) {
        super(depth, nodes, innovation);
        this.value = value;
    }

    public INode create(int depth, INode[] children) {
        return new Constant(depth, value);
    }

    public INode copy(INode[] children) {
        return new Constant(depth, children, innovation, value);
    }

    public double evaluate(TreeInputs treeInputs) {
        return value;
    }

    public int getArity() {
        return 0;
    }

    @Override
    public String toString() {
        if (value == Math.round(value)) {
            return Integer.toString((int) value);
        }
        return Double.toString(value);
    }

    //TODO brackets!

    public String toMathematicaExpression() {
        return Double.toString(value).replace("E", "*10^");
    }

    public String getName() {
        return "const";
    }
}
