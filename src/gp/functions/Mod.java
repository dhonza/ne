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
public class Mod extends Node {

    public Mod() {
        super();
    }

    private Mod(int depth, INode[] children) {
        super(depth, children.clone());
    }

    private Mod(int depth, INode[] nodes, long innovation) {
        super(depth, nodes, innovation);
    }

    public INode create(int depth, INode[] children) {
        return new Mod(depth, children);
    }

    public INode copy(INode[] children) {
        return new Mod(depth, children, innovation);
    }

    public double evaluate(TreeInputs treeInputs) {
        double n = nodes[1].evaluate(treeInputs);
        if (n == 0.0) {
            return 0.0;
        }
        double a = nodes[0].evaluate(treeInputs);
        double q = Math.floor(a / n);
        if (Double.isInfinite(q)) {
            return 0.0;
        }

        return a - n * q;
    }

    public int getArity() {
        return 2;
    }

    @Override
    public String toString() {
        if (nodes[0] == null) {
            return "%";
        }
        return new StringBuilder("(").append(nodes[0].toString()).append("%").append(nodes[1].toString()).append(")").toString();
    }

    public String toMathematicaExpression() {
        return new StringBuilder("PMod[").append(nodes[0].toMathematicaExpression()).append(",").append(nodes[1].toMathematicaExpression()).append("]").toString();
    }

//    public static void main(String[] args) {
//        double a = 3.0;
//        double n = 0.3;
//        double q = Math.floor(a / n);
//        System.out.println( "q=" + q + " r=" + (a - n * q));
//    }

    public String getName() {
        return "mod";
    }
}