package gpaac;

import gp.TreeInputs;

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

    int getArity();

    INode getChildren(int idx);

    int getDepth();

    long getInnovation();

    String getName();
}
