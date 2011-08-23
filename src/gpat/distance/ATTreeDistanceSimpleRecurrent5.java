package gpat.distance;

import common.evolution.IDistance;
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
    public double distance(ATTree a, ATTree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot(), 0);
        return distance;
    }

    private double distanceRecursive(ATNode a, ATNode b, int depth) {
        double df = 0.8;
        double distance = 0.0;
        double factor = 0.9 * Math.pow(df, depth);

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
        distance += (0.9 * Math.pow(df, depth + 1)) * rest;

        return distance;
    }

}
