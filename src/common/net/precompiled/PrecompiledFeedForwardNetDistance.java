package common.net.precompiled;

import common.evolution.IDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 1:11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrecompiledFeedForwardNetDistance implements IDistance<PrecompiledFeedForwardNet> {
    public double distance(PrecompiledFeedForwardNet a, PrecompiledFeedForwardNet b) {
        double[] weightsA = a.weights;
        double[] weightsB = b.weights;
        if (weightsA.length > 1000000) {
            System.out.println("PrecompiledFeedForwardNetDistance warning: check precission!");
        }
        double sum = 0.0;
        for (int i = 0; i < weightsA.length; i++) {
            sum += Math.abs(weightsA[i] - weightsB[i]);
        }

        sum /= weightsA.length;

        return sum;
    }
}
