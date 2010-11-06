package gpat;

import common.evolution.IDistance;
import gpaac.AACForest;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:41:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATForestDistance implements IDistance<ATForest> {
    public double distance(ATForest a, ATForest b) {
        double distances = 0.0;
        for (int i = 0; i < a.trees.length; i++) {
            distances += a.trees[i].distance(b.trees[i]);
        }
        return distances / a.trees.length;
    }
}
