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
public class Sin extends Node {

    public Sin() {
        super();
    }

    private Sin(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private Sin(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public INode create(int depth, INode[] children) {
        return new Sin(depth, children);
    }

    public INode copy(INode[] children) {
        return new Sin(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        return Math.sin(nodes[0].evaluate(treeInputs));
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "sin";
        }
        return new StringBuilder("sin(").append(nodes[0].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Sin[").append(nodes[0].toMathematicaExpression()).append("]").toString();
    }

    public String getName() {
        return "sin";
    }
}