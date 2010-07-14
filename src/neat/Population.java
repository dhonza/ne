package neat;

import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.evolution.ParallelPopulationEvaluator;
import common.net.linked.Net;
import common.net.linked.NetStorage;
import common.xml.XMLSerialization;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * This class is a predecesor of all Population classes.
 */
public abstract class Population {

    /**
     * All the Genomes of the Population.
     */
    Genome[] genomes;

    /**
     * The number of genome evaluations.
     *
     * @see #evaluate
     */
    protected int evaluation = 0;

    /**
     * The number of generation.
     */
    protected int generation = 0;

    /**
     * All the Species of the Population.
     */
    protected LinkedList<Species> species;

    /**
     * The maximum Species id ever.
     */
    protected int maxSpeciesId = 0;


    protected GlobalInnovation globalInnovation;

    /**
     * The best Genome of current generation.
     */
    protected Genome bestOfGeneration;

    /**
     * The best so far Genome (the best of all generations).
     */
    protected Genome bestSoFar;

    /**
     * The last innovation (based on fitness).
     */
    protected int lastInnovation = 0;

    /**
     * Here we store Species history (for stats etc.)
     */
    //TODO again enable
//    protected SpeciesHistory speciesHistory;

    protected OutputStream logging;

    /**
     * You have to implement Evaluable interface in order to compute fitness.
     */
    protected Evaluable<Genome>[] perThreadEvaluators;
    protected ParallelPopulationEvaluator<Genome> populationEvaluator;

    protected double[][][] bsfInputs;

    protected double[][][] bsfOutputs;


    public Population(Evaluable<Genome>[] perThreadEvaluators) {
        this.perThreadEvaluators = perThreadEvaluators;
        populationEvaluator = new ParallelPopulationEvaluator<Genome>();
        genomes = new Genome[NEAT.getConfig().populationSize];
        species = new LinkedList<Species>();
//        speciesHistory = new SpeciesHistory(); //(NE.POPULATION_SIZE);
    }

    public Population(Evaluable<Genome>[] perThreadEvaluators, Genome oproto) {
        this(perThreadEvaluators);
        spawn(oproto);
    }

    /**
     * Creates population using ANNs stored in a file
     *
     * @param ofileName file name
     */
    public Population(Evaluable<Genome>[] perThreadEvaluators, String ofileName) {
        Net[] nets = NetStorage.loadMultiple(ofileName);
        this.perThreadEvaluators = perThreadEvaluators;
        NEAT.getConfig().populationSize = nets.length;
        genomes = new Genome[nets.length];
        for (int i = 0; i < nets.length; i++) {
            genomes[i] = new Genome(nets[i]);
        }
        species = new LinkedList<Species>();
        // just sets initial sizes
        globalInnovation = new GlobalInnovationList(nets[0].getMaxLinkId(), nets[0].getMaxNeuronId());
        bestSoFar = genomes[0]; // just for the first step...
        bestOfGeneration = genomes[0];
    }

    public void incrementGeneration() {
        generation++;
        getGlobalInnovation().setGeneration(generation);
    }

    public int getEvaluations() {
        return evaluation;
    }

    public void incrementEvaluation() {
        this.evaluation++;
    }

    public void incrementEvaluation(int ooffset) {
        this.evaluation += ooffset;
    }


    public void setLogging(OutputStream oos) {
        logging = oos;
    }

    /**
     * @return Returns the speciesHistory.
     */
//    public SpeciesHistory getSpeciesHistory() {
//        return speciesHistory;
//    }
    public Genome getBestOfGeneration() {
        return bestOfGeneration;
    }

    /**
     * The Net of the best Genome of current generation.
     *
     * @return network
     */
    public Net getBestOfGenerationNet() {
        return bestOfGeneration.getNet();
    }

    public Genome getBestSoFar() {
        return bestSoFar;
    }

