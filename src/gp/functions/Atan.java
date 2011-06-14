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
public class Atan extends Node {

    public Atan() {
        super();
    }

    private Atan(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private Atan(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public Node create(int depth, INode[] children) {
        return new Atan(depth, children);
    }

    public Node copy(INode[] children) {
        return new Atan(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        return Math.atan(nodes[0].evaluate(treeInputs));
    }

    public int getArity() {
        return 1;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "atan";
        }
        return new StringBuilder("atan(").append(nodes[0].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("atan[").append(nodes[0].toMathematicaExpression()).append("]").toString();
    }

    public String getName() {
        return "atan";
    }
}