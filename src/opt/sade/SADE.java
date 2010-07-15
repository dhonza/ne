package opt.sade;

import common.RND;
import common.evolution.Evaluable;
import common.evolution.EvaluationInfo;
import common.evolution.EvolutionaryAlgorithm;
import common.evolution.ParallelPopulationEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: SADE Library</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Anicka Kucerova, java version Jan Drchal
 * @version 1.0
 */

/**
 * The <b>SADE</b> class implements the SADE genetic algorithm. The SADE operates
 * on real domains (the chromosomes are represented by real number vectors).
 * The scheme of SADE:
 * <pre>
 * void SADE ( void )
 * {
 *   FIRST_GENERATION ();
 *    while ( to_be_continued )
 *    {
 *      MUTATE ();
 *      LOCAL_MUTATE ();
 *      CROSS ();
 *      EVALUATE_GENERATION ();
 *      SELECT ();
 *    }
 * }
 * </pre>
 */


public class SADE implements EvolutionaryAlgorithm {
    /**
     * Determines the size of the pool.
     */
    public int poolRate = 2;

    public boolean fixedPopulationSize = false;

    /**
     * Population size. In fact this number is only a half. It rather determines the number of objective function evaluations in
     * each generation.
     */
    public int populationSize = 100;

    /**
     * The probability of the MUTATION.
     */
    public double radioactivity = 0.1;
    /**
     * The probability of the LOCAL_MUTATION.
     */
    public double localRadioactivity = 0.1;
    /**
     * Determines the amount of MUTATION.
     */
    public double mutationRate = 0.5;
    /**
     * Determines the amount of LOCAL_MUTATION.
     */
    public double mutagenRate = 400;
    /**
     * The probability of the CROSS.
     */
    public double crossRate = 0.3;
    /**
     * Optimization process will stop when the number of fitness function calls exceeds this value.
     */
    public int fitnessCallsLimit = Integer.MAX_VALUE;
    public int maxGenerations = Integer.MAX_VALUE;
    public double targetFitness = Double.MAX_VALUE;

    public int dimensions;
    public double lowerBound = -10;
    public double upperBound = 10;
    public boolean returnToDomain = false;


    private int actualSize;
    private int fitnessCall;
    private int poolSize;
    private int selectedSize;

    private int generation;
    private int lastInnovation;

    private double[] Force, mutagen;
    private double[][] CH;

    private double[] bsf, btg;
    private double bsfValue, btgValue;

    final private Evaluable<SADEGenome>[] perThreadEvaluators;
    final private ParallelPopulationEvaluator<SADEGenome> populationEvaluator;


    private EvaluationInfo[] evaluationInfos;
    private int generalizationGeneration;
    private EvaluationInfo generalizationEvaluationInfo;

    /**
     * Constructor for <b>SADE</b> object. The parameter represents the optimized function.
     */
    public SADE(Evaluable<SADEGenome>[] perThreadEvaluators) {
        this.perThreadEvaluators = perThreadEvaluators;
        populationEvaluator = new ParallelPopulationEvaluator<SADEGenome>();
        bsfValue = Double.NEGATIVE_INFINITY;
        btgValue = Double.NEGATIVE_INFINITY;
    }

//---------- SADE Technology ---------------------------------------------------

