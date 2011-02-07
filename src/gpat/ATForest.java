package gpat;

import common.RND;
import common.evolution.EvaluationInfo;
import gp.IGPForest;
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
public class ATForest implements IGPForest, Comparable, Serializable {
    ATTree[] trees;
    private double fitness = -Double.MAX_VALUE;
    private EvaluationInfo evaluationInfo;
    private int generationOfOrigin;
    private TreeInputs treeInputs;
    private ATInnovationHistory innovationHistory;

    private ATForest(int generationOfOrigin, int numOfInputs) {
        this.generationOfOrigin = generationOfOrigin;
        this.treeInputs = new TreeInputs(numOfInputs);
    }

    public static ATForest createEmpty() {
        return new ATForest(0, 0);
    }

    public static ATForest createRandom(int generationOfOrigin, int numOfInputs, int numOfOutputs, ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        ATForest forest = new ATForest(generationOfOrigin, numOfInputs);
        forest.trees = new ATTree[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {
            forest.trees[i] = ATTree.createMinimalSubstrate(nodeCollection, innovationHistory);
        }
        return forest;
    }

    public ATForest mutate(int generationOfOrigin) {
        ATForest forest = new ATForest(generationOfOrigin, this.getNumOfInputs());
        forest.trees = new ATTree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            ATTree toMutate = this.trees[i].copy();
            if (RND.getDouble() < GPAT.MUTATION_ADD_LINK) {
                toMutate.mutateAddLink();

            }
            if (RND.getDouble() < GPAT.MUTATION_ADD_NODE) {
                toMutate.mutateAddNode();
            }
            toMutate.mutateSwitchConstantLocks();
            toMutate.mutateConstants();
            forest.trees[i] = toMutate;
        }
        forest.setFitness(Double.NaN);
        forest.setEvaluationInfo(new EvaluationInfo(Double.NaN));
        return forest;

    }

    public ATForest eliteCopy(int generationOfOrigin) {
        ATForest forest = new ATForest(generationOfOrigin, this.getNumOfInputs());
        forest.trees = new ATTree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            ATTree eliteCopy = this.trees[i].copy();
            eliteCopy.elite();
            forest.trees[i] = eliteCopy;
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
        return -new Double(fitness).compareTo(((ATForest) o).fitness);
    }

    @Override
    public String toString() {
        return Arrays.asList(trees) + " F: " + fitness + " G: " + generationOfOrigin;
    }

    public String[] getOrigins() {
        List<String> originList = new ArrayList<String>();
        for (int i = 0; i < trees.length; i++) {
            originList.addAll(trees[i].getOrigin());
        }
        String[] o = originList.toArray(new String[0]);
        return o;
    }

    public String toMathematicaExpression() {
        return Arrays.asList(trees).toString();
    }
}
