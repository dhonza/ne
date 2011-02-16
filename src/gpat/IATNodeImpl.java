package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/16/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IATNodeImpl {
    String getName();

    double evaluate(IATNode node, TreeInputs treeInputs);

}
