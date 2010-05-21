package neat;

/**
 * <p>Title: ne</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class is the main neuro-evolution class.
 * It includes:</br>
 * - method for running the main evolution loop,</br>
 * - method for computation of the termination-condition,</br>
 * - all parameters affecting neuro-evolution.
 */
public class NEAT {
    protected static NEATConfig config = new NEATConfig();

    /**
     * The reference to Population
     */
    private Population population;

    private ProgressPrinter progressPrinter;

    /**
     * Constructs new NE object. It initializes what is needed and assigns
     * the population. Typically it would be !!!!!!!!!!!!!!!!!!!!!!!
     */
    public NEAT() {
    }

    public static NEATConfig getConfig() {
        return config;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public void setProgressPrinter(ProgressPrinter progressPrinter) {
        this.progressPrinter = progressPrinter;
    }

    /**
     * The main evolution loop.
     * The loop is stopped when the termination-condition is satisfied.
     *
     * @param oevaluateAll population can be evaluated in two possible ways: 1. (false) All Genomes separately, which is usual.
     *                     2. (true) The whole population of Genomes together, which is usefull for co-evolutionary tasks.
     * @see #toStop
     */
    public void run(boolean oevaluateAll) {
        boolean cont;

//        population.storeDistanceMatrix();
        do {
            population.incrementGeneration();
            population.evaluate(oevaluateAll); //obtain the fitness of all Genomes
            population.speciate(); //assign Genomes to Species, if needed create new Species
            //+ compute sharedFitness of all Genomes, penalize unimproved Species etc...
            // + estimate the amount of offspring for all Species
            progressPrinter.printGeneration();
            if (population.getLastInnovation() == 0) {
                progressPrinter.printProgress(); //prints only when there is a progress...
            }
            cont = !toStop(); //stopping criteria
            if (cont) {
                population.select(); //eliminate inferior genomes
                population.reproduce(oevaluateAll); //reproduce the Population - the current Population is completely replaced by its offspring

                if (population.getGeneration() % config.clearHistoryGenerations == 0) {
                    population.getGlobalInnovation().cleanHistory(); //? clear memory of innovations? after one generation ?
                }
            }
//            closeGenerationScopeStats();
        } while (cont);
        progressPrinter.printFinished();
//        population.storeDistanceMatrix();
    }

    /**
     * Checks whether the termination-condition is satisfied.
     * By default it stops when the number of generations reaches <b>LAST_GENERATION</b>
     *
     * @return <b>true</b> if the termination-condition is satisied otherwise <b>false</b>
     *         Typically this method would be overwritten with something more convenient.
     * @see #run
     */
    protected boolean toStop() {
        return !((population.getGeneration() < config.lastGeneration && (population.bestSoFar.fitness < config.targetFitness)));
    }

    /*
    private void closeGenerationScopeStats() {
        Stats stats = StatsSingleton.getInstance();
        stats.addSample("STAT_TIME_GEN", ((double) bench.stop()) / 1000);
    }
    */
}