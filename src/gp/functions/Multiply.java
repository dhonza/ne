package gp.functions;

import gp.Node;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:05:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Multiply extends Node {

    public Multiply() {
        super();
    }

    private Multiply(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private Multiply(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    protected Node create(int depth, Node[] children) {
        return new Multiply(depth, children);
    }

    protected Node copy(Node[] children) {
        return new Multiply(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        return nodes[0].evaluate(treeInputs) * nodes[1].evaluate(treeInputs);
    }

    public int getArity() {
        return 2;
    }

    @Override
    public String toString() {
        return new StringBuilder("(").append(nodes[0].toString()).append("*").append(nodes[1].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("(").append(nodes[0].toMathematicaExpression()).append("*").append(nodes[1].toMathematicaExpression()).append(")").toString();
    }
}