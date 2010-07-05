package hyper.builder;

import hyper.cppn.CPPN;
import hyper.substrate.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 5:54:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WeightEvaluator {
    double evaluate(CPPN aCPPN, int aCPPNOutput, Node nodeFrom, Node nodeTo, int incomingLinks);
}
