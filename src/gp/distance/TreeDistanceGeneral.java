package gp.distance;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gp.INode;
import gp.Tree;
import gp.terminals.Random;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistanceGeneral implements IDistance<Tree> {
    private double C;
    private double K;
    private boolean notMatchingNodeExit;
    private boolean descendNullTrees;

    private double min = Double.MAX_VALUE;
    private double max = -Double.MAX_VALUE;
    private double range;

    public TreeDistanceGeneral(ParameterCombination parameters) {
        C = parameters.getDouble("GP.DISTANCE_GENERAL_C");
        K = parameters.getDouble("GP.DISTANCE_GENERAL_K");
        notMatchingNodeExit = parameters.getBoolean("GP.DISTANCE_GENERAL_NOT_MATCHING_NODE_EXIT");
        descendNullTrees = parameters.getBoolean("GP.DISTANCE_GENERAL_DESCEND_NULL_TREES");

    }

    public double distance(Tree a, Tree b) {
        findMinMaxConstants(a.getRoot());
        findMinMaxConstants(b.getRoot());
        range = max - min;
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot());
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
        return distance;
    }

    private double distanceRecursive(INode a, INode b) {
        double distanceA = 0.0;

        assert a != null || b != null;

        int aLength = 0;
        int bLength = 0;

        if (a == null) {
            bLength = b.getArity();
            distanceA += 1.0;
            if (notMatchingNodeExit) {
                return distanceA;
            }
        } else if (b == null) {
            aLength = a.getArity();
            distanceA += 1.0;
            if (notMatchingNodeExit) {
                return distanceA;
            }
        } else {
            aLength = a.getArity();
            bLength = b.getArity();

            if (!a.getName().equals(b.getName())) {
                distanceA += 1.0;
                if (notMatchingNodeExit) {
                    return distanceA;
                }
            } else if (a instanceof Random && b instanceof Random) {
                double aValue = a.evaluate(null);
                double bValue = b.evaluate(null);
                if (range != 0.0) {//or better use epsilon?
                    double distanceC = Math.abs(aValue - bValue) / range;
                    assert distanceC <= 1.0;
                    assert !Double.isInfinite(distanceC);
                    assert !Double.isNaN(distanceC);
                    distanceA += C * distanceC;
                }
            }

        }

        double distanceB = 0.0;
        int longer = aLength < bLength ? bLength : aLength;
        int shorter = aLength < bLength ? aLength : bLength;
        for (int i = 0; i < longer; i++) {
            if (!descendNullTrees && i >= shorter) {
                break;
            }
            INode aChild = (i >= aLength ? null : a.getChild(i));
            INode bChild = (i >= bLength ? null : b.getChild(i));
            distanceB += distanceRecursive(aChild, bChild);
        }

        return distanceA + distanceB / K;
    }


    private void findMinMaxConstants(INode n) {
        if (n instanceof Random) {
            double value = n.evaluate(null);
            if (min > value) {
                min = value;
            }
            if (max < value) {
                max = value;
            }
            for (int i = 0; i < n.getArity(); i++) {
                findMinMaxConstants(n.getChild(i));
            }
        }
    }

}
