package gp.distance;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gp.INode;
import gp.Tree;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistanceTreeplicity2 implements IDistance<Tree> {
    private double c1;
    private double c2;

    public TreeDistanceTreeplicity2(ParameterCombination parameters) {
        c1 = parameters.getDouble("GP.DISTANCE_TREEP_C1");
        c2 = parameters.getDouble("GP.DISTANCE_TREEP_C2");
    }

    public double distance(Tree a, Tree b) {
        double distance = 0.0;
        distance = distanceRecursive(a.getRoot(), b.getRoot(), 0);
        return distance;
    }

    private double distanceRecursive(INode a, INode b, int depth) {
        double distance = 0.0;

        if (!a.getName().equals(b.getName())) {
            distance += 1.0;
//            return distance;
        }

        INode shorter;
        INode longer;

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
            //TODO otestovat ruzne verze viz ATTreeDistanceSimpleRecurrent7
            //hlavne treeplicity(longer.getChild(i), 0);
            distance += treeplicity(longer.getChild(i), depth + 1);
        }

        return distance;
    }

    private double treeplicity(INode a, int depth) {
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
