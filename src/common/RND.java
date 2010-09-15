package common;

import java.io.Serializable;
import java.util.Random;

/**
 * <p>Title: Common Classes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class performs operations with random numbers.
 */

public class RND implements Serializable {

    /**
     * The seed of random numbers.
     */
    private static Random seed;
    private static final long RND_SEED = 1111L;

    private static volatile long seedUniquifier = 8682522807148012L; //inspired by SUN JAVA SDK Random.java version 1.43, 01/12/04

    /**
     * Initializes the random number generator. The seed is generated from time.
     *
     * @return Time generated seed - usable in <i>initialize(int oseed)</i>.
     */
    public static long initializeTime() {
        long timeSeed = ++seedUniquifier + System.nanoTime(); //inspired by SUN JAVA SDK Random.java version 1.43, 01/12/04
        seed = new Random(timeSeed);
        return timeSeed;
    }

    public static int initializeTimeInt() {
        long timeSeed = ++seedUniquifier + System.nanoTime();
        int intSeed = (int) (timeSeed % (long) Integer.MAX_VALUE);
        seed = new Random((int) intSeed);
        return intSeed;
    }

    /**
     * Initializes the random number generator with a constant.
     */
    public static void initialize() {
        seed = new Random(RND_SEED);
    }

    /**
     * Initializes the random number generator.
     *
     * @param oseed Seed.
     */
    public static void initialize(long oseed) {
        seed = new Random(oseed);
    }


    /**
     * Returns a random boolean.
     *
     * @return random boolean
     */
    public static boolean getBoolean() {
        return seed.nextBoolean();
    }

    /**
     * Returns a random double from the given range.
     *
     * @param omin the lower bound
     * @param omax the higher bound
     * @return double from <i>&lt;omin; omax&gt;</i>
     */
    public static double getDouble(double omin, double omax) {
        return (omax - omin) * seed.nextDouble() + omin;
    }

    /**
     * Returns a random double.
     *
     * @return double from <i>&lt;0; 1&gt;</i>
     */
    public static double getDouble() {
        return seed.nextDouble();
    }

    /**
     * Returns a random integer from given range. Both bounds are inclusive.
     *
     * @param min the lower bound
     * @param max the higher bound
     * @return the int from <i>&lt;min; max&gt;</i>
     */
    public static int getInt(int min, int max) {
        return seed.nextInt(max - min + 1) + min;
    }

    /**
     * Returns a random integer from range 0 to <i>max</i>.
     * Both bounds are inclusive.
     *
     * @param max the higher bound
     * @return the int from <i>&lt;0; max&gt;</i>
     */

    public static int getIntZero(int max) {
        return seed.nextInt(max + 1);
    }

    /**
     * Returns a random number from the Cauchy distribution.
     * see http://en.wikipedia.org/wiki/Cauchy_distribution#Cumulative_distribution_function
     * and http://en.wikipedia.org/wiki/Inverse_transform_sampling
     *
     * @return random number from the Cauchy distribution
     */
    public static double getCauchy() {
        return Math.tan(Math.PI * (seed.nextDouble() - 0.5));
    }

    //------------ SHUFFLE -----------

    /**
     * Randomly shuffles array of Objects. Using Durstenfeld's algorithm.
     * See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
     *
     * @param array
     */
    public static void shuffle(Object[] array) {
        for (int i = array.length; i > 1; i--) {
            int r = seed.nextInt(i); // r one of 0..(i-1)
            Object tmp = array[i - 1];
            array[i - 1] = array[r];
            array[r] = tmp;
        }
    }

    /**
     * Randomly shuffles array of int. Using Durstenfeld's algorithm.
     * See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
     *
     * @param array
     */
    public static void shuffle(int[] array) {
        for (int i = array.length; i > 1; i--) {
            int r = seed.nextInt(i); // r one of 0..(i-1)
            int tmp = array[i - 1];
            array[i - 1] = array[r];
            array[r] = tmp;
        }
    }

    /**
     * Randomly shuffles array of double. Using Durstenfeld's algorithm.
     * See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
     *
     * @param array
     */
    public static void shuffle(double[] array) {
        for (int i = array.length; i > 1; i--) {
            int r = seed.nextInt(i); // r one of 0..(i-1)
            double tmp = array[i - 1];
            array[i - 1] = array[r];
            array[r] = tmp;
        }
    }

    // SAMPLE WITH REPLACEMENT

    /**
     * Simple random sample with replacement (SRSWR) from Object array.
     * See http://en.wikipedia.org/wiki/Simple_random_sample
     *
     * @param array  source array
     * @param sample target array, its size determines the size of the sample
     */
    public static void sampleWithReplacement(Object[] array, Object[] sample) {
        for (int i = 0; i < sample.length; i++) {
            sample[i] = array[seed.nextInt(array.length)];
        }
    }

    /**
     * Simple random sample with replacement (SRSWR) from int array.
     * See http://en.wikipedia.org/wiki/Simple_random_sample
     *
     * @param array  source array
     * @param sample target array, its size determines the size of the sample
     */
    public static void sampleWithReplacement(int[] array, int[] sample) {
        for (int i = 0; i < sample.length; i++) {
            sample[i] = array[seed.nextInt(array.length)];
        }
    }

    /**
     * Simple random sample with replacement (SRSWR) from double array.
     * See http://en.wikipedia.org/wiki/Simple_random_sample
     *
     * @param array  source array
     * @param sample target array, its size determines the size of the sample
     */
    public static void sampleWithReplacement(double[] array, double[] sample) {
        for (int i = 0; i < sample.length; i++) {
            sample[i] = array[seed.nextInt(array.length)];
        }
    }

    // SAMPLE WITHOUT REPLACEMENT
}