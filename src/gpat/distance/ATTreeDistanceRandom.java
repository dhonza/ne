package gpat.distance;

import common.RND;
import common.evolution.IDistance;
import gpat.ATNode;
import gpat.ATTree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTreeDistanceRandom implements IDistance<ATTree> {
    public double distance(ATTree a, ATTree b) {
        return RND.getDouble();
    }
}
