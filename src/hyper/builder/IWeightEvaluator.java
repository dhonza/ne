package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.node.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 5:54:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IWeightEvaluator {
    double evaluate(ICPPN aCPPN, int aCPPNOutput, INode nodeFrom, INode nodeTo, int incomingLinks);
}
