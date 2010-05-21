package neat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 10:51:32 PM
 */
public class NEATConfig {
    //TODO Gettery a settery - vyhazovat vyjimky pri spatnem pouziti (enable flag false)

    /**
     * The constant needed to determine the Genome distance.
     *
     * @see ne.Genome#distance
     */
    public double distanceC1 = 1.0;
    /**
     * The constant needed to determine the Genome distance.
     *
     * @see ne.Genome#distance
     */
    public double distanceC2 = 1.0;
    /**
     * The constant needed to determine the Genome distance.
     *
     * @see ne.Genome#distance
     */
    public double distanceC3 = 0.4;

    public double distanceCActivation = 2.0;

    /**
     * This constant determines the threshold while searching the appropriate
     * Species to put a Genome into.
     *
     * @see ne.FitnessSharingPopulation#speciate
     */
    public double distanceDelta = 3.0;

    /**
     * The number of tries of <b>InnovationMatrix.randomLink()</b> method.
     *
     * @see ne
     */
    public int innovationMatrixTries = 30;

    /**
     * The number of generations to compute.
     *
     * @see
     */
    public int lastGeneration = 100;

    public double mutateAddLink = 0.3;

    public double mutateAddNeuron = 0.01;

    public double mutateToggleEnabled = 0.025;

    public double mutateActivation = 0.01;

    /**
     * The amount of mutation.
     *
     * @see ne.Genome#mutateWeights
     */
    public double mutationPower = 1.0;

    /**
     * The probability of a gene mutation.
     *
     * @see ne.Genome#mutateWeights
     */
    public double mutationRate = 0.2;

    /**
     * Specifies the interval <i>&lt;-NET_NEW_WEIGHTS_SCATTER; NET_NEW_WEIGHTS_SCATTER&gt;</i> into which
     * the randomized weights fall. It is used when new <i>Link</i> is created due to a structural mutation.
     *
     * @see ne.Net#randomizeWeights
     * @see ne.Genome#mutateAddLink
     * @see ne.Genome#mutateAddNeuron
     */
    public double netNewWeightsScatter = 0.3;

    /**
     * Specifies the interval <i>&lt;-NET_WEIGHTS_SCATTER; NET_WEIGHTS_SCATTER&gt;</i> into which
     * the randomized weights fall. It is used while spawning the population.
     *
     * @see ne.Net#randomizeWeights
     * @see ne.FitnessSharingPopulation#spawn
     */
    public double netWeightsScatter = 0.3;

    public double netWeightsAmplitude = Double.MAX_VALUE;

    /**
     * The size of the population. The size of the population is constant for the
     * whole evolution.
     */
    public int populationSize = 100;

    public boolean populationInitiallyRandomizeWeights = true;

    /**
     * the maximum number of species
     */
//    protected  IntParameter speciesMax = new IntParameter(20, "innovation.speciesMax");

    /**
     * Determines whether the species is still innovative
     * (it's the threshold for <code>ageOfLastInnovation</code>.
     *
     * @see ne.Species#adjustFitness
     * @see ne.Species#ageOfLastInnovation
     */
    public int speciesNotInnovative = 15;
    /**
     * The penalty for non innovative Species.
     *
     * @see ne.Species#adjustFitness
     */
    public double speciesNotInnovativePenalty = 0.01;

    /**
     * Determines the amount of Species' Genomes which will take part in reproduction.
     *
     * @see ne.Species#markForReproduction
     */
    public double speciesReproductionRatio = 0.4;

    /**
     * This threshold determines whether the species is young and therefore should be protected
     *
     * @see ne.Species#adjustFitness
     */
    public int speciesYoung = 10;
    /**
     * The bonus for young Species.
     *
     * @see ne.Species#adjustFitness
     */
    public double speciesYoungBonus = 1.0;

    /**
     * Target fitness. When the best ever Genome's fitness is euqual or higher than this value,
     * the evolution is stopped.
     */
    public double targetFitness = Double.POSITIVE_INFINITY;

    /**
     * If true recurrent networks are produced by structural mutations otherwise only feedforward.
     */
    public boolean recurrent = true;

    /**
     * When a new Neuron is added, this value stands for a  probability of assigning a new Neuron GlobalInnovation number.
     * If new Neuron GlobalInnovation number is not assigned, then some older version of the same GlobalInnovation is used.
     *
     * @see ne.GlobalInnovationList
     */
    public double globalNeuronInnovationAcceptNewRatio = 0.6;

    /**
     * This is used to choose a previous Neuron innovation version. Newer version are more likely to be chosen.
     *
     * @see ne.GlobalInnovationList
     */
    public double globalNeuronInnovationAttentuationRatio = 0.5;

    //TODO comment
    public double structuralInnovationAvoidancePower = 0.3;

    public int clearHistoryGenerations = 10;

    //-----------EFS-------------
    /**
     * The probabilty of breeding new Genome by mutation rather then by mating
     */
    public double mutateOnlyProbability = 0.25;

    /**
     * Specifies the age of Population, when Delta Coding is performed
     */
    public int populationDeltaCodingAge = 20;

    /**
     * The ratio of Species' best Genomes to be saved.
     */
    public double elitistProportionPerSpecies = 0.2;

    //------------activation functions--------
    public double activationSigmoidProbability = 0.1;
    public double activationBipolarSigmoidProbability = 0.2;
    public double activationLinearProbability = 0.1;
    public double activationGaussProbability = 0.1;
    public double activationAbsProbability = 0.1;
    public double activationSinProbability = 0.1;
    public double activationCosProbability = 0.1;
    public double activationSqrProbability = 0.1;
    public double activationSqrtProbability = 0.1;

//    public double activationSigmoidProbability = 0.0;
    //    public double activationLinearProbability = 0.0;
    //    public double activationGaussProbability = 1.0;
    //    public double activationAbsProbability = 0.0;
    //    public double activationSinProbability = 0.0;
    public Object logger;

    public String toString() {
        String result = "";
        Field[] field = getClass().getDeclaredFields();
        for (Field f : field) {
            String fname = f.getName();
            String fTypeName = f.getType().toString();
            try {
                result += fTypeName + " " + fname + "=" + f.get(this).toString() + "\n";
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
            //System.out.println(fname+" "+type);
        }
        return result;
    }

    public void registerLogger(Object logger) {
        this.logger = logger;
    }

    /**
     * The println method uses reflections to avoid dependencies on the logger class
     * and implementation.
     *
     * @param s
     */

    public void println(String s) {
        if (logger == null) {
            System.out.println(s);
        } else {
            Class lc = logger.getClass();
            try {
                Class[] par = new Class[1];
                par[0] = String.class;
                //String[] ars = new String[1];
                String[] ars = {s};
                Method m = lc.getDeclaredMethod("outLn", par);
                m.invoke(logger, ars);
            } catch (NoSuchMethodException nsme) {
                System.out.println("Invalid logger class");
                nsme.printStackTrace();
            } catch (IllegalAccessException iae) {
                System.out.println("Illegal access in logger class");
                iae.printStackTrace();
            } catch (java.lang.reflect.InvocationTargetException ite) {
                System.out.println("Invocation exception in logger class");
                ite.printStackTrace();
            }
        }
    }
}