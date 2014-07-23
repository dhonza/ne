package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.node.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 2, 2010
 * Time: 1:52:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockWeightEvaluator implements IWeightEvaluator {
    public double evaluate(ICPPN aCPPN, int aCPPNOutput, INode nodeFrom, INode nodeTo, int incomingLinks) {
        return 1.0;
    }
}
