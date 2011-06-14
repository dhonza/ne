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
public class GaussDiff extends Node {

    public GaussDiff() {
        super();
    }

    private GaussDiff(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private GaussDiff(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public INode create(int depth, INode[] children) {
        return new GaussDiff(depth, children);
    }

    public INode copy(INode[] children) {
        return new GaussDiff(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        double x = nodes[0].evaluate(treeInputs);
        double y = nodes[1].evaluate(treeInputs);
        double diff = x - y;
        return Math.exp(-(diff * diff));
    }

    public int getArity() {
        return 2;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "gd";
        }
        return new StringBuilder("exp(-(").append(nodes[0].toString()).append("-").append(nodes[1].toString()).append(")^2)").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("gaussdif[").append(nodes[0].toMathematicaExpression()).append(",").append(nodes[1].toMathematicaExpression()).append("]").toString();
    }

    public String getName() {
        return "gaussdiff";
    }
}