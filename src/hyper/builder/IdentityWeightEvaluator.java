package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 2, 2010
 * Time: 1:52:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdentityWeightEvaluator implements IWeightEvaluator {
    public double evaluate(ICPPN aCPPN, int aCPPNOutput, Node nodeFrom, Node nodeTo, int incomingLinks) {
        return aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
    }
}
