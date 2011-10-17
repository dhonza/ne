package gpaac;

import common.RND;
import common.evolution.EvaluationInfo;
import gp.IGPForest;
import gp.NodeCollection;
import gp.TreeInputs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public int getId() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public int getParentId() {
        throw new IllegalStateException("Not yet implemented!");
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
        for (int i = 0; i < trees.length; i++) {
            AACTree toMutate = this.trees[i];
            toMutate.resetOrigin();
            if (RND.getDouble() < GPAAC.MUTATION_SUBTREE_PROBABLITY) {
                toMutate = toMutate.mutateSubtree(nodeCollection);

            }
            if (RND.getDouble() < GPAAC.MUTATION_NODE_PROBABLITY) {
                toMutate = toMutate.mutateNode(nodeCollection);
            }
            if (RND.getDouble() < GPAAC.MUTATION_ADD_CHILD) {
                toMutate = toMutate.mutateAddChild(nodeCollection);
            }
            toMutate = toMutate.mutateSwitchConstantLock();
            toMutate = toMutate.mutateConstants();
            toMutate = toMutate.mutateReplaceConstants();
            forest.trees[i] = toMutate;
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

    public void propagate() {
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

    public String toMathematicaExpression() {
        return "AACForest: NOT IMPLEMENTED!";
    }

    public String[] getOrigins() {
        List<String> originList = new ArrayList<String>();
        for (int i = 0; i < trees.length; i++) {
            originList.addAll(trees[i].getOrigin());
        }
        String[] o = originList.toArray(new String[0]);
        return o;
    }

    public int getMaxTreeDepth() {
        int maxDepth = 0;
        for (AACTree tree : trees) {
            int d = tree.getDepth();
            maxDepth = maxDepth < d ? d : maxDepth;
        }
        return maxDepth;
    }

    public double getAverageArity() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public int getNumOfConstants() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public int getNumOfLeaves() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public int getNumOfNodes() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public double[] getConstants() {
        throw new IllegalStateException("Not yet implemented!");
    }

    public void setConstants(double[] constants) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public IGPForest copy() {
        throw new IllegalStateException("Not yet implemented!");
    }
}
