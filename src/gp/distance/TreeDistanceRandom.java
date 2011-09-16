package gp.distance;

import common.RND;
import common.evolution.IDistance;
import gp.Tree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistanceRandom implements IDistance<Tree> {
    public double distance(Tree a, Tree b) {
        return RND.getDouble();
    }
}
