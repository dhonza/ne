package gp;

import common.evolution.IDistance;
import gp.distance.TreeDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:41:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForestDistance implements IDistance<Forest> {
    final private IDistance<Tree> treeIDistance;

    public ForestDistance() {
        this(new TreeDistance());
    }

    public ForestDistance(IDistance<Tree> treeIDistance) {
        this.treeIDistance = treeIDistance;
    }

    public double distance(Forest a, Forest b) {
        double distances = 0.0;
        for (int i = 0; i < a.trees.length; i++) {
            distances += treeIDistance.distance(a.trees[i], b.trees[i]);
        }
        return distances / a.trees.length;
    }
}
