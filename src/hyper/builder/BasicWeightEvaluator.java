package hyper.builder;

import hyper.cppn.CPPN;
import hyper.substrate.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 6:05:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicWeightEvaluator implements WeightEvaluator {
    public double evaluate(CPPN aCPPN, int aCPPNOutput, Node nodeFrom, Node nodeTo, int incomingLinks) {
        return 3.0 * aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
    }
}