package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.node.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 6:05:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class OriginalWeightEvaluator implements IWeightEvaluator {
    public double evaluate(ICPPN aCPPN, int aCPPNOutput, INode nodeFrom, INode nodeTo, int incomingLinks) {
        double greyVal = aCPPN.evaluate(aCPPNOutput, nodeFrom.getCoordinate(), nodeTo.getCoordinate());
//        System.out.println("{" + nodeFrom.getCoordinate() + ", " + nodeTo.getCoordinate() + ", " + aCPPNOutput + "},");
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