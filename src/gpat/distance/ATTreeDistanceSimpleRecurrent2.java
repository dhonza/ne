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
public class ATTreeDistanceSimpleRecurrent2 implements IDistance<ATTree> {
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

        for (int i = 0; i < shorter; i++) {
            if (!a.getChild(i).getName().equals(b.getChild(i).getName())) {
                distance += 1.0;
            }
            if (a.getArity() > 0 && b.getArity() > 0) {
                distance += distanceRecursive(a.getChild(i), b.getChild(i));
            }
        }
        distance += rest;

        return distance;
    }

}
