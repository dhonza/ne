package neat;

import common.RND;
import common.evolution.PopulationManager;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>Title: NeuroEvolution</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class represents the whole population. It contains the methods for
 * initialisation, evaluation, selection and recombination. It also contains the
 * single genome evaulation method. Explicit fitness sharing is implemented.
 */

public class FitnessSharingPopulation<P> extends Population<P> {
    /** The actual size of the population */
    // int actualSize;

    /**
     * The number of unassigned Genomes for reproduction
     */
    private int unassignedForReproduction = 0;

    public FitnessSharingPopulation(PopulationManager<Genome, P> populationManager) {
        super(populationManager);
    }

    public FitnessSharingPopulation(PopulationManager<Genome, P> populationManager, Genome oproto) {
        super(populationManager, oproto);
    }

    public FitnessSharingPopulation(PopulationManager<Genome, P> populationManager, String ofileName) {
        super(populationManager, ofileName);
    }

    /**
     * Reproduces the Population. New Genomes are recombined by using genetic
     * operators.
     */
    void reproduce() {
        // System.out.println( " Population.reproduce()" );
        Genome[] tpop = new Genome[NEAT.getConfig().populationSize]; // here we store a new
        // Population
        int tpopi = 0; // pointer to tpop
        Genome tg;
        Genome tgnew;
        Genome[] tgs;

        // clone or mutate the BSG for the unassigned place left in the Population
        for (int i = 0; i < unassignedForReproduction; i++) {
            if (RND.getBoolean()) {
                tpop[tpopi++] = bestOfGeneration.mutateWeights();
            } else {
                tpop[tpopi++] = (Genome) bestOfGeneration.clone();
            }
        }

        for (Species specie : species) { // for each Species...
            if (specie.genomes.size() < specie.elitistSize) {
                System.out.println("PRUSER!! opravit");
            }


            int j = 0;
            for (int i = 0; i < specie.elitistSize; i++) { // copy elite Genomes to the next generation

                if (j >= specie.genomes.size()) {
                    j = 0;
                }
                tpop[tpopi++] = specie.genomes.get(j++).eliteCopy();
//                System.out.println("S" + specie.getId() + " ELITE COPY: " + tpop[tpopi - 1]);
//                NEAT.getConfig().println("S" + specie.getId() + " ELITE COPY: " + tpop[tpopi - 1]);
            }
            for (int i = specie.elitistSize; i < specie.expectedOffspring; i++) { // create expectedOffspring
                if (RND.getDouble() < NEAT.getConfig().mutateOnlyProbability) {
                    // decide whether to mutate or mate
                    /** TODO make this better */
                    tg = specie.getRandomGenome();
                    double r = RND.getDouble();
                    if (r < NEAT.getConfig().mutateAddNeuron) {
                        tgnew = tg.mutateAddNeuron();
                        tpop[tpopi++] = tgnew;
                    } else if (r < NEAT.getConfig().mutateAddLink + NEAT.getConfig().mutateAddNeuron) {
                        tgnew = tg.mutateAddLink();
                        tpop[tpopi++] = tgnew;
                    } else { // non-structural mutations
                        tpop[tpopi] = tg.mutateWeights();
                        tpop[tpopi].mutateToggleEnabled();
                        tpop[tpopi++].mutateActivation();
                        /** TODO maybe call from mutateWeight (DelphiNEAT) */
                    }
                } else { // mating
                    tgs = specie.getTwoRandomGenomes();
                    tgnew = tgs[0].mateMultipoint(tgs[1]);
                    tpop[tpopi++] = tgnew;
                }
                if (!(tpop[tpopi - 1].check() && tpop[tpopi - 1].getNet().check())) {
                    System.out.println("  ERROR in reproduction");
                }
            }
        }
        genomes = tpop;// we don't need the previous generation no more...

        //adjusting
//        if (species.size() > 25)
//            NE.DISTANCE_DELTA *= 1.1;
//        else if (species.size() < 5)
//            NE.DISTANCE_DELTA /= 1.1;
    }

    /**
     * Selects the Genomes to survive.
     */
    void select() {
        // System.out.println( " Population.select()" );
        for (Species specie : species) { // for all Species...
            // Species were already sorted in speciat()
            specie.markForReproduction();
        }
    }

