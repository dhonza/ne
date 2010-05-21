/*
 * Created on 23.5.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package neat;

import common.RND;

import java.util.Iterator;

/**
 * @author drchal
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class DeterministicCrowdingPopulation extends Population {

    private boolean eval = true;

    public DeterministicCrowdingPopulation(Evaluable oevaluator) {
        super(oevaluator);
    }

    /**
     * @param oproto
     */
    public DeterministicCrowdingPopulation(Evaluable oevaluator, Genome oproto) {
        super(oevaluator, oproto);
    }

    /**
     * @param oevaluateAll strategy of evaluation - all together (true) or separately (false).
     */
    void reproduce(boolean oevaluateAll) {
        int n = NEAT.getConfig().populationSize; // population size

        Genome[] tpop = new Genome[n]; // here we store a new
        // Population
        int tpopi = 0;
        int tpopj = 0;

        Genome p1, p2; // parents
        Genome c1, c2; // children

        int counter = 0;
        int p1idx, p2idx;


        Genome[] parents = new Genome[n];
        Genome[] children = new Genome[n];
        boolean[] structurals = new boolean[n];

        //flags
        boolean structural1 = false;
        boolean structural2 = false;

        for (int i = 0; i < n / 2; i++, counter += 2) {
            p1idx = RND.getInt(counter, NEAT.getConfig().populationSize - 1);
            p2idx = RND.getInt(counter + 1, NEAT.getConfig().populationSize - 1);
            p1 = genomes[p1idx];
            p2 = genomes[p2idx];
            genomes[p1idx] = genomes[counter];
            genomes[p2idx] = genomes[counter + 1];
            genomes[counter] = p1;
            genomes[counter + 1] = p2;

//            c1 = p1.mateMultipoint(p2);
//            c2 = (Genome) (c1.clone());

//            c1 = p1.mateMultipoint(p2);
//            c2 = p1.mateMultipoint(p2);

            c1 = p1.mateMultipoint(p2, true); // prefered topology from higher fitness
            c2 = p1.mateMultipoint(p2, false);// prefered topology from lower fitness

//            tg = p1.crossover(p2); // cross two children
//            c1 = tg[0];
//            c2 = tg[1];


//            if (logging != null) {
//                new GenomePrinter(new Genome[]{p1, p2, c1, c2}, logging);
//            }

            boolean addNeuron = false;
            boolean addLink = false;

            double r;


            double lastI = 0.0;
            double avoidStructuralProbability = 0.0;
            if (c1.getLastNIGeneration() != -1) {
                lastI = getGeneration() - c1.getLastNIGeneration();
                avoidStructuralProbability = 1.0 / Math.pow(lastI, NEAT.getConfig().structuralInnovationAvoidancePower);
            }

            avoidStructuralProbability = 0.0;
            if (avoidStructuralProbability < RND.getDouble()) {
                r = RND.getDouble();
                if (r < NEAT.getConfig().mutateAddNeuron) {
                    addNeuron = true;
                    structural1 = true;
                } else
                if (r < NEAT.getConfig().mutateAddNeuron + NEAT.getConfig().mutateAddLink) {
                    addLink = true;
                    structural1 = true;
                }
            }

            if (addNeuron) { // child #1
                c1 = c1.mutateAddNeuron();
            } else if (addLink) {
                c1 = c1.mutateAddLink();
            } else {
                c1 = c1.mutateWeights();
                //c1.mutateToggleEnabled();
            }


            lastI = 0.0;
            avoidStructuralProbability = 0.0;
            if (c2.getLastNIGeneration() != -1) {
                lastI = getGeneration() - c2.getLastNIGeneration();
                avoidStructuralProbability = 1.0 / Math.pow(lastI, NEAT.getConfig().structuralInnovationAvoidancePower);
            }

            avoidStructuralProbability = 0.0;
            if (avoidStructuralProbability < RND.getDouble()) {
                r = RND.getDouble();
                if (r < NEAT.getConfig().mutateAddNeuron) {
                    addNeuron = true;
                    structural2 = true;
                } else
                if (r < NEAT.getConfig().mutateAddNeuron + NEAT.getConfig().mutateAddLink) {
                    addLink = true;
                    structural2 = true;
                }
            }

            if (addNeuron) { // child #2c2 = c2.mutateWeights();
                c2 = c2.mutateAddNeuron();
            } else if (addLink) {
                c2 = c2.mutateAddLink();
            } else {

                //c2.mutateToggleEnabled();
            }

            if (!oevaluateAll) {
                c1.fitness = evaluator.evaluate(c1); // evaluate them
                c2.fitness = evaluator.evaluate(c2);
                this.incrementEvaluation(2);
            }
            // copy to arrays for final fight of parents vs. children
            parents[tpopi] = p1;
            children[tpopi] = c1;
            structurals[tpopi++] = structural1;
            parents[tpopi] = p2;
            children[tpopi] = c2;
            structurals[tpopi++] = structural2;

//            if(1.5 < RND.getDouble()) {
//                structural1 = false; structural2 = false;
//            }
        }

        if (oevaluateAll) {
            double[] fitnessValues = new double[n];
            evaluator.evaluateAll(children, fitnessValues);
            this.incrementEvaluation(n);
            for (int i = 0; i < n; i++) {
                children[i].fitness = fitnessValues[i];
            }
        }

        tpopi = 0;

        for (int i = 0; i < n / 2; i++) {
            //extract parrents and children
            p1 = parents[tpopi];
            c1 = children[tpopi];
            structural1 = structurals[tpopi++];
            p2 = parents[tpopi];
            c2 = children[tpopi];
            structural2 = structurals[tpopi++];

            // choose similar
            if ((p1.distance(c1) + p2.distance(c2)) <= (p1.distance(c2) + p2.distance(c1))) {
                if ((c1.fitness > p1.fitness) || structural1) { // choose better
                    tpop[tpopj++] = c1;
                } else {
                    tpop[tpopj++] = p1;
                }

                if ((c2.fitness > p2.fitness) || structural2) {
                    tpop[tpopj++] = c2;
                } else {
                    tpop[tpopj++] = p2;
                }
            } else {
                if ((c2.fitness > p1.fitness) || structural2) {
                    tpop[tpopj++] = c2;
                } else {
                    tpop[tpopj++] = p1;
                }

                if ((c1.fitness > p2.fitness) || structural1) {
                    tpop[tpopj++] = c1;
                } else {
                    tpop[tpopj++] = p2;
                }
            }
        }

        genomes = tpop;// we don't need the previous generation no more...
    }

    void evaluate(boolean oevaluateAll) {
        // System.out.println( " Population.evaluate()" );
        Genome tg;
        int n = NEAT.getConfig().populationSize;
        if (oevaluateAll) {

            if (eval) {
                double[] fitnessValues = new double[n];
                evaluator.evaluateAll(genomes, fitnessValues);
                this.incrementEvaluation(n);
                for (int i = 0; i < n; i++) {
                    genomes[i].fitness = fitnessValues[i];
                }
            }

            for (int i = 0; i < n; i++) {
                if (genomes[i].fitness > bestOfGeneration.fitness) {
                    bestOfGeneration = genomes[i];
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                tg = genomes[i];
                if (eval) {
                    tg.fitness = evaluator.evaluate(tg);
                    this.incrementEvaluation();
                }
                if (tg.fitness > bestOfGeneration.fitness) {
                    bestOfGeneration = tg;
                }
            }
        }

        eval = false;

        if (bestOfGeneration.fitness > bestSoFar.fitness) {
            bestSoFar = bestOfGeneration;
            lastInnovation = 0;
        } else
            lastInnovation++;
    }

    /*
    * (non-Javadoc)
    *
    * @see ne.Population#select()
    */
    void select() {

    }

    /*
    * (non-Javadoc)
    *
    * @see ne.Population#speciate()
    */
    void speciate() {
        Species ts;

        Iterator it = species.iterator();
        while (it.hasNext()) {
            ts = (Species) it.next();
            if (ts.getSize() == 0) {
                it.remove();
            }
        }

        it = species.iterator(); // choose representative for each
        // Species
        while (it.hasNext()) {
            ts = (Species) it.next();
            ts.setRepresentative();
            ts.genomes.clear(); // clear the species
            ts.maxFitness = 0;

        }

        Genome tg, tg2;
        for (int i = 0; i < NEAT.getConfig().populationSize; i++) {// place each Genome
            // into the proper
            // Species
            boolean found = false;
            tg = genomes[i];
            it = species.iterator();
            while (it.hasNext()) {
                ts = (Species) it.next(); // get species,
                tg2 = ts.representative; // its representative
                // System.out.println( " dist:" +tg.distance( tg2 ) );
                if (tg.distance(tg2) < NEAT.getConfig().distanceDelta) { // it fits into this
                    // Species
                    ts.genomes.add(tg);
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

        it = species.iterator();
        while (it.hasNext()) {
            ts = (Species) it.next();
            ts.computeMaxFitness();
        }

//        this.saveSpeciesHistory(); // save for statistics
    }

    /**
     * Prints info. This method is always called when a new generation is
     * created. It can be overwritten to perform drawing, writing to files etc..
     */
    public void printNews() {
        System.out.print("G:" + getGeneration() + " EVA:" + getEvaluation() + " SPE:" + getSpecies().size() + " BSF:" + getBestSoFar().getFitness() + " BOG:"
                + getBestOfGeneration().getFitness() + " LASTIN:" + getLastInnovation() + " DELTA:" + NEAT.getConfig().distanceDelta + " LIN:"
                + getGlobalInnovation().getLinkInnovation() + " NIN:" + getGlobalInnovation().getNeuronInnovation() + " BSFL:" + getBestSoFar().getNet().getNumLinks()
                + " BSFHN:" + getBestSoFar().getNet().getNumHidden());
    }

    /**
     * Prints info only when population improves. This method is always called
     * when a new generation is created. It can be overwritten to perform
     * drawing, writing to files etc..
     */
    public void printProgress() {
//        System.out.println(" NEW CHAMP:" + getBestSoFar().getFitness() + " BSFL:" + getBestSoFar().getNet().getNumLinks() + " BSFHN:"
//                + getBestSoFar().getNet().getNumHidden());
    }
}