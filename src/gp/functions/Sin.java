package gp.functions;

import gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:05:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sin extends Node {

    public Sin() {
        super();
    }

    private Sin(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private Sin(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    protected Node create(int depth, Node[] children) {
        return new Sin(depth, children);
    }

    protected Node copy(Node[] children) {
        return new Sin(depth, children, innovation);
    }

    public double evaluate() {
        return Math.sin(nodes[0].evaluate());
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        return new StringBuilder("sin(").append(nodes[0].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Sin[").append(nodes[0].toMathematicaExpression()).append("]").toString();
    }
}