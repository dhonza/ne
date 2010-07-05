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
public class OriginalWeightEvaluator implements WeightEvaluator {
    public double evaluate(CPPN aCPPN, int aCPPNOutput, Node nodeFrom, Node nodeTo, int incomingLinks) {
        double greyVal = aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
        if (Math.abs(greyVal) > 0.2) {
            if (greyVal > 0.0) {
                return (((greyVal - 0.2) / 0.8) * 3.0);
            } else {
                return (((greyVal + 0.2) / 0.8) * 3.0);
            }
        } else {
            return 0.0;
        }
    }
}