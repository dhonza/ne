package gp.distance;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gp.Tree;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistancePhenotypic implements IDistance<Tree> {
    private double low;
    private double high;
    private int steps;

    public TreeDistancePhenotypic(ParameterCombination parameters) {
        low = parameters.getDouble("GPAT.DISTANCE_PHENO_LOW");
        high = parameters.getDouble("GPAT.DISTANCE_PHENO_HIGH");
        steps = parameters.getInteger("GPAT.DISTANCE_PHENO_STEPS");
    }

    public double distance(Tree a, Tree b) {
        int n = a.getNumOfInputs();
        TreeInputs treeInputs = new TreeInputs(n);
        double[] inputs = new double[n];
        double distance = distanceRecursive(a, b, treeInputs, inputs, 0);
        return distance;
    }


    private double distanceRecursive(Tree a, Tree b, TreeInputs treeInputs, double[] inputs, int idx) {
        if (idx == inputs.length) {//input vector completed
            treeInputs.loadInputs(inputs);
            double diff = a.evaluate(treeInputs) - b.evaluate(treeInputs);
//            for (int i = 0; i < inputs.length; i++) {
//                System.out.print(inputs[i] + " ");
//            }
//            System.out.println();
            return diff * diff;
        } else {
            //construct vector
            double scale = high - low;
            double step = scale / (steps - 1);
            double distance = 0.0;
            for (int i = 0; i < steps; i++) {
                inputs[idx] = low + i * step;
                distance += distanceRecursive(a, b, treeInputs, inputs, idx + 1);
            }
            return distance / steps;
        }
    }
}
