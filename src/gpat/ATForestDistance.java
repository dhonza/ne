package gpat;

import common.evolution.IDistance;
import gpat.distance.ATTreeDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:41:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATForestDistance implements IDistance<ATForest> {
    final private IDistance<ATTree> treeIDistance;

    public ATForestDistance() {
        this(new ATTreeDistance());
    }

    public ATForestDistance(IDistance<ATTree> treeIDistance) {
        this.treeIDistance = treeIDistance;
    }

    public double distance(ATForest a, ATForest b) {
        double distances = 0.0;
        for (int i = 0; i < a.trees.length; i++) {
            distances += treeIDistance.distance(a.trees[i], b.trees[i]);
        }
        return distances / a.trees.length;
    }
}
