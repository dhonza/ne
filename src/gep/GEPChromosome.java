package gep;

import common.evolution.EvaluationInfo;
import gp.NodeCollection;
import gp.TreeInputs;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 4:27:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEPChromosome implements Comparable, Serializable {
    GEPGenome[] trees;
    private double fitness = -Double.MAX_VALUE;
    private EvaluationInfo evaluationInfo;
    private int generationOfOrigin;
    private TreeInputs treeInputs;

    private GEPChromosome(int generationOfOrigin, int numOfInputs) {
        this.generationOfOrigin = generationOfOrigin;
        this.treeInputs = new TreeInputs(numOfInputs);
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

    public static GEPChromosome createEmpty() {
        return new GEPChromosome(0, 0);
    }

    public static GEPChromosome createRandom(int generationOfOrigin, int numOfInputs, int numOfOutputs, NodeCollection nodeCollection) {
        GEPChromosome forest = new GEPChromosome(generationOfOrigin, numOfInputs);
        forest.trees = new GEPGenome[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {
            forest.trees[i] = GEPGenome.createRandom(nodeCollection);
        }
        return forest;
    }

    public GEPChromosome mutate(NodeCollection nodeCollection, int generationOfOrigin) {
        GEPChromosome forest = new GEPChromosome(generationOfOrigin, this.getNumOfInputs());
        forest.trees = new GEPGenome[trees.length];
        //TODO nebo vybrat jeden?
        for (int i = 0; i < trees.length; i++) {
            forest.trees[i] = this.trees[i].mutate(nodeCollection);
        }
        forest.setFitness(Double.NaN);
        forest.setEvaluationInfo(new EvaluationInfo(Double.NaN));
        return forest;

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
        return -new Double(fitness).compareTo(((GEPChromosome) o).fitness);
    }
}
