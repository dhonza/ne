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
        forest.setFitness(Double.NaN);
        forest.setEvaluationInfo(new EvaluationInfo(Double.NaN));

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

            //mutate DC
            for (int i = 0; i < GEP.DC; i++) {
                if (RND.getDouble() < GEP.MUTATION_DC_RATE) {
                    mutated.dc[i] = RND.getInt(0, GEP.C_SIZE - 1);
                }
            }

            //directly mutate constants
            for (int i = 0; i < GEP.C_SIZE; i++) {
                if (RND.getDouble() < GEP.MUTATION_CAUCHY_PROBABILITY) {
                    mutated.constants[i] += GEP.MUTATION_CAUCHY_POWER * RND.getCauchy();
                }
            }

            forest.genes[k] = mutated;
        }
        return forest;

    }

    GEPChromosome invert(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //choose a random gene
        int randomGeneIdx = RND.getIntZero(genes.length);
        Gene inverted = forest.genes[randomGeneIdx];

        //and make an inversion
        int[] startStop = new int[2];
        RND.sampleRangeWithoutReplacementSorted(GEP.HEAD, startStop);
        int start = startStop[0];
        int stop = startStop[1];
//        System.out.println("start: " + start + " stop: " + stop);
        while (start < stop) {
            Node t = inverted.headTail[start];
            inverted.headTail[start] = inverted.headTail[stop];
            inverted.headTail[stop] = t;
            start++;
            stop--;
        }

        return forest;
    }

    GEPChromosome invertDC(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //choose a random gene
        int randomGeneIdx = RND.getIntZero(genes.length);
        Gene inverted = forest.genes[randomGeneIdx];

        //and make an inversion
        int[] startStop = new int[2];
        RND.sampleRangeWithoutReplacementSorted(GEP.DC, startStop);
        int start = startStop[0];
        int stop = startStop[1];
//        System.out.println("start: " + start + " stop: " + stop);
        while (start < stop) {
            int t = inverted.dc[start];
            inverted.dc[start] = inverted.dc[stop];
            inverted.dc[stop] = t;
            start++;
            stop--;
        }

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

        //random point in the head
        int functionIdx = RND.getIntZero(GEP.HEAD);
        boolean foundFunction = false;

        //go downstream and find a function
        for (int i = functionIdx; i < GEP.HEAD; i++) {
            if (genes[sourceGeneIdx].headTail[i].getArity() > 0) {
                foundFunction = true;
                functionIdx = i;
                break;
            }
        }
//        System.out.println("functionIdx: " + functionIdx);

        //do nothing if there is no function
        if (!foundFunction) {
            return forest;
        }

        //pick one, truncate if required
        int start = functionIdx;
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

    GEPChromosome transposeDC(int generationOfOrigin) {
        GEPChromosome forest = copy(generationOfOrigin);

        //get random transposition sequence length
        int length = RND.getInt(1, GEP.MAX_DC_TRANSPOSITION_LENGTH);

        //source and target genes -> can be same
        int sourceGeneIdx = RND.getIntZero(genes.length);
        int targetGeneIdx = RND.getIntZero(genes.length);
        System.out.println("sourceGeneIdx: " + sourceGeneIdx + " targetGeneIdx: " + targetGeneIdx);

        //extract transposition sequence, truncate if required
        //note, that this differs from PyGEP where the sequence is
        //truncated to the head
        int start = RND.getIntZero(GEP.DC);
        int end = start + length - 1;
        end = end >= GEP.DC ? GEP.DC - 1 : end;
//        System.out.println("start: " + start + " end: " + end);
        length = end - start + 1; //recompute length

        //offset into target
        int offset = RND.getInt(0, GEP.DC - 1);
//        System.out.println("offset: " + offset);

        //determine real targetLength (might be truncated to head)
        int allowableTargetLength = GEP.DC - offset;
        int targetLength = length <= allowableTargetLength ? length : allowableTargetLength;
//        System.out.println("targetLength = " + targetLength);

        //extract transposed sequence first
        int[] seq = new int[targetLength];
        System.arraycopy(genes[sourceGeneIdx].dc, start, seq, 0, seq.length);
//        System.out.println("seq: " + ArrayUtils.toString(seq));

        //now copy sequence over target's head
        System.arraycopy(seq, 0, forest.genes[targetGeneIdx].dc, offset, targetLength);

        //and also shifted rest of the original head
        System.arraycopy(genes[targetGeneIdx].dc, offset,
                forest.genes[targetGeneIdx].dc, offset + targetLength,
                GEP.DC - offset - targetLength);

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

    GEPChromosome[] crossoverOnePoint(GEPChromosome other, int generationOfOrigin) {
        GEPChromosome a = this.copy(generationOfOrigin);
        GEPChromosome b = other.copy(generationOfOrigin);

        //select crossover point
        int geneIdx = RND.getIntZero(a.genes.length);
        int cIdx = RND.getInt(0, GEP.HEAD_TAIL);
//        System.out.println("geneIdx: " + geneIdx + " cIdx: " + cIdx);


        //the crossover happens in geneIdx gene
        //otherwise it happens just after
        if (cIdx < GEP.HEAD_TAIL) {
            for (int i = cIdx; i < GEP.HEAD_TAIL; i++) {
                Node t = a.genes[geneIdx].headTail[i];
                a.genes[geneIdx].headTail[i] = b.genes[geneIdx].headTail[i];
                b.genes[geneIdx].headTail[i] = t;
            }
        }

        //now swap the rest of genes
        for (int i = geneIdx + 1; i < a.genes.length; i++) {
            Gene t = a.genes[i];
            a.genes[i] = b.genes[i];
            b.genes[i] = t;
        }

        return new GEPChromosome[]{a, b};
    }

    GEPChromosome[] crossoverTwoPoint(GEPChromosome other, int generationOfOrigin) {
        GEPChromosome a = this.copy(generationOfOrigin);
        GEPChromosome b = other.copy(generationOfOrigin);

        //select crossover points
        int[] g = new int[2]; //genes
        RND.sampleRangeWithoutReplacementSorted(a.genes.length, g);
        int[] c = new int[2]; //points inside
        RND.sampleRangeWithoutReplacementSorted(GEP.HEAD_TAIL + 1, c);
//        System.out.println("g1: " + g[0] + " c1: " + c[0]);
//        System.out.println("g2: " + g[1] + " c2: " + c[1]);

        int cIdx = c[0];
        for (int i = g[0]; i <= g[1]; i++) {//all swapped genes
            for (int j = cIdx; j < GEP.HEAD_TAIL; j++) {//at first go exactly from the first crossover point
                if (i < g[1] || j < c[1]) {// if not second crossover point
                    Node t = a.genes[i].headTail[j];
                    a.genes[i].headTail[j] = b.genes[i].headTail[j];
                    b.genes[i].headTail[j] = t;
                }
            }
            cIdx = 0;//go through whole following genes 
        }
        return new GEPChromosome[]{a, b};
    }

    GEPChromosome[] crossoverGene(GEPChromosome other, int generationOfOrigin) {
        GEPChromosome a = this.copy(generationOfOrigin);
        GEPChromosome b = other.copy(generationOfOrigin);

        //choose gene to swap
        int geneIdx = RND.getIntZero(a.genes.length);
        System.out.println("geneIdx: " + geneIdx);

        Gene t = a.genes[geneIdx];
        a.genes[geneIdx] = b.genes[geneIdx];
        b.genes[geneIdx] = t;

        return new GEPChromosome[]{a, b};
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

    GEPChromosome copy(int generationOfOrigin) {
        GEPChromosome c = new GEPChromosome(generationOfOrigin, getNumOfInputs());
        c.genes = new Gene[genes.length];
        for (int i = 0; i < genes.length; i++) {
            c.genes[i] = genes[i].clone();
        }
        c.setFitness(Double.NaN);
        c.setEvaluationInfo(new EvaluationInfo(Double.NaN));
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

        int genes = 2;

        GEPChromosome c1 = createRandom(1, 0, genes, nc);
        System.out.println(c1.toStringKarva());
        System.out.println("-------- mutate");
        GEPChromosome c2 = c1.mutate(nc, 1);
        System.out.println(c2.toStringKarva());
        System.out.println("-------- invert");
        GEPChromosome c3 = c2.invert(1);
        System.out.println(c3.toStringKarva());
        System.out.println("-------- invertDC");
        c3 = c3.invertDC(1);
        System.out.println(c3.toStringKarva());
        System.out.println("-------- transpose IS");
        GEPChromosome c4 = c3.transposeIS(1);
        System.out.println(c4.toStringKarva());
        System.out.println("-------- transpose RIS");
        GEPChromosome c5 = c4.transposeRIS(1);
        System.out.println(c5.toStringKarva());
        System.out.println("-------- transpose DC");
        c5 = c5.transposeDC(1);
        System.out.println(c5.toStringKarva());

/*
        System.out.println("-------- transpose Gene");
        GEPChromosome c6 = c5.transposeGene(1);
        System.out.println(c6.toStringKarva());

        System.out.println("-------- crossover One Point");
        c1 = createRandom(1, 0, genes, nc);
        c2 = createRandom(1, 0, genes, nc);
        System.out.println(c1.toStringKarva());
        System.out.println(c2.toStringKarva());
        System.out.println("----------------------------");
        GEPChromosome[] c7 = c1.crossoverOnePoint(c2, 1);
        System.out.println(c7[0].toStringKarva());
        System.out.println(c7[1].toStringKarva());

        System.out.println("-------- crossover Two Point");
        c1 = createRandom(1, 0, genes, nc);
        c2 = createRandom(1, 0, genes, nc);
        System.out.println(c1.toStringKarva());
        System.out.println(c2.toStringKarva());
        System.out.println("----------------------------");
        GEPChromosome[] c8 = c1.crossoverTwoPoint(c2, 1);
        System.out.println(c8[0].toStringKarva());
        System.out.println(c8[1].toStringKarva());

        System.out.println("-------- crossover Gene");
        c1 = createRandom(1, 0, genes, nc);
        c2 = createRandom(1, 0, genes, nc);
        System.out.println(c1.toStringKarva());
        System.out.println(c2.toStringKarva());
        System.out.println("----------------------------");
        GEPChromosome[] c9 = c1.crossoverGene(c2, 1);
        System.out.println(c9[0].toStringKarva());
        System.out.println(c9[1].toStringKarva());
  //*/
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
            gene.root = null;
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