    /**
     * The Net of the best so far Genome (the best of all generations).
     *
     * @return network
     */
    public Net getBestSoFarNet() {
        return bestSoFar.getNet();
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public void setLastInnovation(int lastInnovation) {
        this.lastInnovation = lastInnovation;
    }

    public GlobalInnovation getGlobalInnovation() {
        return globalInnovation;
    }

    public LinkedList getSpecies() {
        return species;
    }

    /**
     * Evaluates the whole Population. This method computes fitness for all
     * Genomes.
     */
    void evaluate() {
        // System.out.println( " Population.evaluate()" );
        Genome tg;
        int n = NEAT.getConfig().populationSize;

        EvaluationInfo[] evaluationInfos = populationEvaluator.evaluate(perThreadEvaluators, Arrays.asList(genomes));
        for (int i = 0; i < n; i++) {
            tg = genomes[i];
            tg.fitness = evaluationInfos[i].getFitness();
            tg.setEvaluationInfo(evaluationInfos[i]);
            tg.evaluated = true; //mark evaluated
            if (tg.fitness > bestOfGeneration.fitness) {
                bestOfGeneration = tg;
            }
        }

        if (bestOfGeneration.fitness > bestSoFar.fitness) {
            bestSoFar = bestOfGeneration;
            lastInnovation = 0;
//            evaluator.storeEvaluation(bestSoFar); // for gradient learning
//            bsfInputs = evaluator.getStoredInputs();
//            bsfOutputs = evaluator.getStoredOutputs();

        } else {
            lastInnovation++;
        }
//        System.out.println(generation + ": -------------Population:EVALUATION: ");
//        if (generation % 8 == 0) {
//            teachGeneration();
//        }
    }

    EvaluationInfo evaluateGeneralization() {
        return populationEvaluator.evaluateGeneralization(perThreadEvaluators, getBestSoFar());
    }

    /**
     * Reproduces the Population. New Genomes are recombined by using genetic
     * operators.
     */
    abstract void reproduce();

    /**
     * Selects the Genomes to survive.
     */
    abstract void select();

    /**
     * Specitates the whole population.
     */
    abstract void speciate();

    /**
     * Retuns actual generation.
     *
     * @return actual generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Creates Genomes of initial population. The size of the population is
     * determined by <i>NE.POPULATION_SIZE </i>. All Genomes of the population
     * have the same topology as <i>og </i> but their weights are randomized. It
     * also initializes <i>GlobalInnovation </i> structure.
     *
     * @param og prototypal Genome
     */
    void spawn(Genome og) {
        // System.out.println( "Population.spawn()" );
        // globalInnovation = new InnovationMatrix( og, 1500,
        // og.linkGenes[og.net.getNumLinks()-1].link.id,
        // og.refInHidOut[og.net.numNeurons-1].id ); //must be enough..
        // TODO pozor tady muze byt chyba
        //globalInnovation = new InnovationList(og.linkGenes[og.net.getNumLinks() - 1].link.id, og.refInHidOut[og.net.getNumNeurons() - 1].id);
        globalInnovation = new GlobalInnovationList(og.getNet().getMaxLinkId(), og.getNet().getMaxNeuronId());

        Genome tg;
        for (int i = 0; i < NEAT.getConfig().populationSize; i++) {
            tg = (Genome) og.clone();
            tg.setPop(this);
            if (NEAT.getConfig().populationInitiallyRandomizeWeights) {
                tg.getNet().randomizeWeights(-NEAT.getConfig().netWeightsScatter, NEAT.getConfig().netWeightsScatter);
            }
            genomes[i] = tg;
        }
        bestSoFar = genomes[0]; // just for the first step...
        bestOfGeneration = genomes[0];
    }

    /*
        void saveSpeciesHistory() {
            speciesHistory.openGeneration(generation);
            for (Object spec : species) {
                Species ts = (Species) spec; // get species
                speciesHistory.addSpecies(generation, ts.getId(), ts.getSize());
            }
            speciesHistory.closeGeneration(generation);
        }
    */

    /**
     * Adds a new Species to the Population. The Genome which didn't fit into
     * any of other already living Species is inserted.
     *
     * @param og the Species' initial Genome
     * @return newly created Species
     */
    Species addSpecies(Genome og) {
        Species ts = new Species(++maxSpeciesId, 50, this);
        /** TODO set the const. properly */
        ts.genomes.add(og);
        species.addLast(ts);
        return ts;
    }

    void storeDistanceMatrix() {
        int n = genomes.length;
        double[][] d = new double[n - 1][];
//        System.out.print("{");
        for (int i = 0; i < n - 1; i++) {
            d[i] = new double[n - i - 1];
//            System.out.print("{");
            for (int j = 0; j < n - i - 1; j++) {
                Genome g1 = genomes[i];
                Genome g2 = genomes[j + i + 1];
                d[i][j] = g1.distance(g2);
                if (j == n - i - 2) {
//                    System.out.print(d[i][j]);
                } else {
//                    System.out.print(d[i][j] + ", ");
                }
            }
//            System.out.println("},");
        }
//        System.out.println("}");
        XMLSerialization.save(d, "d_" + generation + ".xml");

        double[][] d2 = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i < j) {
                    d2[i][j] = d[i][j - 1 - i];
                    d2[j][i] = d2[i][j];
                }
            }
        }
//        BinaryFile.(new File("d_" + generation + ".dat"), d2, "LITTLE_ENDIAN");
//        ASCIIFile af = new ASCIIFile(new File("d_" + generation + ".dat"));
//        af.write(ArrayString.printDoubleArray(d2, ";", ":"), false);
    }


    /**
     * Strores all phenotype networks to a file.
     *
     * @param ofileName file name
     */
    public void savePopulationNetworks(String ofileName) {
        Net[] nets = new Net[genomes.length];
        for (int i = 0; i < genomes.length; i++) {
            nets[i] = genomes[i].getNet();
        }
        NetStorage.saveMultiple(nets, ofileName);
    }

    public double[] getFitnessVector() {
        double[] fv = new double[genomes.length];
        for (int i = 0; i < genomes.length; i++) {
            fv[i] = genomes[i].fitness;

        }
        return fv;
    }

    public EvaluationInfo[] getEvaluationInfo() {
        EvaluationInfo[] fv = new EvaluationInfo[genomes.length];
        for (int i = 0; i < genomes.length; i++) {
            fv[i] = genomes[i].getEvaluationInfo();

        }
        return fv;
    }
}