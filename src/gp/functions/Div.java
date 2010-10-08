package gp.functions;

import gp.INode;
import gp.Node;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 11:05:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class Div extends Node {

    public Div() {
        super();
    }

    private Div(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private Div(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public Node create(int depth, INode[] children) {
        return new Div(depth, children);
    }

    public INode copy(INode[] children) {
        return new Div(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        double a = nodes[0].evaluate(treeInputs);
        double b = nodes[1].evaluate(treeInputs);
        if (a == 0.0 || b == 0.0) {
            return 0.0;
        }
        return a / b;
    }

    public int getArity() {
        return 2;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "/";
        }
        return new StringBuilder("(").append(nodes[0].toString()).append("/").append(nodes[1].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("(").append(nodes[0].toMathematicaExpression()).append("/").append(nodes[1].toMathematicaExpression()).append(")").toString();
    }

    public String getName() {
        return "div";
    }
}