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
public class SqrtAbs extends Node {

    public SqrtAbs() {
        super();
    }

    private SqrtAbs(int depth, Node[] children) {
        super(depth, children.clone());
    }

    private SqrtAbs(int depth, Node[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public Node create(int depth, Node[] children) {
        return new SqrtAbs(depth, children);
    }

    protected Node copy(Node[] children) {
        return new SqrtAbs(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        return Math.sqrt(Math.abs(nodes[0].evaluate(treeInputs)));
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "sa";
        }
        return new StringBuilder("sqrt(|").append(nodes[0].toString()).append("|)").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Sqrt[Abs[").append(nodes[0].toMathematicaExpression()).append("]]").toString();
    }
}