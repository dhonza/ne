package neat;

import common.RND;
import common.evolution.EvaluationInfo;
import common.net.linked.Link;
import common.net.linked.Net;
import common.net.linked.Neuron;

/**
 * <p>Title: NeuroEvolution</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class represents the genome, in fact it includes both genotype {list of
 * genes) and phenotype (neural network). It tracks the statistics, too.
 */
@SuppressWarnings({"JavadocReference"})
public class Genome implements Comparable {

    private static final int FIRST_OF_FUNCTION_BLOCK = 0;

    private LocalInnovation localInnovation;

    /**
     * Encapsulates the Link and its position in <i>linkMatrix </i>
     */
    public class LinkGene {
        /**
         * The reference to Link. <i>Link.id </i> is the Link's innovation
         * number.
         */
        Link link;

        int functionBlockPosition = 0;

        /**
         * The row/column position of the Link in linkMatrix.
         */
        int row, col;

        /**
         * Constructs LinkGene.
         *
         * @param olink                  the Link
         * @param ofunctionBlockPosition
         */
        LinkGene(Link olink, int ofunctionBlockPosition) {
            link = olink;
            functionBlockPosition = ofunctionBlockPosition;
        }

        public void setFunctionBlockPosition(int ofunctionBlockPosition) {
            this.functionBlockPosition = ofunctionBlockPosition;
        }

        public int getFunctionBlockPosition() {
            return functionBlockPosition;
        }

        protected Object clone() {
            return new LinkGene(link, functionBlockPosition);
        }
    }

    public enum GenomeOrigin {
        NEW,
        CLONE,
        ELITE_COPY,
        ADD_LINK,
        ADD_NEURON,
        MUTATE_WEIGHTS,
        MUTATE_WEIGHTS2,
        MATE
    }


    /**
     * The array of all Links. Links are ordered by their innovation number (
     * <i>Link.id </i>).
     */
    protected LinkGene[] linkGenes;

    /**
     * The network represented by this genome.
     */
    protected Net net;

    /**
     * The fitness of this Genome - higher number means higher fitness kde se
     * pocita !!!!!!!!!!!!!!!!!!!!!!!!??????????????????
     */
    protected double fitness;

    protected EvaluationInfo evaluationInfo;

    /**
     * The shared fitness = fitness / this species size kde se pocita
     * !!!!!!!!!!!!!!!!!!!!!!!!??????????????????
     */
    protected double sharedFitness;

    /**
     * The output error of the network, this can be used in supervised learning
     * (when there are given matching input/output pairs).
     */
    protected double error = Double.NaN; //TODO dat pryc - resit stopping criteria

    /**
     * Flag which is true when the Genome fitness is evaluated.
     */
    protected boolean evaluated;

    /**
     * The population to which this genome belongs.
     */
    Population pop;

    private int lastNIGeneration = -1;

    private int lastLIGeneration = -1;

    protected GenomeOrigin genomeOrigin;

    /**
     * Construct new Genome directly from given Net. This constructor creates
     * and fills structures like linkMatrix or linkGenes. Note Neurons and Links
     * of the network MUST be ordered by their ids, otherwise the Genome's
     * behavior is undefined. This method is typically called from
     * <i>Population.spawn() </i> to construct the initial population.
     *
     * @param onet the network from which is this Genome derived
     * @see ne.FitnessSharingPopulation#spawn
     */
    public Genome(Net onet) {
        net = onet;
        int i, j;
        Neuron tn;
        Link tl;
        LinkGene tlg;

        localInnovation = new LocalInnovation(net);

        linkGenes = new LinkGene[net.getNumLinks() + 3]; // +3 for mutateAddNeuron()

        int[] addressHelper = new int[net.getMaxNeuronId() + 1]; // to simply address
        // rows and columns
        // by id

        // fill refInHidOut[] and refHidOut[] with Neurons, prepare
        // addressHelper
        for (i = 0; i < net.getNumInputs(); i++) {
            tn = net.getInputs().get(i);
            addressHelper[tn.getId()] = i;
        }
        for (i = 0, j = net.getNumInputs(); i < net.getNumHidOut(); i++, j++) {
            tn = net.getHidout().get(i);
            addressHelper[tn.getId()] = j;
        }

        // now Links
        int tr, tc;
        for (i = 0; i < net.getNumLinks(); i++) {
            tl = net.getLinks().get(i);
            tr = addressHelper[tl.getIn().getId()];
            tc = addressHelper[tl.getOut().getId()] - net.getNumInputs();
            tlg = new LinkGene(tl, FIRST_OF_FUNCTION_BLOCK);
            linkGenes[i] = tlg;
            localInnovation.addLink(tl);
        }

        evaluated = false;
        genomeOrigin = GenomeOrigin.NEW;
    }

