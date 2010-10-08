package gp.terminals;

import common.RND;
import gp.GP;
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
public class Random extends Node {
    protected double value;

    public Random() {
        this(0, 0);
    }

    private Random(int depth) {
        this(depth, RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE));
    }

    private Random(int depth, double value) {
        super(depth, new Node[0]);
        this.value = value;
    }

    private Random(int depth, INode[] nodes, long innovation, double value) {
        super(depth, nodes, innovation);
        this.value = value;
    }

    public INode create(int depth, INode[] children) {
        return new Random(depth);
    }

    public INode copy(INode[] children) {
        return new Random(depth, children, innovation, value);
    }

    public Node localMutate() {
        return new Random(depth, value + GP.MUTATION_CAUCHY_POWER * RND.getCauchy());
    }

    public double evaluate(TreeInputs treeInputs) {
        return value;
    }

    public int getArity() {
        return 0;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    //TODO brackets!

    public String toMathematicaExpression() {
        return Double.toString(value).replace("E", "*10^");
    }

    public String getName() {
        return "rnd";
    }
}