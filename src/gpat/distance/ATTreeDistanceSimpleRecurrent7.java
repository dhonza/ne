package gpat.distance;

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
public class ATTreeDistanceSimpleRecurrent7 implements IDistance<ATTree> {
    private double c1;
    private double c2;

    public ATTreeDistanceSimpleRecurrent7(ParameterCombination parameters) {
        c1 = parameters.getDouble("GPAT.DISTANCE_REC5_C1");
        c2 = parameters.getDouble("GPAT.DISTANCE_REC5_C2");
    }

    public double distance(ATTree a, ATTree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot(), 0);
        return distance;
    }

    private double distanceRecursive(ATNode a, ATNode b, int depth) {
        double distance = 0.0;
        double factor = c1 * Math.pow(c2, depth);

        if (!a.getName().equals(b.getName())) {
            distance += factor;
            return distance;
        }

        ATNode shorter;
        ATNode longer;

        if (a.getArity() < b.getArity()) {
            shorter = a;
            longer = b;
        } else {
            shorter = b;
            longer = a;
        }

        for (int i = 0; i < shorter.getArity(); i++) {
            distance += distanceRecursive(a.getChild(i), b.getChild(i), depth + 1);
        }

        for (int i = shorter.getArity(); i < longer.getArity(); i++) {
//            distance += 0.5 * treeplicity(longer.getChild(i), depth);
//            distance += 0.5 * treeplicity(longer.getChild(i), depth + 1);//(Dp1)
            distance += treeplicity(longer.getChild(i), depth + 1);//(Dp2)
//            distance += 2.0 * treeplicity(longer.getChild(i), depth + 1);//(Dp3)
        }
        return distance;
    }

    private double treeplicity(ATNode a, int depth) {
        if (a.getArity() == 0) {
            return c1 * Math.pow(c2, depth);
        }
        double simplicity = 0.0;
        for (int i = 0; i < a.getArity(); i++) {
            simplicity += treeplicity(a.getChild(i), depth + 1);
        }
        return simplicity / a.getArity();
    }

}
