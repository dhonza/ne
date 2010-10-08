package gpaac;

import gp.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 3:36:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IArbitraryArityNode extends INode {
    int getDefaultArity();

    int getMinArity();

    void addChild(INode child);

    void removeChild(int idx);

    int getNumOfConstants();

    double getConstants(int idx);

    void setConstant(int idx, double value);
}