    /**
     * @return Returns the fitness.
     */
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

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    /**
     * @param opos
     * @return Returns the Link of Link Gene on given position.
     */
    public Link getLink(int opos) {
        return linkGenes[opos].link;
    }

    public Net getNet() {
        return net;
    }

    /**
     * @return Returns the number of linkGenes.
     */
    public int getNumOfLinkGenes() {
        return linkGenes.length;
    }

    public int getLastNIGeneration() {
        return lastNIGeneration;
    }

    public void setLastLIGeneration(int lastLIGeneration) {
        this.lastLIGeneration = lastLIGeneration;
    }

    public Population getPop() {
        return pop;
    }

    public void setPop(Population pop) {
        this.pop = pop;
    }

    public LinkGene[] getLinkGenes() {
        return linkGenes;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    /**
     * Computes the distance of two Genomes. It uses the equation: <br>
     * <img src="../pic/eq_dist.gif">
     * <p/>
     * Where <i>E </i> is the number of excess genes, <i>D </i> the number of
     * disjoint genes and <i>W </i> is the average weight difference of matching
     * genes (including disabled). <i>c1 </i>- <i>3 </i> are the constants (
     * <b>NE.DISTANCE_C1 </b>- <b>3 </b>).
     *
     * @param og the other genome
     * @return the distance between this and <i>oother </i> Genome
     * @see ne.NE#DISTANCE_C1
     * @see ne.NE#DISTANCE_C2
     * @see ne.NE#DISTANCE_C3
     */
    public double distance(Genome og) {
        int disjoint = 0, excess;
        int common = 0; // how many genes have the same innovation numbers
        double wDif = 0.0; // weight difference
        double actDif = 0.0; // activation function difference

        int i = 0, j = 0;
        Link il, jl;

        int iLen = net.getNumLinks(), jLen = og.net.getNumLinks();
        int larger = iLen; // number of genes of the larger Genome

        while ((i < iLen) && (j < jLen)) {
            il = linkGenes[i].link;
            jl = og.linkGenes[j].link;
            if (il.getId() == jl.getId()) {
                i++;
                j++;
                common++;
                wDif += Math.abs(il.getWeight() - jl.getWeight());
                if (il.getOut().getActivation() != jl.getOut().getActivation()) {
                    actDif += 1.0;
                }
            } else if (il.getId() > jl.getId()) {
                disjoint++;
                j++;
            } else {
                disjoint++;
                i++;
            }
        }
        if (i < iLen)
            excess = iLen - i;
        else {
            excess = jLen - j;
            larger = jLen;
        }

        // System.out.println( "C:" + common +" D:" + disjoint + " E:" + excess
        // + " W" + wDif/common);
        /** TODO set N = 1 for short genomes */
        // if( larger < 20 ) larger = 1;
        if (larger < 50)
            larger = 1;
        return ((NEAT.getConfig().distanceC1 * excess + NEAT.getConfig().distanceC2 * disjoint) / larger + (NEAT.getConfig().distanceC3 * wDif) / common + NEAT.getConfig().distanceCActivation * actDif);
    }

    Genome eliteCopy() {
        Genome tg = (Genome) this.clone();
        tg.genomeOrigin = GenomeOrigin.ELITE_COPY;
        return tg;
    }

    /**
     * Mates this Genome with the given one and returns their baby.
     *
     * @param og Genome to mate
     * @return the resultive Genome of th mating
     */
    Genome mateMultipoint(Genome og) {
        return mateMultipoint(og, true);
    }

    /**
     * Mates this Genome with the given one and returns their baby.
     *
     * @param og            Genome to mate
     * @param ochooseBetter
     * @return the resultive Genome of th mating
     */
    Genome mateMultipoint(Genome og, boolean ochooseBetter) {
        Genome g1, g2;
        Net tn;

        // put the better Genome to g1 and the worse to g2
        if (fitness < og.fitness) {
            if (ochooseBetter) {
                g1 = og;
                g2 = this;
            } else {
                g1 = this;
                g2 = og;
            }
        } else if (fitness > og.fitness) {
            if (ochooseBetter) {
                g1 = this;
                g2 = og;
            } else {
                g1 = og;
                g2 = this;
            }
        } else {
            if (RND.getBoolean()) {
                g1 = og;
                g2 = this;
            } else {
                g1 = this;
                g2 = og;
            }
        }

        int maxNeuronId;
        if (g1.net.getMaxNeuronId() > g2.net.getMaxNeuronId())
            maxNeuronId = g1.net.getMaxNeuronId();
        else
            maxNeuronId = g2.net.getMaxNeuronId();
        Neuron[] idTable = new Neuron[maxNeuronId + 1]; // table of Neurons
        // indexed by their id's

        /** TODO better estimation of number of hidden Neurons */
        tn = new Net(g1.net.getId(), g1.net.getNumInputs(), g1.net.getNumHidden() + g2.net.getNumHidden(), g1.net.getNumOutputs());
        /** TODO do this randomly if alpha is not constant for all Neurons */

        int i = 0, j = 0;
        int g1Len = g1.net.getNumLinks(), g2Len = g2.net.getNumLinks();
        Link l, l1, l2;
        LinkGene chosen;
        Neuron n;

        /** TODO skip the innovative structures of the worse Genome */
        while (!((i == g1Len) && (j == g2Len))) {
            if (i == g1Len) {
                chosen = null;
                // chosen = g2.linkGenes[j];
                j++;
            } else if (j == g2Len) {
                chosen = g1.linkGenes[i];
                i++;
            } else {
                l1 = g1.linkGenes[i].link;
                l2 = g2.linkGenes[j].link;
                if (l1.getId() == l2.getId()) {
                    if (RND.getBoolean())
                        chosen = g1.linkGenes[i];
                    else
                        chosen = g2.linkGenes[j];
                    i++;
                    j++;
                } else if (l1.getId() > l2.getId()) {
                    chosen = null;
                    // chosen = g2.linkGenes[j];
                    j++;
                } else {
                    chosen = g1.linkGenes[i];
                    i++;
                }
            }

            if (chosen == null)
                continue;

            l = (Link) chosen.link.clone(); // clone the Link

            n = chosen.link.getIn(); // its input Neuron
            if (idTable[n.getId()] == null) { // it is not in the table
                idTable[n.getId()] = (Neuron) n.clone(); // put it there
            }
            idTable[n.getId()].getOutgoing().add(l); // connect the Neuron to the Link
            l.setIn(idTable[n.getId()]);

            n = chosen.link.getOut(); // its output Neuron
            if (idTable[n.getId()] == null) { // it is not in the table
                idTable[n.getId()] = (Neuron) n.clone();
            }
            idTable[n.getId()].getIncoming().add(l); // connect the Neuron to the Link
            l.setOut(idTable[n.getId()]);
            tn.addLink(l); // add the Link to the Net
        }

        for (i = 0; i < idTable.length; i++) { // add Neurons to the Net
            if ((n = idTable[i]) != null) {
                switch (n.getType()) {
                    case INPUT:
                        tn.addInput(n);
                        break;
                    case HIDDEN:
                        tn.addHidden(n);
                        break;
                    case OUTPUT:
                        tn.addOutput(n);
                        break;
                }
            }
        }
        /*
        * if( !tn.check() ) { System.out.println( "MATE ERROR" ); net.sort();
        * og.net.sort(); tn.sort(); System.out.println( net );
        * System.out.println( og.net ); System.out.println( tn );
        * //mateMultipoint( og ); System.exit(1); } else {
        * //System.out.println( "MATE OK" ); }
        */
        Genome g = new Genome(tn);
        g.genomeOrigin = GenomeOrigin.MATE;
        g.lastNIGeneration = g1.lastNIGeneration; //new structures only from better
        g.lastLIGeneration = g1.lastLIGeneration;
        g.setPop(getPop());
        return g; // now create a Genome out of the Net
    }


    /**
     * Clones this Genome and mutates the weights of this copy DOPLNIT
     * !!!!!!!!!!!!!! THE ORIGINAL NEAT IMPLEMENTATION METHOD
     *
     * @return mutated Genome
     */
    Genome mutateWeights() {

        Genome tg = (Genome) clone();
        tg.genomeOrigin = GenomeOrigin.MUTATE_WEIGHTS;
        tg.evaluated = false;
        Link tl;

        double genes = (double) net.getNumLinks();
        double endPart = genes * 0.8;
        double num = 0.0;
        double powerMod = 1.0;
        double randNum, randChoice;
        boolean severe;
        double gaussPoint, coldGaussPoint;

        severe = RND.getBoolean();

        for (int i = 0; i < net.getNumLinks(); i++) {
            if (severe) {
                gaussPoint = 0.3;
                coldGaussPoint = 0.1;
            } else if ((genes >= 10.0) && (num > endPart)) {
                gaussPoint = 0.5; // Mutate by modification % of connections
                coldGaussPoint = 0.3; // Mutate the rest by replacement % of
                // the time
            } else {
                // Half the time don't do any cold mutations
                gaussPoint = 1.0 - NEAT.getConfig().mutationRate;
                if (RND.getBoolean()) {
                    coldGaussPoint = 1.0 - NEAT.getConfig().mutationRate - 0.1;
                } else {
                    coldGaussPoint = 1.0 - NEAT.getConfig().mutationRate;
                }
            }

            randNum = RND.getDouble(-1, 1) * powerMod * NEAT.getConfig().mutationPower;

            tl = tg.linkGenes[i].link;
            randChoice = RND.getDouble();
            if (gaussPoint < randChoice) {
                tl.setWeight(tl.getWeight() + randNum);
            } else if (coldGaussPoint < randChoice) {
                tl.setWeight(randNum);
            }
            num += 1.0;
        }

        return tg;
    }

    /**
     * Clones this Genome and mutates the weights of this copy DOPLNIT
     * !!!!!!!!!!!!!! WRIGHT'S METHOD
     *
     * @return mutated Genome
     */
    Genome mutateWeights2() {
        Genome tg = (Genome) clone();
        tg.genomeOrigin = GenomeOrigin.MUTATE_WEIGHTS2;
        tg.evaluated = false;
        Link tl;
        double genes = (double) net.getNumLinks();
        double endPart = genes * 0.8;
        double num = 0.0;
        double oldVal;
        double perturb;

        for (int i = 0; i < net.getNumLinks(); i++) {
            if ((RND.getDouble() < NEAT.getConfig().mutationRate) || ((genes >= 10.0) && (num > endPart))) {
                tl = tg.linkGenes[i].link;
                oldVal = tl.getWeight();
                perturb = RND.getDouble() * NEAT.getConfig().mutationPower;

                if (num > endPart)
                    if (RND.getDouble() < 0.2)
                        perturb = 0.0;

                if (RND.getBoolean()) { // positive case
                    if (oldVal + perturb > 100.0) {
                        perturb = (100.0 - oldVal) * RND.getDouble();
                    }
                    tl.setWeight(tl.getWeight() + perturb);
                } else { // negative case
                    if (oldVal - perturb < 100.0) {
                        perturb = (oldVal + 100.0) * RND.getDouble();
                    }
                    tl.setWeight(tl.getWeight() - perturb);
                }
            }
            num += 1.0;
        }

        return tg;
    }

    /**
     * Clones this Genome and tries to add new random Link. DOPLNIT
     * !!!!!!!!!!!!!!
     *
     * @return mutated Genome
     */
    Genome mutateAddLink() {
        // System.out.println( " Genome.mutateAddLink()" );
        Genome tg = (Genome) clone();
        tg.genomeOrigin = GenomeOrigin.ADD_LINK;
        tg.evaluated = false;

        Neuron tnin, tnout;
        Neuron[] tneurons = tg.localInnovation.proposeLinkInnovation();
        if (tneurons == null) {
            return mutateWeights();
        }
        // just rename variables...
        tnin = tneurons[0];
        tnout = tneurons[1];

        int innov;
        boolean global = false; // global innovation?
        /** TODO WARNING!!!!! */
        if ((innov = getPop().getGlobalInnovation().checkLink(tnin.getId(), tnout.getId())) == GlobalInnovation.FREE) { // is
//        if (true) { // is
            // it a global innovation?
            global = true;
            innov = getPop().getGlobalInnovation().addLink(tnin.getId(), tnout.getId()); // no, then assign it
            // a new innovation number
            // System.out.println( " GLOBAL" );
        }

        // create a new Link and connect to the chosen Neurons
        Link tl = new Link(innov, RND.getDouble(-NEAT.getConfig().netNewWeightsScatter, NEAT.getConfig().netNewWeightsScatter), tnin, tnout,
                localInnovation.isProposedRecurrent());

        // update linkGenes and linkMatrix
        LinkGene lg = new LinkGene(tl, FIRST_OF_FUNCTION_BLOCK);
        if (global)
            tg.linkGenes[net.getNumLinks()] = lg; // the Link is a global
            // innovation - put its gene at
            // the end
        else { // the Link is a local innovation, it must be inserted into
            // linkGenes (which is sorted by the Link.id)
            /** TODO make the search binary not linear like now */
            // System.out.println( "Genome.mutateAddLink() - CHECK!!!");
            int idx = 0;
            LinkGene tlg;
            while ((idx < net.getNumLinks()) && (tg.linkGenes[idx].link.getId() < innov)) idx++;
            tlg = tg.linkGenes[idx];
            tg.linkGenes[idx++] = lg;
            while (idx < net.getNumLinks() + 1) {
                // System.out.println( " a " + idx + " " + net.getNumLinks() );
                lg = tg.linkGenes[idx];
                tg.linkGenes[idx++] = tlg;
                tlg = lg;
            }
        }

        // finally add the Link to the Network
        tg.net.addLink(tl);

        tg.localInnovation.addLink(tl);

        /*
        * if( !tg.net.check() ) { System.out.println( "addLINK ERROR" );
        * System.out.println( net ); System.out.println( tg.net ); tg =
        * mutateAddLink( orecurr ); //System.exit(1); }
        */
        tg.lastLIGeneration = getPop().getGeneration();

        return tg;
    }

    /**
     * Clones this Genome and tries to add new random Neuron. Which means:
     * disable a random Link, put a new Neuron and two Links on its place.
     * DOPLNIT !!!!!!!!!!!!!!
     *
     * @return mutated Genome
     */
    Genome mutateAddNeuron() {
        Genome tg = (Genome) clone();
        tg.genomeOrigin = GenomeOrigin.ADD_NEURON;
        tg.evaluated = false;
//        this is faster but takes also biasing Links...
//        LinkGene tlg = null;

//        if (NE.RECURRENT) {
//            /** TODO watch for bias */
//            tlg = tg.linkGenes[RND.getInt(0, net.getNumLinks() - 1)]; // choose a
//            // random existing Link
//            recurrent = tlg.link.recurrent;
//        } else {
//            int tries = 0;
//            while (tries < NE.INNOVATION_MATRIX_TRIES) {
//                tlg = tg.linkGenes[RND.getInt(0, net.getNumLinks() - 1)];
//                recurrent = tlg.link.recurrent;
//                if (!recurrent)
//                    break;
//                tries++;
//            }
//            if (tries == NE.INNOVATION_MATRIX_TRIES) {
//                return mutateWeights();
//            }
//        }

        Link tl = tg.localInnovation.proposeNeuronInnovation(); // propose Link to be replaced by Neuron and Links
        if (tl == null) {//failed to find innovation
            return this.mutateWeights(); //TODO add to stats
        }

        tl.setEnabled(false); // disable it

        Neuron tn1 = tl.getIn(); // get incoming Neuron
        Neuron tn2 = tl.getOut(); // get outgoing Neuron

        // create a new Neuron, grow GlobalInnovation
        int ninnov, linnov1, linnov2, linnovb; // innovation numbers of a new Neuron (Neuron, two Links + bias Link)
        // and both new Links
        ninnov = getPop().getGlobalInnovation().addNeuron(tn1.getId(), tn2.getId(), getPop().getGeneration(), this.net);

        Neuron n = new Neuron(ninnov, Neuron.Type.HIDDEN, Neuron.Activation.getRandom());

        // create two links, interconect them with Neurons already present in
        // Net, fill innovation matrix

        linnov1 = getPop().getGlobalInnovation().addLink(tn1.getId(), ninnov);
        Link l1 = new Link(linnov1, 1.0, tn1, n, tg.localInnovation.isProposedRecurrent());

        linnov2 = getPop().getGlobalInnovation().addLink(ninnov, tn2.getId());
        Link l2 = new Link(linnov2, tl.getWeight(), n, tn2, tg.localInnovation.isProposedRecurrent());

        //bias
        linnovb = getPop().getGlobalInnovation().addLink(tg.net.getBiasNeuron().getId(), n.getId());
        Link lb = new Link(linnovb, RND.getDouble(-NEAT.getConfig().netNewWeightsScatter, NEAT.getConfig().netNewWeightsScatter), tg.net.getBiasNeuron(), n, false);

        // update linkGenes, localInnovation
        LinkGene lg1 = new LinkGene(l1, FIRST_OF_FUNCTION_BLOCK);
        LinkGene lg2 = new LinkGene(l2, FIRST_OF_FUNCTION_BLOCK + 1);
        LinkGene lg3 = new LinkGene(lb, FIRST_OF_FUNCTION_BLOCK + 2); //TODO check this

        tg.linkGenes[net.getNumLinks()] = lg1;
        tg.linkGenes[net.getNumLinks() + 1] = lg2;
        tg.linkGenes[net.getNumLinks() + 2] = lg3;

        tg.localInnovation.addNeuron(n, l1, l2, lb);

        tg.net.addNeuron(n, l1, l2); // add it to Net structures

        tg.net.addLink(lb);

        tg.lastNIGeneration = getPop().getGeneration();

        return tg;
    }

    /**
     * DOPSAT!!!!!!!!!!!!!!!!!!!!!!
     */
    void mutateToggleEnabled() {
        if (RND.getDouble() < NEAT.getConfig().mutateToggleEnabled) {
            getRandomLink().toggleEnabled();
        }
    }

    void mutateActivation() {
        if (RND.getDouble() < NEAT.getConfig().mutateActivation) {
            Neuron n = getRandomLink().getOut();
            if (n.getType() == Neuron.Type.OUTPUT) {
                return;
            }
            Neuron.Activation a = n.getActivation();
            Neuron.Activation na;
            int i = 0;
            while (i < 5) {
                na = Neuron.Activation.getRandom();
                if (na != a) {
                    n.setActivation(na);
                    break;
                }
                i++;
            }
        }
    }

    /**
     * Checks the Genome.
     *
     * @return <b>true </b> if the Net is OK, else <b>false </b>
     */
    boolean check() {
        /** TODO finish */
        net.sort();
        for (int i = 0; i < net.getNumLinks(); i++) {
            if (net.getLinks().get(i).getId() != linkGenes[i].link.getId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a random Link.
     *
     * @return random Link
     */
    Link getRandomLink() {
        return linkGenes[RND.getInt(0, net.getNumLinks() - 1)].link;
    }

    /**
     * Clones this Genome.
     *
     * @return copy of this Genome.
     */
    protected Object clone() {
        net.check();
        Net tn = (Net) net.clone();
        if (!tn.check())
            System.out.println("ERROR: bad net");
        Genome tg;
        tg = new Genome(tn);
        if (tg.genomeOrigin != GenomeOrigin.NEW) {
            tg.genomeOrigin = GenomeOrigin.CLONE;
        }

        tg.setPop(pop);
        tg.fitness = fitness;
        tg.evaluationInfo = evaluationInfo;
        tg.evaluated = evaluated;
        tg.sharedFitness = sharedFitness;
        tg.lastNIGeneration = lastNIGeneration;
        tg.lastLIGeneration = lastLIGeneration;

        // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        for (int i = 0; i < net.getNumLinks(); i++) {
            tg.linkGenes[i].setFunctionBlockPosition(linkGenes[i].getFunctionBlockPosition());
        }

        /*
        * if( (pop != null) && (pop.evaluate( this ) != pop.evaluate( tg )) ) {
        * System.out.println( "ERROR in Genome.clone!!!" ); tn =
        * (Net)net.clone(); }
        */
        return tg;
    }

    /**
     * Implements the <code>Comparable</code> interface. Used for sorting.
     * Note, the sorting is done in descending order.
     *
     * @param oo the Object to be compared
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object
     */
    public int compareTo(Object oo) {
        if (fitness > ((Genome) oo).fitness)
            return -1;
        else if (fitness < ((Genome) oo).fitness)
            return 1;
        else
            return 0;
    }

    public String toString() {
        if (evaluated) {
            return "Genome links:" + this.linkGenes.length + " F:" + this.fitness + " O:" + this.genomeOrigin;
        } else {
            return "Genome links:" + this.linkGenes.length + " O:" + this.genomeOrigin;
        }
    }
}