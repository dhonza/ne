package neat;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: honza
 * Date: Apr 10, 2006
 * Time: 10:51:32 PM
 */
public class NEATConfig implements Serializable {
    //TODO Gettery a settery - vyhazovat vyjimky pri spatnem pouziti (enable flag false)

    /**
     * The constant needed to determine the Genome distance.
     *
     * @see neat.Genome#distance
     */
    public double distanceC1 = 1.0;
    /**
     * The constant needed to determine the Genome distance.
     *
     * @see neat.Genome#distance
     */
    public double distanceC2 = 1.0;
    /**
     * The constant needed to determine the Genome distance.
     *
     * @see neat.Genome#distance
     */
    public double distanceC3 = 0.4;

    public double distanceCActivation = 2.0;

    /**
     * This constant determines the threshold while searching the appropriate
     * Species to put a Genome into.
     *
     * @see neat.FitnessSharingPopulation#speciate
     */
    public double distanceDelta = 3.0;

    /**
     * The number of tries of <b>InnovationMatrix.randomLink()</b> method.
     *
     * @see neat
     */
    public int innovationMatrixTries = 30;

    /**
     * The number of generations to compute.
     *
     * @see
     */
    public int maxGenerations = 100;

    public int maxEvaluations = Integer.MAX_VALUE;

    public double mutateAddLink = 0.3;

    public double mutateAddNeuron = 0.01;

    public double mutateToggleEnabled = 0.025;

    public double mutateActivation = 0.01;

    /**
     * The amount of mutation.
     *
     * @see neat.Genome#mutateWeights
     */
    public double mutationPower = 1.0;

    /**
     * The probability of a gene mutation.
     *
     * @see neat.Genome#mutateWeights
     */
    public double mutationRate = 0.2;

    /**
     * Specifies the interval <i>&lt;-NET_NEW_WEIGHTS_SCATTER; NET_NEW_WEIGHTS_SCATTER&gt;</i> into which
     * the randomized weights fall. It is used when new <i>Link</i> is created due to a structural mutation.
     *
     * @see neat.Net#randomizeWeights
     * @see neat.Genome#mutateAddLink
     * @see neat.Genome#mutateAddNeuron
     */
    public double netNewWeightsScatter = 0.3;

    /**
     * Specifies the interval <i>&lt;-NET_WEIGHTS_SCATTER; NET_WEIGHTS_SCATTER&gt;</i> into which
     * the randomized weights fall. It is used while spawning the population.
     *
     * @see neat.Net#randomizeWeights
     * @see neat.FitnessSharingPopulation#spawn
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
     * @see neat.Species#adjustFitness
     * @see neat.Species#ageOfLastInnovation
     */
    public int speciesNotInnovative = 15;
    /**
     * The penalty for non innovative Species.
     *
     * @see neat.Species#adjustFitness
     */
    public double speciesNotInnovativePenalty = 0.01;

    /**
     * Determines the amount of Species' Genomes which will take part in reproduction.
     *
     * @see neat.Species#markForReproduction
     */
    public double speciesReproductionRatio = 0.4;

    /**
     * This threshold determines whether the species is young and therefore should be protected
     *
     * @see neat.Species#adjustFitness
     */
    public int speciesYoung = 10;
    /**
     * The bonus for young Species.
     *
     * @see neat.Species#adjustFitness
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
     * @see neat.GlobalInnovationList
     */
    public double globalNeuronInnovationAcceptNewRatio = 0.6;

    /**
     * This is used to choose a previous Neuron innovation version. Newer version are more likely to be chosen.
     *
     * @see neat.GlobalInnovationList
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

    public String toString() {
        String result = "";
        Field[] fieldArray = getClass().getDeclaredFields();
        List<Field> fields = Arrays.asList(fieldArray);
        Collections.sort(fields, new Comparator<Field>() {
            public int compare(Field f1, Field f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
        for (Field f : fields) {
            String fname = f.getName();
//            String fTypeName = f.getType().toString();
            try {
                result += fname + " = " + f.get(this).toString() + "\n";
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
            //System.out.println(fname+" "+type);
        }
        return result;
    }
}