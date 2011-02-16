package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/16/11
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IATNode {
    int getArity();

    double getConstant(int idx);

    IATNode getChild(int idx);

    double evaluate(TreeInputs treeInputs);
}
