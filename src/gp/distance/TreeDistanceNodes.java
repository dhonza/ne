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
public class TreeDistanceNodes implements IDistance<Tree> {
    public double distance(Tree a, Tree b) {
        double aNodes = a.getNumOfNodes();
        double bNodes = b.getNumOfNodes();
        double max = aNodes < bNodes ? bNodes : aNodes;
        double distance = Math.abs(aNodes - bNodes) / max;
        assert distance <= 1.0;
        return distance;
    }
}
