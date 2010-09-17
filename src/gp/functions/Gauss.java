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
public class Gauss extends Node {

    public Gauss() {
        super();
    }

    private Gauss(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private Gauss(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public Node create(int depth, Node[] children) {
        return new Gauss(depth, children);
    }

    protected Node copy(Node[] children) {
        return new Gauss(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        double x = nodes[0].evaluate(treeInputs);
        return Math.exp(-(x * x));
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "gauss";
        }
        return new StringBuilder("exp(-").append(nodes[0].toString()).append("^2)").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Exp[- ").append(nodes[0].toMathematicaExpression()).append("^2]").toString();
    }
}