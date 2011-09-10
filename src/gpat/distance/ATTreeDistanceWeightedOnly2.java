package gpat.distance;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gpat.ATNode;
import gpat.ATTree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTreeDistanceWeightedOnly2 implements IDistance<ATTree> {

    public ATTreeDistanceWeightedOnly2(ParameterCombination parameters) {
    }

    public double distance(ATTree a, ATTree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot());
        return distance;
    }

    private double distanceRecursive(ATNode a, ATNode b) {
        double distance = 0.0;

        int shorter;
        int rest;

        if (a.getArity() < b.getArity()) {
            shorter = a.getArity();
            rest = b.getArity() - shorter;
        } else {
            shorter = b.getArity();
            rest = a.getArity() - shorter;
        }

        double maxAmplitude = 0.0;
        double[] deltas = new double[shorter];
        for (int i = 0; i < shorter; i++) {
            double thisMaxAmplitude = Math.max(Math.abs(a.getConstant(i)), Math.abs(b.getConstant(i)));
            if (maxAmplitude < thisMaxAmplitude) {
                maxAmplitude = thisMaxAmplitude;
            }
            deltas[i] = Math.abs(a.getConstant(i) - b.getConstant(i));
        }

        for (int i = 0; i < shorter; i++) {
            if (maxAmplitude == 0.0) {
                deltas[i] = 0.0;
            }
            deltas[i] = deltas[i] / maxAmplitude;
            if (Double.isInfinite(deltas[i]) || Double.isNaN(deltas[i])) {
                throw new IllegalStateException("Delta is : " + deltas[i]);
            }
        }

        for (int i = 0; i < shorter; i++) {
            distance += distanceRecursive(a.getChild(i), b.getChild(i)) + deltas[i];
        }

        distance += 2.0 * rest;

        return distance;
    }

}
