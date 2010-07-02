package hyper.evaluate;

import common.net.INet;
import cz.cvut.felk.cig.jcool.core.Function;
import cz.cvut.felk.cig.jcool.core.Point;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.FakeArrayCPPN;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 1:26:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectEncodingFunction implements Function {
    final private Problem problem;
    final private EvaluableSubstrateBuilder substrateBuilder;

    public DirectEncodingFunction(Problem problem, EvaluableSubstrateBuilder substrateBuilder) {
        this.problem = problem;
        this.substrateBuilder = substrateBuilder;
    }

    public double valueAt(Point point) {
        double[] weights = point.toArray();
        FakeArrayCPPN aCPPN = new FakeArrayCPPN(weights, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        return -problem.evaluate(hyperNet);
    }

    public int getDimension() {
        return 9;
    }
}
