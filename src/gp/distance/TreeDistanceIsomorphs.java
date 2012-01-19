package gp.distance;

import common.evolution.IDistance;
import gp.Tree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 9/12/11
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistanceIsomorphs implements IDistance<Tree> {
    public double distance(Tree a, Tree b) {
        double distance = 0.0;
        distance += relativeDifference(a.getNumOfNodes(), b.getNumOfNodes());
        distance += relativeDifference(a.getNumOfLeaves(), b.getNumOfLeaves());
        distance += relativeDifference(a.getDepth(), b.getDepth());
        distance += relativeDifference(a.getNumOfConstants(), b.getNumOfConstants());
        distance /= 4.0;
        assert distance <= 1.0;
        return distance;
    }

    private static double relativeDifference(int a, int b) {
        assert a >= 0 && b >= 0;
        int max = a < b ? b : a;
        if (max == 0) {
            return 0.0;
        }
        return Math.abs((double) a - (double) b) / ((double) max);
    }
}
