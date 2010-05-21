package gp.functions;

import gp.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:05:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class GaussDiff extends Node {

    public GaussDiff() {
        super();
    }

    private GaussDiff(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private GaussDiff(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    protected Node create(int depth, Node[] children) {
        return new GaussDiff(depth, children);
    }

    protected Node copy(Node[] children) {
        return new GaussDiff(depth, children, innovation);
    }

    public double evaluate() {
        double x = nodes[0].evaluate();
        double y = nodes[1].evaluate();
        double diff = x - y;
        return Math.exp(-(diff * diff));
    }

    public int getArity() {
        return 2;
    }

    @Override
    public String toString() {
        return new StringBuilder("exp(-(").append(nodes[0].toString()).append("-").append(nodes[1].toString()).append(")^2)").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Exp[-(").append(nodes[0].toMathematicaExpression()).append(" - ").append(nodes[1].toMathematicaExpression()).append(")^2]").toString();
    }
}