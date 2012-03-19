package gpat.distance;

import common.RND;
import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
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
        return RND.getDouble();
    }
}
