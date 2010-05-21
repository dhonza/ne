package gp.functions;

import gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:05:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Atan extends Node {

    public Atan() {
        super();
    }

    private Atan(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private Atan(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    protected Node create(int depth, Node[] children) {
        return new Atan(depth, children);
    }

    protected Node copy(Node[] children) {
        return new Atan(depth, children, innovation);
    }

    public double evaluate() {
        return Math.atan(nodes[0].evaluate());
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        return new StringBuilder("atan(").append(nodes[0].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Atan[").append(nodes[0].toMathematicaExpression()).append("]").toString();
    }
}