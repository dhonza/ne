package gpaac;

import gp.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 30, 2010
 * Time: 10:55:16 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractArbitraryArityOneNode extends AbstractArbitraryArityNode {
    protected AbstractArbitraryArityOneNode(int depth, long innovation, INode[] children) {
        super(depth, innovation, children);
    }

    protected AbstractArbitraryArityOneNode() {
        super();
    }

    @Override
    public int getDefaultArity() {
        return 1;
    }
}
