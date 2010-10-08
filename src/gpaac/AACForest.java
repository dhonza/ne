package gpaac;

import common.RND;
import common.evolution.EvaluationInfo;
import gp.IGPForest;
import gp.NodeCollection;
import gp.TreeInputs;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 8, 2010
 * Time: 5:00:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class AACForest implements IGPForest, Comparable, Serializable {
    AACTree[] trees;
    private double fitness = -Double.MAX_VALUE;
    private EvaluationInfo evaluationInfo;
    private int generationOfOrigin;
    private TreeInputs treeInputs;

    private AACForest(int generationOfOrigin, int numOfInputs) {
        this.generationOfOrigin = generationOfOrigin;
        this.treeInputs = new TreeInputs(numOfInputs);
    }

    public static AACForest createEmpty() {
        return new AACForest(0, 0);
    }

    public static AACForest createRandom(int generationOfOrigin, int numOfInputs, int numOfOutputs, NodeCollection nodeCollection) {
        AACForest forest = new AACForest(generationOfOrigin, numOfInputs);
        forest.trees = new AACTree[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {
            forest.trees[i] = AACTree.createRandom(nodeCollection);
        }
        return forest;
    }

    public AACForest mutate(NodeCollection nodeCollection, int generationOfOrigin) {
        AACForest forest = new AACForest(generationOfOrigin, this.getNumOfInputs());
        forest.trees = new AACTree[trees.length];
        //TODO nebo vybrat jeden?
        for (int i = 0; i < trees.length; i++) {
            if (RND.getDouble() < GPAAC.MUTATION_SUBTREE_PROBABLITY) {
                forest.trees[i] = this.trees[i].mutateSubtree(nodeCollection);
            } else {
                forest.trees[i] = this.trees[i].mutateNode(nodeCollection);
            }
            forest.trees[i] = forest.trees[i].mutateConstants();
        }
        forest.setFitness(Double.NaN);
        forest.setEvaluationInfo(new EvaluationInfo(Double.NaN));
        return forest;

    }

    public int getNumOfInputs() {
        return treeInputs.getNumOfInputs();
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public EvaluationInfo getEvaluationInfo() {
        return evaluationInfo;
    }

    public void setEvaluationInfo(EvaluationInfo evaluationInfo) {
        this.evaluationInfo = evaluationInfo;
    }

    public void loadInputs(double[] inputs) {
        treeInputs.loadInputs(inputs);
    }

    public double[] getOutputs() {
        double[] outputs = new double[trees.length];
        for (int i = 0; i < trees.length; i++) {
            outputs[i] = trees[i].evaluate(treeInputs);
        }
        return outputs;
    }

    public int compareTo(Object o) {
        return -new Double(fitness).compareTo(((AACForest) o).fitness);
    }

    @Override
    public String toString() {
        return Arrays.asList(trees) + " F: " + fitness + " G: " + generationOfOrigin;
    }
}