    private double[] newPoint() // creates new random point
    {
        double[] x = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            x[i] = RND.getDouble(lowerBound, upperBound);
        }
        return x;
    }

    private void configuration() //
    {
        if (!fixedPopulationSize) {
            populationSize = poolRate * dimensions;
        }
        poolSize = 2 * populationSize;
        selectedSize = populationSize;
        mutagen = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            mutagen[i] = (upperBound - lowerBound) / mutagenRate;
            if (0 > mutagen[i]) {//TODO the "zero" was originally a variable for each dimension
                mutagen[i] = 0;
            }
        }
    }

    private void EVALUATE_GENERATION(int start) {
        List<SADEGenome> evalPopulation = new ArrayList<SADEGenome>();
        for (int i = start * selectedSize; i < actualSize; i++) {
            if (returnToDomain) {
                for (int k = 0; k < dimensions; k++) {
                    if (CH[i][k] < lowerBound) {
                        CH[i][k] = lowerBound;
                    }
                    if (CH[i][k] > upperBound) {
                        CH[i][k] = upperBound;
                    }
                }
            }
            evalPopulation.add(new SADEGenome(CH[i]));
        }

        evaluationInfos = populationEvaluator.evaluate(perThreadEvaluators, evalPopulation);

        for (int i = start * selectedSize; i < actualSize; i++) {
            Force[i] = evaluationInfos[i - start * selectedSize].getFitness();
            fitnessCall++;
            if (Force[i] > btgValue) {
                btgValue = Force[i];
                btg = CH[i];
            }
        }
        if (btgValue > bsfValue) {
            bsf = btg.clone();
            bsfValue = btgValue;
            lastInnovation = 0;
        } else {
            lastInnovation++;
        }
    }

    private void SELECT() {
        double[] h;
        int i1, i2, dead, last;
        while (actualSize > selectedSize) {
            i1 = RND.getInt(0, actualSize - 1);
            i2 = RND.getInt(1, actualSize - 1);
            if (i1 == i2) i2--;
            if (Force[i1] >= Force[i2]) {
                dead = i2;
            } else {
                dead = i1;
            }
            last = actualSize - 1;
            h = CH[last];
            CH[last] = CH[dead];
            CH[dead] = h;
            if (btg == CH[last]) {
                btg = CH[dead];
            }
            Force[dead] = Force[last];
            actualSize--;
        }
    }

    private void MUTATE() {
        double p;
        double[] x;
        int index;
        for (int i = 0; i < selectedSize; i++) {
            if (actualSize == poolSize) {
                break;
            }
            p = RND.getDouble(0, 1);
            if (p <= radioactivity) {
                index = RND.getInt(0, selectedSize - 1);
                mutationRate = RND.getDouble(0, 1);
                x = newPoint();
                for (int j = 0; j < dimensions; j++) {
                    CH[actualSize][j] = CH[index][j] + mutationRate * (x[j] - CH[index][j]);
                }
                actualSize++;
            }
        }
    }

    private void LOCAL_MUTATE() {
        double p, dCH;
        int index;
        for (int i = 0; i < selectedSize; i++) {
            if (actualSize == poolSize) {
                break;
            }
            p = RND.getDouble(0, 1);
            if (p <= localRadioactivity) {
                index = RND.getInt(0, selectedSize - 1);
                for (int j = 0; j < dimensions; j++) {
                    dCH = RND.getDouble(-mutagen[j], mutagen[j]);
                    CH[actualSize][j] = CH[index][j] + dCH;
                }
                actualSize++;
            }
        }
    }

    private void CROSS() {
        int i1, i2, i3;
        while (actualSize < poolSize) {
            i1 = RND.getInt(0, selectedSize - 1);
            i2 = RND.getInt(1, selectedSize - 1);
            if (i1 == i2) {
                i2--;
            }
            i3 = RND.getInt(0, selectedSize - 1);
            for (int j = 0; j < dimensions; j++) {
                CH[actualSize][j] = CH[i3][j] + crossRate * (CH[i2][j] - CH[i1][j]);
            }
            actualSize++;
        }
    }

    /*
    * Original algorithm created whole poolSize of new individuals. I have changed this to create only a half (selectedSize)
    * to have the same number of evaluations each in generation including the first.
    */

    private void FIRST_GENERATION() {
        Force = new double[poolSize];
        CH = new double[poolSize][];
        for (int i = 0; i < poolSize; i++) {
            CH[i] = newPoint();
        }
        bsf = new double[dimensions];
//        actualSize = poolSize;
        actualSize = selectedSize;  //this was changed from poolSize
        EVALUATE_GENERATION(0);
//        SELECT();
    }

//---------- END SADE Technology -----------------------------------------------

    /**
     * Returns the "best so far" chromosome.
     *
     * @return BSF
     */
    public double[] getBsf() {
        return bsf;
    }

    public double getBestOfGenerationFitness() {
        return btgValue;
    }

    public void initialGeneration() {
        lastInnovation = 0;
        generation = 1;
        configuration();
        FIRST_GENERATION();
    }

    public void nextGeneration() {
        generation++;
        MUTATE();
        LOCAL_MUTATE();
        CROSS();
        EVALUATE_GENERATION(1);
        SELECT();
    }

    public void performGeneralizationTest() {
        generalizationEvaluationInfo = populationEvaluator.evaluateGeneralization(perThreadEvaluators, new SADEGenome(bsf));
        generalizationGeneration = generation;
    }

    public void finished() {
    }

    public boolean hasImproved() {
        return lastInnovation == 0;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEvaluations() {
        return fitnessCall;
    }

    public int getLastInnovation() {
        return lastInnovation;
    }

    public double getMaxFitnessReached() {
        return bsfValue;
    }

    public EvaluationInfo[] getEvaluationInfo() {
        return evaluationInfos;
    }

    public EvaluationInfo getGeneralizationEvaluationInfo() {
        if (generation != generalizationGeneration) {
            throw new IllegalStateException("Generalization was not called this generation!");
        }
        return generalizationEvaluationInfo;
    }

    public boolean isSolved() {
        for (Evaluable<SADEGenome> evaluator : perThreadEvaluators) {
            if (evaluator.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public String getConfigString() {
        return "IMPLEMENT SADE.getConfigString()";
    }
}