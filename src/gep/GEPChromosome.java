package gep;

import common.RND;
import common.evolution.EvaluationInfo;
import gp.IGPForest;
import gp.Node;
import gp.NodeCollection;
import gp.TreeInputs;
import gp.functions.Add;
import gp.functions.Multiply;
import gp.terminals.Constant;
import gp.terminals.RNC;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 4:27:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GEPChromosome implements IGPForest, Comparable, Serializable {
    private Gene[] genes;

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
        forest.genes = new Gene[numOfOutputs];
        for (int i = 0; i < numOfOutputs; i++) {//create as many genes as there are outputs
            forest.genes[i] = forest.createRandomGene(nodeCollection);
        }
        return forest;
    }

    private Gene createRandomGene(NodeCollection nodeCollection) {
        Gene gene = new Gene();

        gene.headTail = new Node[GEP.HEAD_TAIL];
        gene.headTail[0] = nodeCollection.getRandomFunction(); //always function in the root
        for (int i = 1; i < GEP.HEAD; i++) {
            gene.headTail[i] = nodeCollection.getRandomOfAll();
        }
        for (int i = GEP.HEAD; i < GEP.HEAD_TAIL; i++) {
            gene.headTail[i] = nodeCollection.getRandomTerminal();
        }

        gene.dc = new int[GEP.DC];
        for (int i = 0; i < GEP.DC; i++) {
            gene.dc[i] = RND.getInt(0, GEP.C_SIZE - 1);
        }

        gene.constants = new double[GEP.C_SIZE];
        for (int i = 0; i < GEP.C_SIZE; i++) {
            gene.constants[i] = RND.getDouble(-GEP.CONSTANT_AMPLITUDE, GEP.CONSTANT_AMPLITUDE);
        }

        return gene;
    }

    //mutates all genomes

    GEPChromosome mutate(NodeCollection nodeCollection, int generationOfOrigin) {
        GEPChromosome forest = new GEPChromosome(generationOfOrigin, this.getNumOfInputs());
        forest.genes = new Gene[genes.length];
        for (int k = 0; k < genes.length; k++) {

            //for each gene do the actual mutation
            Gene mutated = this.genes[k].clone();
            for (int i = 0; i < GEP.HEAD_TAIL; i++) {
                if (RND.getDouble() < GEP.MUTATION_HEADTAIL_RATE) {
                    if (i < GEP.HEAD) {
                        mutated.headTail[i] = nodeCollection.getRandomOfAll();
                    } else {
                        mutated.headTail[i] = nodeCollection.getRandomTerminal();
                    }
                }
            }

            for (int i = 0; i < GEP.DC; i++) {
                if (RND.getDouble() < GEP.MUTATION_DC_RATE) {
                    mutated.dc[i] = RND.getInt(0, GEP.C_SIZE - 1);
                }
            }

            for (int i = 0; i < GEP.C_SIZE; i++) {
                if (RND.getDouble() < GEP.MUTATION_CAUCHY_PROBABILITY) {
                    mutated.constants[i] += GEP.MUTATION_CAUCHY_POWER * RND.getCauchy();
                }
            }
            forest.genes[k] = mutated;
        }
        //TODO - kam to dat? asi do clone?
        forest.setFitness(Double.NaN);
        forest.setEvaluationInfo(new EvaluationInfo(Double.NaN));
        return forest;

    }

    GEPChromosome invert(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //choose a random gene
        int randomGeneIdx = RND.getIntZero(genes.length);
        Gene inverted = this.genes[randomGeneIdx].clone();

        //and make an inversion
        int[] startStop = new int[2];
        RND.sampleRangeWithoutReplacement(GEP.HEAD, startStop);
        int start = startStop[0] <= startStop[1] ? startStop[0] : startStop[1];
        int stop = startStop[0] > startStop[1] ? startStop[0] : startStop[1];
//        System.out.println("start: " + start + " stop: " + stop);
        while (start < stop) {
            Node t = inverted.headTail[start];
            inverted.headTail[start] = inverted.headTail[stop];
            inverted.headTail[stop] = t;
            start++;
            stop--;
        }
        forest.genes[randomGeneIdx] = inverted;

        return forest;
    }

    /**
     * Selects a source and target genes and performs IS transposition
     * of a sequence to head transposed (but not to root).
     *
     * @param generationOfOrigin to store generation info
     * @return transposed chromosome
     */
    GEPChromosome transposeIS(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //get random transposition sequence length
        int length = RND.getInt(1, GEP.MAX_IS_TRANSPOSITION_LENGTH);

        //source and target genes -> can be same
        int sourceGeneIdx = RND.getIntZero(genes.length);
        int targetGeneIdx = RND.getIntZero(genes.length);
//        System.out.println("sourceGeneIdx: " + sourceGeneIdx + " targetGeneIdx: " + targetGeneIdx);

        //extract transposition sequence, truncate if required
        //note, that this differs from PyGEP where the sequence is
        //truncated to the head
        int start = RND.getIntZero(GEP.HEAD_TAIL);
        int end = start + length - 1;
        end = end >= GEP.HEAD_TAIL ? GEP.HEAD_TAIL - 1 : end;
//        System.out.println("start: " + start + " end: " + end);
        length = end - start + 1; //recompute length

        //offset into target, not root!
        int offset = RND.getInt(1, GEP.HEAD - 1);
//        System.out.println("offset: " + offset);

        //determine real targetLength (might be truncated to head)
        int allowableTargetLength = GEP.HEAD - offset;
        int targetLength = length <= allowableTargetLength ? length : allowableTargetLength;
//        System.out.println("targetLength = " + targetLength);

        //extract transposed sequence first
        Node[] seq = new Node[targetLength];
        System.arraycopy(genes[sourceGeneIdx].headTail, start, seq, 0, seq.length);
//        System.out.println("seq: " + ArrayUtils.toString(seq));

        //now copy sequence over target's head
        System.arraycopy(seq, 0, forest.genes[targetGeneIdx].headTail, offset, targetLength);

        //and also shifted rest of the original head
        System.arraycopy(genes[targetGeneIdx].headTail, offset,
                forest.genes[targetGeneIdx].headTail, offset + targetLength,
                GEP.HEAD - offset - targetLength);

        return forest;
    }

    /**
     * Selects a source and target genes and performs RIS transposition
     * of a sequence to the root.
     *
     * @param generationOfOrigin to store generation info
     * @return transposed chromosome
     */
    GEPChromosome transposeRIS(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //get random transposition sequence length
        int length = RND.getInt(1, GEP.MAX_RIS_TRANSPOSITION_LENGTH);

        //source and target genes -> can be same
        int sourceGeneIdx = RND.getIntZero(genes.length);
        int targetGeneIdx = RND.getIntZero(genes.length);
//        System.out.println("sourceGeneIdx: " + sourceGeneIdx + " targetGeneIdx: " + targetGeneIdx);

        //search for all function indices in the head of the source gene
        ArrayList<Integer> functionIndices = new ArrayList<Integer>();
        for (int i = 0; i < GEP.HEAD; i++) {
            if (genes[sourceGeneIdx].headTail[i].getArity() > 0) {
                functionIndices.add(i);
            }
        }

//        System.out.println("function indices: " + functionIndices);

        //do nothing if there is no function
        if (functionIndices.size() == 0) {
            return forest;
        }

        //pick one, truncate if required
        int start = functionIndices.get(RND.getIntZero(functionIndices.size()));
        int end = start + length;
        end = end >= GEP.HEAD_TAIL ? GEP.HEAD_TAIL - 1 : end;
//        System.out.println("start: " + start + " end: " + end);
        length = end - start + 1; //recompute length

        //extract transposed sequence first
        Node[] seq = new Node[length];
        System.arraycopy(genes[sourceGeneIdx].headTail, start, seq, 0, seq.length);
//        System.out.println("seq: " + ArrayUtils.toString(seq));

        //copy the sequence to the root of the target gene
        System.arraycopy(seq, 0, forest.genes[targetGeneIdx].headTail, 0, length);

        //and also shifted rest of the original head
        System.arraycopy(genes[targetGeneIdx].headTail, 0,
                forest.genes[targetGeneIdx].headTail, length,
                GEP.HEAD - length);

        return forest;
    }

    /**
     * Selects a gene and performs Gene transposition.
     * The selected gene is removed from the chromosome and
     * relocated to its start.
     * <p/>
     * This implementation matches the book not PyGEP.
     *
     * @param generationOfOrigin to store generation info
     * @return transposed chromosome
     */
    GEPChromosome transposeGene(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //selected gene index
        int geneIdx = RND.getIntZero(genes.length);
//        System.out.println("geneIdx: " + geneIdx);
        Gene gene = forest.genes[geneIdx];

        //shift genes
        for (int i = geneIdx; i > 0; i--) {
            forest.genes[i] = forest.genes[i - 1];
        }
        //place the selected gene to the start of chromosome
        forest.genes[0] = gene;
        return forest;
    }

    public int getNumOfInputs() {
        return treeInputs.getNumOfInputs();
    }

    public void loadInputs(double[] inputs) {
        treeInputs.loadInputs(inputs);
    }

    public double[] getOutputs() {
        double[] outputs = new double[genes.length];
        for (int i = 0; i < genes.length; i++) {
            outputs[i] = genes[i].evaluate(treeInputs);
        }
        return outputs;
    }

    public int compareTo(Object o) {
        return -new Double(fitness).compareTo(((GEPChromosome) o).fitness);
    }

    private GEPChromosome copy(int generationOfOrigin) {
        GEPChromosome c = new GEPChromosome(generationOfOrigin, getNumOfInputs());
        c.genes = new Gene[genes.length];
        for (int i = 0; i < genes.length; i++) {
            c.genes[i] = genes[i].clone();
        }

        return c;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Gene gene : genes) {
            builder.append(gene).append('\n');
        }
        return builder.append(" F: ").append(fitness).append(" G: ").append(generationOfOrigin).toString();
    }

    public String toStringKarva() {
        StringBuilder builder = new StringBuilder();
        for (Gene gene : genes) {
            builder.append(gene.toStringKarva()).append('\n');
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        Node[] functions = new Node[]{new Add(), new Multiply()};
        Node[] terminals = new Node[]{new RNC(), new Constant(1), new Constant(2)};
        GEP gep = new GEP(null, functions, terminals);
        NodeCollection nc = new NodeCollection(functions, terminals);
        RND.initializeTime();
        GEPChromosome c1 = createRandom(1, 0, 1, nc);
        System.out.println(c1.toStringKarva());
        System.out.println("-------- mutate");
        GEPChromosome c2 = c1.mutate(nc, 1);
        System.out.println(c2.toStringKarva());
        System.out.println("-------- invert");
        GEPChromosome c3 = c2.invert(1);
        System.out.println(c3.toStringKarva());
        System.out.println("-------- transpose IS");
        GEPChromosome c4 = c3.transposeIS(1);
        System.out.println(c4.toStringKarva());
        System.out.println("-------- transpose RIS");
        GEPChromosome c5 = c4.transposeRIS(1);
        System.out.println(c5.toStringKarva());
        System.out.println("-------- transpose Gene");
        GEPChromosome c6 = c5.transposeGene(1);
        System.out.println(c6.toStringKarva());
    }

    // ----------------------------------------------------------------------------------------------
    // -------- GENE --------------------------------------------------------------------------------
    //   this class represents a single gene = tree
    // ----------------------------------------------------------------------------------------------

    private class Gene {
        private Node root = null;

        private Node[] headTail;
        private int[] dc;
        private double[] constants;

        private int ptr;
        private int constPtr;

        public double evaluate(TreeInputs treeInputs) {
            buildTree();
            return root.evaluate(treeInputs);
        }

        private void buildTree() {
            if (root == null) {
                ptr = 0;
                constPtr = 0;
                root = buildTree(headTail[ptr++], 0);
            }
        }

        private Node buildTree(Node node, int depth) {
            Node[] children = new Node[node.getArity()];
            if (node instanceof RNC) {
                double value = constants[dc[constPtr++]];
                return new RNC(depth, value);
            }
            for (int i = 0; i < node.getArity(); i++) {
                children[i] = headTail[ptr++];
            }
            for (int i = 0; i < node.getArity(); i++) {
                children[i] = buildTree(children[i], depth + 1);
            }
            return node.create(depth, children);
        }

        protected Gene clone() {
            Gene gene = new Gene();

            gene.headTail = this.headTail.clone();
            gene.dc = this.dc.clone();
            gene.constants = this.constants.clone();

            return gene;
        }

        @Override
        public String toString() {
            buildTree();
            return toStringKarva() + "\n" + root.toString();
        }

        public String toStringKarva() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < GEP.HEAD_TAIL; i++) {
                builder.append(headTail[i]);
                if (i == GEP.HEAD - 1) {
                    builder.append('|');
                } else if (i != GEP.HEAD_TAIL - 1) {
                    builder.append(',');
                }
            }
            builder.append('|');
            for (int i = 0; i < GEP.DC; i++) {
                builder.append(dc[i]);
                if (i != GEP.DC - 1) {
                    builder.append(',');
                }
            }
            builder.append(" {");
            for (int i = 0; i < GEP.C_SIZE; i++) {
                builder.append(constants[i]);
                if (i != GEP.C_SIZE - 1) {
                    builder.append(',');
                }

            }
            builder.append("}");
            return builder.toString();
        }
    }

}
