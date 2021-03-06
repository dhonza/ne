package gp;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 3:28:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface INode {
    double evaluate(TreeInputs treeInputs);

    INode create(int depth, INode[] children);

    INode copy(INode[] children);

    INode copySubtree();

    int getArity();

    INode getChild(int idx);

    void setChild(int idx, INode child);

    INode[] getChildren();

    int getDepth();

    long getInnovation();

    String getName();

    //TODO remove these two

    String innovationToString();

    String toMathematicaExpression();

    double computeAritySum();

    int computeDepth();

    int computeConstants();

    int computeLeaves();

    int computeNodes();
}
