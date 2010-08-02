package opt;

import common.evolution.IDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 2, 2010
 * Time: 1:34:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleVectorGenomeDistance implements IDistance<DoubleVectorGenome> {
    public double distance(DoubleVectorGenome a, DoubleVectorGenome b) {
        double[] weightsA = a.genome;
        double[] weightsB = b.genome;
        if (weightsA.length > 1000000) {
            System.out.println("DoubleVectorGenomeDistance warning: check precission!");
        }
        double sum = 0.0;
        for (int i = 0; i < weightsA.length; i++) {
            sum += Math.abs(weightsA[i] - weightsB[i]);
        }
        return sum /= weightsA.length;
    }
}
