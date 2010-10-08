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
public class Cos extends Node {

    public Cos() {
        super();
    }

    private Cos(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private Cos(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public INode create(int depth, INode[] children) {
        return new Cos(depth, children);
    }


    public INode copy(INode[] children) {
        return new Cos(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        return Math.cos(nodes[0].evaluate(treeInputs));
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "cos";
        }
        return new StringBuilder("cos(").append(nodes[0].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("Cos[").append(nodes[0].toMathematicaExpression()).append("]").toString();
    }

    public String getName() {
        return "cos";
    }
}