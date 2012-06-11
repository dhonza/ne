package gp.distance;

import common.evolution.IDistance;
import common.evolution.IDistanceByOutput;
import common.pmatrix.ParameterCombination;
import gp.Tree;
import gp.TreeInputs;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/22/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistancePhenotypic implements IDistanceByOutput<Tree> {
    private double low;
    private double high;
    private int steps;
    private double[][][] coords = null;

    public TreeDistancePhenotypic(ParameterCombination parameters) {
        low = parameters.getDouble("GPAT.DISTANCE_PHENO_LOW", 0.0);
        high = parameters.getDouble("GPAT.DISTANCE_PHENO_HIGH", 0.0);
        steps = parameters.getInteger("GPAT.DISTANCE_PHENO_STEPS", 0);
        if (parameters.contains("SUBSTRATE.DISTANCE_PHENO_COORD_FILE")) {
            String coordFile = parameters.getString("SUBSTRATE.DISTANCE_PHENO_COORD_FILE");
            coords = importCoordFile(coordFile);
        }
    }

    public double distance(Tree a, Tree b, int output) {
        if (coords == null) {
            return distance(a, b);
        }

        int cnt = 0;
        int n = a.getNumOfInputs();
        TreeInputs treeInputs = new TreeInputs(n);
        double sum = 0.0;
        for (double[][] coord : coords) {
            for (double[] inputs : coord) {
                treeInputs.loadInputs(inputs);
                double diff = a.evaluate(treeInputs) - b.evaluate(treeInputs);
//                System.out.println(diff);
                sum += diff * diff;
                cnt++;
            }
        }
//        System.out.println("---------------");
//        System.out.println(sum/cnt);
        return sum / cnt;
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

    static double[][][] importCoordFile(String coordFile) {
        File file = new File(coordFile);
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            Map<Integer, List<Object>> coordMap = new HashMap<Integer, List<Object>>();
            int maxOuput = 0;
            for (String line : lines) {
                String[] split = line.split(" ");
                double[] converted = new double[split.length - 1];
                for (int i = 0; i < split.length - 1; i++) {
                    converted[i] = Double.valueOf(split[i]);
                }
                int output = Integer.valueOf(split[split.length - 1]);
                if (output > maxOuput) {
                    maxOuput = output;
                }
                if (coordMap.containsKey(output)) {
                    coordMap.get(output).add(converted);
                } else {
                    List<Object> l = new ArrayList<Object>();
                    l.add(converted);
                    coordMap.put(output, l);
                }
            }
            double[][][] ret = new double[maxOuput + 1][][];
            for (int i = 0; i <= maxOuput; i++) {
                List<Object> l = coordMap.get(i);
                ret[i] = new double[l.size()][];
                int cnt = 0;
                for (Object o : l) {
                    ret[i][cnt++] = (double[]) o;
                }
            }
            return ret;

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
