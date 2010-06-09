package common.net.precompiled;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 11:40:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IPrecompiledFeedForwardStub {
    double[] propagate(double b, double[] in, double[] w);
}
