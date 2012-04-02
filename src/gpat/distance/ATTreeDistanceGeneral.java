package gpat.distance;

import common.RND;
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
public class ATTreeDistanceGeneral implements IDistance<ATTree> {
    private double C;
    private double K;
    private boolean notMatchingNodeExit;
    private boolean descendNullTrees;

    private double min = Double.MAX_VALUE;
    private double max = -Double.MAX_VALUE;
    private double range;

    public ATTreeDistanceGeneral(ParameterCombination parameters) {
        C = parameters.getDouble("GP.DISTANCE_GENERAL_C");
        K = parameters.getDouble("GP.DISTANCE_GENERAL_K");
        notMatchingNodeExit = parameters.getBoolean("GP.DISTANCE_GENERAL_NOT_MATCHING_NODE_EXIT");
        descendNullTrees = parameters.getBoolean("GP.DISTANCE_GENERAL_DESCEND_NULL_TREES");
    }

    public double distance(ATTree a, ATTree b) {
        return distanceRecursive(a.getRoot(), b.getRoot());
    }

    private double distanceRecursive(ATNode a, ATNode b) {
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
            }
        }

        double distanceB = 0.0;
        int longer = aLength < bLength ? bLength : aLength;
        int shorter = aLength < bLength ? aLength : bLength;
        for (int i = 0; i < longer; i++) {
            if (!descendNullTrees && i >= shorter) {
                break;
            }
            ATNode aChild = (i >= aLength ? null : a.getChild(i));
            ATNode bChild = (i >= bLength ? null : b.getChild(i));
            distanceB += distanceRecursive(aChild, bChild);
        }

        return distanceA + distanceB / K;
    }

}
