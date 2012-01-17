package gp.distance;

import common.evolution.IDistance;
import gp.INode;
import gp.Tree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistanceOnlyRest2 implements IDistance<Tree> {
    public double distance(Tree a, Tree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot());
        return distance;
    }

    private double distanceRecursive(INode a, INode b) {
        double distance = 0.0;

        if (!a.getName().equals(b.getName())) {
            distance += 1.0;
            return distance;
        }

        assert a.getArity() == b.getArity();

        for (int i = 0; i < a.getArity(); i++) {
            distance += distanceRecursive(a.getChild(i), b.getChild(i));
        }

        return distance;
    }

}
