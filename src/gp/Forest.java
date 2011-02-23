package gp;

import common.RND;
import common.evolution.EvaluationInfo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 6:21:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class Forest implements IGPForest, Comparable, Serializable {
    Tree[] trees;
    private double fitness = -Double.MAX_VALUE;
    private EvaluationInfo evaluationInfo;
    private int generationOfOrigin;
    private TreeInputs treeInputs;

    private Forest(int generationOfOrigin, int numOfInputs) {
        this.generationOfOrigin = generationOfOrigin;
        this.treeInputs = new TreeInputs(numOfInputs);
    }

    public Forest() {
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

    public static Forest createEmpty() {
        return new Forest(0, 0);
    }

    public static Forest createRandom(int generationOfOrigin, int numOfInputs, int numOfOutputs, NodeCollection nodeCollection) {
        Forest forest = new Forest(generationOfOrigin, numOfInputs);
        forest.trees = new Tree[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {
            forest.trees[i] = Tree.createRandom(nodeCollection);
        }
        return forest;
    }

    public Forest mutate(NodeCollection nodeCollection, int generationOfOrigin) {
        Forest forest = new Forest(generationOfOrigin, this.getNumOfInputs());
        forest.trees = new Tree[trees.length];
        //TODO nebo vybrat jeden?
        for (int i = 0; i < trees.length; i++) {
            if (RND.getDouble() < GP.MUTATION_SUBTREE_PROBABLITY) {
                forest.trees[i] = this.trees[i].mutateSubtree(nodeCollection);
            } else {
                forest.trees[i] = this.trees[i].mutateNode(nodeCollection);
            }
        }
        forest.setFitness(Double.NaN);
        EvaluationInfo evaluationInfo = new EvaluationInfo(Double.NaN);
        forest.setEvaluationInfo(evaluationInfo);
        return forest;

    }

    public double distance(Forest other) {
        double distances = 0.0;
        for (int i = 0; i < trees.length; i++) {
            distances += this.trees[i].distance(other.trees[i]);
        }
        return distances / trees.length;
    }

    public int getNumOfInputs() {
        return treeInputs.getNumOfInputs();
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
        return -new Double(fitness).compareTo(((Forest) o).fitness);
    }

    @Override
    public String toString() {
        return Arrays.asList(trees) + " F: " + fitness + " G: " + generationOfOrigin;
    }

    public String toMathematicaExpression() {
        StringBuilder b = new StringBuilder("{");
        for (int i = 0; i < trees.length; i++) {
            Tree tree = trees[i];
            b.append(tree.toMathematicaExpression());
            if (i < (trees.length - 1)) {
                b.append(", ");
            }
        }
        b.append("}");
        return b.toString();
    }

    public String innovationToString() {
        String s = "";
        for (Tree tree : trees) {
            s += tree.innovationToString() + ", ";
        }
        return s;
    }

    public String[] getOrigins() {
        String[] o = new String[trees.length];
        for (int i = 0; i < trees.length; i++) {
            o[i] = trees[i].getOrigin();
        }
        return o;
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
