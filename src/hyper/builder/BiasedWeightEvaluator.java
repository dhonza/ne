package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.node.INode;
import hyper.substrate.node.NodeType;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 6:05:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class BiasedWeightEvaluator implements IWeightEvaluator {
    public double evaluate(ICPPN aCPPN, int aCPPNOutput, INode nodeFrom, INode nodeTo, int incomingLinks) {
        double greyVal = aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
        double biasMultiplier = 1.0;
        if (nodeFrom.getType() == NodeType.BIAS) {
            biasMultiplier = incomingLinks;
        }
        if (Math.abs(greyVal) > 0.2) {
            if (greyVal > 0.0) {
                return biasMultiplier * (((greyVal - 0.2) / 0.8) * 3.0);
            } else {
                return biasMultiplier * (((greyVal + 0.2) / 0.8) * 3.0);
            }
        } else {
            return 0.0;
        }
    }
}