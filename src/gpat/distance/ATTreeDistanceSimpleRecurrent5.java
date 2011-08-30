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
public class ATTreeDistanceSimpleRecurrent5 implements IDistance<ATTree> {
    private double c1;
    private double c2;

    public ATTreeDistanceSimpleRecurrent5(ParameterCombination parameters) {
        c1 = parameters.getDouble("GPAT.DISTANCE_REC5_C1");
        c2 = parameters.getDouble("GPAT.DISTANCE_REC5_C2");
    }

    public double distance(ATTree a, ATTree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot(), 0);
        return distance;
    }

    private double distanceRecursive(ATNode a, ATNode b, int depth) {
        double distance = 0.0;
        double factor = c1 * Math.pow(c2, depth);

        if (!a.getName().equals(b.getName())) {
            distance += factor;
        }

        int shorter;
        int rest;

        if (a.getArity() < b.getArity()) {
            shorter = a.getArity();
            rest = b.getArity() - shorter;
        } else {
            shorter = b.getArity();
            rest = a.getArity() - shorter;
        }

        for (int i = 0; i < shorter; i++) {
            distance += distanceRecursive(a.getChild(i), b.getChild(i), depth + 1);
        }
        distance += (c1 * Math.pow(c2, depth + 1)) * rest;

        return distance;
    }

}
