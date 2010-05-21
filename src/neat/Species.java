package neat;

import common.RND;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <p/>
 * Title: NeuroEvolution
 * </p>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company:
 * </p>
 *
 * @author Jan Drchal
 * @version 0001
 */

public class Species implements Comparable {

    /**
     * Species id number.
     */
    int id;

    /**
     * The Population to which this Species belongs.
     */
    Population population;

    /**
     * All Genomes of this Species.
     */
    ArrayList<Genome> genomes;

    /**
     * The randomly chosen Genome: representative of this Species.
     */
    Genome representative;

    /**
     * The best Genome of this Species.
     */
    Genome bestOfGeneration;

    /**
     * The Species' age.
     */
    private int age = 0;

    /**
     * The age of the last innovation (based on fitness).
     */
    double ageOfLastInnovation = 0;

    /**
     * The Species' average shared fitness.
     *
     * @see #computeAvgSharedFitness
     */
    double avgSharedFitness;

    /**
     * The fitness of the best Genome of this Species.
     *
     * @see #computeMaxFitness
     */
    double maxFitness;

    /**
     * The fitness of the best Genome of this Species since the creation of the
     * Species. Used to track wheter the Species is still innovative.
     *
     * @see #maxFitnessEver
     */
    double maxFitnessEver;

    /**
     * Determines the amount of offspring.
     */
    int expectedOffspring;

    /**
     * The number of elite Genomes to survive without changes.
     */
    int elitistSize;

    /**
     * The Genomes above this index are selected for reproduction. Note,
     * <code>genomes</code> must be sorted.
     *
     * @see #markForReproduction
     */
    int reproductionThreshold;

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @return size of the Species (number of Genomes)
     */
    public int getSize() {
        return genomes.size();
    }

    /**
     * Constructs a new Species.
     *
     * @param oid   the Species id
     * @param osize the expected size of the Species (could affect the speed)
     * @param opop  the Population to which this Species belongs
     */
    Species(int oid, int osize, Population opop) {
        id = oid;
        genomes = new ArrayList<Genome>(osize);
        population = opop;
    }

    /**
     * Sets the <b>Genome.sharedFitness </b> of Species' each Genome according
     * to the Species' size and evolution success.
     */
    void adjustFitness() {
        double rate = 1.0; // the modification rate
        computeMaxFitness();

        if (age < NEAT.getConfig().speciesYoung) { // protect new Species
            rate = NEAT.getConfig().speciesYoungBonus;
            // System.out.println( " id:" + id + " young" );
        } else if (ageOfLastInnovation > NEAT.getConfig().speciesNotInnovative) {
            rate = NEAT.getConfig().speciesNotInnovativePenalty; // penalty for not
            // innovative ones
            // System.out.println( " id:" + id + " not innovative" );
        }

        Genome tg;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < genomes.size(); i++) {
            tg = genomes.get(i);
            if (tg.fitness > bestFitness) {
                bestFitness = tg.fitness;
                bestOfGeneration = tg;
            }
            tg.sharedFitness = rate * tg.fitness / genomes.size();
        }
        age++;
        ageOfLastInnovation++;
    }

    /**
     * Computes the Species' average of shared fitnesses.
     *
     * @see #avgSharedFitness
     */
    void computeAvgSharedFitness() {
        double total = 0.0;
        Genome tg;
        for (Genome genome : genomes) {
            tg = genome;
            //total += tg.sharedFitness;
            total += tg.fitness;
        }
        avgSharedFitness = total / genomes.size();
    }

    /**
     * Finds the fitness of the best Genome of this Species. Sets
     * <code>maxFitnessEver</code> and <code>ageOfLastInnovation</code>,
     * too.
     *
     * @see #maxFitness
     * @see #maxFitnessEver
     * @see #ageOfLastInnovation
     */
    void computeMaxFitness() {
        maxFitness = 0.0;
        /** TODO -inf? */
        Genome tg;
        for (Genome genome : genomes) {
            tg = genome;
            if (maxFitness < tg.fitness)
                maxFitness = tg.fitness;
        }
        if (maxFitnessEver < maxFitness) {
            // System.out.println( " id:" + id + " improved" );
            maxFitnessEver = maxFitness;
            ageOfLastInnovation = 0;
        }
    }

    /**
     * Gets a random Genome of those previously selected for reproduction.
     *
     * @return random Genome
     * @see ne.Population#reproduce
     */
    Genome getRandomGenome() {
        return genomes.get(RND.getInt(0, reproductionThreshold));
    }

    /**
     * Gets two different random Genomes of those previously selected for
     * reproduction. Typically they are used for mating.
     *
     * @return two random Genomes stored in array
     * @see ne.Population#reproduce
     */
    Genome[] getTwoRandomGenomes() {
        Genome[] tg = new Genome[2];
        tg[0] = genomes.get(RND.getInt(0, reproductionThreshold));
        if (reproductionThreshold > 0) {
            tg[1] = genomes.get(RND.getInt(1, reproductionThreshold));
            if (tg[0] == tg[1]) {
                tg[1] = genomes.get(0); // if they're the same
            }
        } else { // we have only one Genome for reproduction...
            tg[1] = tg[0].mutateWeights(); // ...so get the other parent by
            // mutating the first
        }
        return tg;
    }

    /**
     * Marks the Genomes which will take part in reproduction.
     *
     * @see #reproductionThreshold
     * @see neat.NEAT#SPECIES_REPRODUCTION_RATIO
     * @see neat.FitnessSharingPopulation#select
     */
    void markForReproduction() {
        /** TODO Maybe let reproduce at least one */
        reproductionThreshold = (int) (NEAT.getConfig().speciesReproductionRatio * genomes.size()) - 1;
        if (reproductionThreshold < 0)
            reproductionThreshold = 0;
    }

    /**
     * Chooses a random Genome of this Species and sets <b>representative </b>
     * to refer to it.
     *
     * @see #representative
     */
    void setRepresentative() {
        /** TODO Maybe use reproductionThreshold */
        //representative = (Genome) genomes.get(0);
        representative = genomes.get(RND.getInt(0, genomes.size() - 1));
    }

    /**
     * Sorts the Genomes from the best to the worst.
     */
    void sort() {
        Collections.sort(genomes);
        // System.out.println( this );
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
        if (maxFitness > ((Species) oo).maxFitness)
            return -1;
        else if (maxFitness < ((Species) oo).maxFitness)
            return 1;
            // if( avgSharedFitness > ((Species)oo).avgSharedFitness ) return -1;
            //    else if( avgSharedFitness < ((Species)oo).avgSharedFitness ) return 1;
        else
            return 0;
    }

    public String toString() {
        String ts;
        ts = " Species id:" + id + " size:" + genomes.size() + " age:" + age + " ageOfLastInnovation:"
                + ageOfLastInnovation + " maxFitness:" + maxFitness + " avgSharedFitness:" + avgSharedFitness
                + " RepL:" + representative.getNet().getNumLinks() + " RepN:" + representative.getNet().getNumNeurons();
        //Collections.sort( genomes );
        //for( int i = 0; i < genomes.size(); i++ ) ts += " " + ((Genome)(genomes.get(i))).fitness;
        return ts;
    }

}