    /**
     * Specitates the whole population. The newly created Genomes fall into the
     * first appropriate Species (its distance from a random representative of
     * that Species is smaller than <i>NE.DISTANCE_DELTA </i>). If there is no
     * such Species a new one is created.
     */
    void speciate() {
        // System.out.println( " Population.speciate()" );
        // Species
        for (Species specie : species) {
            specie.setRepresentative();
            specie.genomes.clear(); // clear the species
            specie.maxFitness = 0;
            specie.avgSharedFitness = 0;
        }

        Genome tg, tg2;
        Species ts;
        for (int i = 0; i < NEAT.getConfig().populationSize; i++) {// place each Genome
            // into the proper
            // Species
            boolean found = false;
            tg = genomes[i];
            for (Species specie : species) {
                tg2 = specie.representative; // its representative
                // System.out.println( " dist:" +tg.distance( tg2 ) );
                if (tg.distance(tg2) < NEAT.getConfig().distanceDelta) { // it fits into this
                    // Species
                    specie.genomes.add(tg);
                    found = true;
                    break;
                }
            }
            if (!found) {
                ts = addSpecies(tg); // creating new Species
                ts.representative = tg; // it's the first and also the only
                // Genome of the new Species, so let it
                // be the Species representative...
            }
        }
        // sort Genomes in Species, used for elitism and marking for reproduction
        for (Species specie : species) {
            specie.sort(); // sort the Species' genomes from the best to the worst
        }

//        this.saveSpeciesHistory(); // save for statistics

        this.adjustFitness(); //compute sharedFitness of all Genomes, penalize unimproved Species etc...
        this.estimateOffspring(); //estimate the amount of offspring for all Species
    }

    /**
     * Sets the <b>Genome.sharedFitness </b> of all Genomes.
     */
    void adjustFitness() {
        // System.out.println( " Population.adjustFitness()" );
        for (Species specie : species) specie.adjustFitness();
    }

    void evaluate() {
        super.evaluate();
        this.incrementEvaluation(NEAT.getConfig().populationSize);
    }

    /**
     * DOPLNIT
     *
     * @return
     */
    private boolean deltaCoding() {
        //System.out.println(" PERFORNING DELTA CODING!");
        if (species.size() > 2) {
            int halfPop = NEAT.getConfig().populationSize / 2;
            LinkedList<Species> tspecies = new LinkedList<Species>();

            Iterator it = species.iterator();
            Species ts = (Species) it.next();
            //System.out.println(ts);
            ts.expectedOffspring = halfPop;
            tspecies.add(ts);

            ts = (Species) it.next();
            ts.expectedOffspring = NEAT.getConfig().populationSize - halfPop;
            //System.out.println(ts);
            tspecies.add(ts);

            species = tspecies;

            //System.out.println(species.size());
            return true;
        } else {
            //System.out.println("  only " + species.size() + " Species");
            return false;
        }
    }

    /**
     * Estimates the amount of offspring of each Species.
     */
    void estimateOffspring() {
        // System.out.println( " Population.estimateOffspring()" );
        Species ts;
        double total = 0.0;
        int assigned = 0;

        Iterator it = species.iterator();
        while (it.hasNext()) { // compute average shared fitness for each
            // Species + their sum
            ts = (Species) it.next();
            if (ts.genomes.size() > 0) {
                ts.computeAvgSharedFitness();
                total += ts.avgSharedFitness;
            } else {// the empty Species must be eliminated
                // System.out.println( " removing empty Species id:" + ts.id );
                it.remove();
            }
        }

        Collections.sort(species);

        int distribute = NEAT.getConfig().populationSize; // save few for champs

        boolean cont = true;

        if (lastInnovation > NEAT.getConfig().populationDeltaCodingAge) {
            // if( false ) {
            cont = !deltaCoding();
            if (!cont) {
                assigned = distribute;
            }
            lastInnovation = 0;
        }
        if (cont) {
            it = species.iterator();
            while (it.hasNext()) { // compute number of offspring (it's
                // proportional to average shared
                // fitness/sum of all average shared
                // fitnesses)
                ts = (Species) it.next();

                ts.expectedOffspring = (int) (distribute * (ts.avgSharedFitness / total));
                //calculate the number of elite Genomes (the maximum is the number currently available)
                ts.elitistSize = (int) Math.min(ts.genomes.size(), (ts.expectedOffspring * NEAT.getConfig().elitistProportionPerSpecies));
                if (ts.elitistSize == 0 && ts.expectedOffspring > 1) { // if we had expecteOffspring = 1, Species won't evolve no more
                    ts.elitistSize = 1;
                    System.out.println("NOTE elitistSize:" + ts.elitistSize + " expectedOffspring:" + ts.expectedOffspring);
                }
//                System.out.println("elitistSize:" + ts.elitistSize + " expectedOffspring:" + ts.expectedOffspring);

                assigned += ts.expectedOffspring;
            }
        }
        // count the number of unassigned due to roundoff errors
        unassignedForReproduction = NEAT.getConfig().populationSize - assigned;
        //System.out.println("unassignedForReproduction = " + unassignedForReproduction);
//        NEAT.getConfig().println("unassignedForReproduction = " + unassignedForReproduction);

    }
}