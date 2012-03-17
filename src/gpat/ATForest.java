package gpat;

import common.ArrayHelper;
import common.evolution.EvaluationInfo;
import common.evolution.GenomeCounter;
import gp.IGPForest;
import gp.TreeInputs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 8, 2010
 * Time: 5:00:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATForest implements IGPForest, Comparable, Serializable {
    final private int id;
    private int parentId = -1;

    ATTree[] trees;
    private double fitness = -Double.MAX_VALUE;
    private EvaluationInfo evaluationInfo;
    private int generationOfOrigin;
    private TreeInputs treeInputs;
    private ATInnovationHistory innovationHistory;

    private ATForest(int generationOfOrigin, int numOfInputs, int parentId) {
        this.generationOfOrigin = generationOfOrigin;
        this.treeInputs = new TreeInputs(numOfInputs);
        this.id = GenomeCounter.INSTANCE.getNext();
        this.parentId = parentId;
    }

    private ATForest(int generationOfOrigin, int numOfInputs) {
        this(generationOfOrigin, numOfInputs, -1);
    }

    public static ATForest createEmpty() {
        return new ATForest(0, 0);
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public static ATForest createRandom(int generationOfOrigin, int numOfInputs, int numOfOutputs, ATNodeCollection nodeCollection, ATInnovationHistory innovationHistory) {
        ATForest forest = new ATForest(generationOfOrigin, numOfInputs);
        forest.trees = new ATTree[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {
            forest.trees[i] = ATTree.createMinimalSubstrate(nodeCollection, innovationHistory);
//            forest.trees[i] = ATTree.createMinimalSubstrateWithInputs(nodeCollection, innovationHistory);
        }
        forest.initEvaluationInfo();
        return forest;
    }

    public ATForest mutate(int generationOfOrigin) {
        ATForest forest = new ATForest(generationOfOrigin, this.getNumOfInputs(), id);
        forest.trees = new ATTree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            ATTree toMutate = this.trees[i].copy();

            toMutate.mutateStructure();
            toMutate.mutateConstants();
            toMutate.mutateSwitchLocks();

            forest.trees[i] = toMutate;
        }
        forest.initEvaluationInfo();
        return forest;
    }

    public ATForest mutateHeavyStructure(int generationOfOrigin) {
        ATForest forest = new ATForest(generationOfOrigin, this.getNumOfInputs(), id);
        forest.trees = new ATTree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            ATTree toMutate = this.trees[i].copy();

            for (int j = 0; j < GPAT.MUTATION_HEAVY_POWER; j++) {
                toMutate.mutateStructure();
            }

            forest.trees[i] = toMutate;
        }
        forest.initEvaluationInfo();
        return forest;
    }

    public ATForest eliteCopy(int generationOfOrigin) {
        ATForest forest = new ATForest(generationOfOrigin, this.getNumOfInputs(), id);
        forest.trees = new ATTree[trees.length];
        for (int i = 0; i < trees.length; i++) {
            ATTree eliteCopy = this.trees[i].copy();
            eliteCopy.elite();
            forest.trees[i] = eliteCopy;
        }
        forest.initEvaluationInfo();
        return forest;
    }

    private void initEvaluationInfo() {
        setFitness(Double.NaN);
        setEvaluationInfo(new EvaluationInfo(Double.NaN));
        getEvaluationInfo().put("G_NODE_NUM", getNumOfNodes());
        getEvaluationInfo().put("G_CONST_NUM", getNumOfConstants());
        getEvaluationInfo().put("G_MAX_DEPTH", getMaxTreeDepth());
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
        return -new Double(fitness).compareTo(((ATForest) o).fitness);
    }

    public String[] getOrigins() {
        List<String> originList = new ArrayList<String>();
        for (int i = 0; i < trees.length; i++) {
            originList.addAll(trees[i].getOrigin());
        }
        String[] o = originList.toArray(new String[0]);
        return o;
    }

    public double getAverageArity() {
        double arity = 0.0;
        for (ATTree tree : trees) {
            arity += tree.getAverageArity();
        }
        return arity / trees.length;
    }

    public int getNumOfConstants() {
        int numOfConstants = 0;
        for (ATTree tree : trees) {
            numOfConstants += tree.getNumOfConstants();
        }
        return numOfConstants;
    }

    public List<Integer> getTreeDepths() {
        List<Integer> l = new LinkedList<Integer>();
        for (ATTree tree : trees) {
            l.add(tree.getDepth());
        }
        return l;
    }

    public int getMaxTreeDepth() {
        int maxDepth = 0;
        for (ATTree tree : trees) {
            int d = tree.getDepth();
            maxDepth = maxDepth < d ? d : maxDepth;
        }
        return maxDepth;
    }

    public int getNumOfLeaves() {
        int numOfNodes = 0;
        for (ATTree tree : trees) {
            numOfNodes += tree.getNumOfLeaves();
        }
        return numOfNodes;
    }

    public int getNumOfNodes() {
        int numOfNodes = 0;
        for (ATTree tree : trees) {
            numOfNodes += tree.getNumOfNodes();
        }
        return numOfNodes;
    }

    public double[] getConstants() {
        double[][] constants = new double[trees.length][];
        for (int i = 0; i < trees.length; i++) {
            constants[i] = trees[i].getConstants();
        }
        return ArrayHelper.flatten(constants);
    }

    public void setConstants(double[] constants) {
        int[] parts = new int[trees.length];
        for (int i = 0; i < trees.length; i++) {
            parts[i] = trees[i].getNumOfConstants();
        }
        double[][] treeConstants = ArrayHelper.partition(constants, parts);
        for (int i = 0; i < trees.length; i++) {
            trees[i].setConstants(treeConstants[i]);
        }
    }


    public IGPForest copy() {
        return eliteCopy(generationOfOrigin);
    }

    @Override
    public String toString() {
        return Arrays.asList(trees) + " F: " + fitness + " G: " + generationOfOrigin + " #N: " + getNumOfNodes() +
                " #C: " + getNumOfConstants() + " D:" + getTreeDepths();
    }

    public String toMathematicaExpression() {
        StringBuilder s = new StringBuilder("{");
        for (int i = 0, treesLength = trees.length - 1; i < treesLength; i++) {
            ATTree tree = trees[i];
            s.append(tree.toMathematicaExpression());
            s.append(", ");
        }
        s.append(trees[trees.length - 1].toMathematicaExpression());
        s.append("}");
        return s.toString();
    }
}
