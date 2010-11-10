package common;

import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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

    private static double SAMPLE_WITHOUT_REPLACEMENT_RATIO = 0.2;
    private static double SAMPLE_RANGE_WITHOUT_REPLACEMENT_RATIO = 0.2;

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
     * Fills a random boolean array.
     *
     * @param array to fill
     */
    public static void fillBoolean(boolean[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = seed.nextBoolean();
        }
    }

    /**
     * Returns a random double from the given range.
     *
     * @param min the lower bound
     * @param max the higher bound
     * @return double from <i>&lt;min; max&gt;</i>
     */
    public static double getDouble(double min, double max) {
        return (max - min) * seed.nextDouble() + min;
    }

    /**
     * Fills a random double array from the given range.
     *
     * @param max
     * @param min
     * @param array to fill from <i>&lt;min; max&gt;</i>
     */
    public static void fillDouble(double[] array, double min, double max) {
        double range = max - min;
        for (int i = 0; i < array.length; i++) {
            array[i] = range * seed.nextDouble() + min;
        }
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
     * Fills a random double array.
     *
     * @param array to fill from <i>&lt;0; 1&gt;</i>
     */
    public static void fillDouble(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = seed.nextDouble();
        }
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
     * Fills a random integer array from given range. Both bounds are inclusive.
     *
     * @param min   the lower bound
     * @param max   the higher bound
     * @param array to fill from <i>&lt;min; max&gt;</i>
     */
    public static void fillInt(int[] array, int min, int max) {
        int base = max - min + 1;
        for (int i = 0; i < array.length; i++) {
            array[i] = seed.nextInt(base) + min;
        }
    }

    /**
     * Returns a random integer from range 0 to <i>max-1</i>.
     * Both bounds are inclusive.
     *
     * @param max the higher bound
     * @return the int from <i>&lt;0; max)</i>
     */

    public static int getIntZero(int max) {
        return seed.nextInt(max);
    }

    /**
     * Returns a random integer from range 1 to <i>max</i>.
     * Both bounds are inclusive.
     *
     * @param max the higher bound
     * @return the int from <i>&lt;1; max&gt;</i>
     */

    public static int getIntOne(int max) {
        return seed.nextInt(max) + 1;
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
     * Randomly shuffles array of generic objects. Using Durstenfeld's algorithm.
     * See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
     *
     * @param array to shuffle
     */
    public static <T> void shuffle(T[] array) {
        for (int i = array.length; i > 1; i--) {
            int r = seed.nextInt(i); // r one of 0..(i-1)
            T tmp = array[i - 1];
            array[i - 1] = array[r];
            array[r] = tmp;
        }
    }

    /**
     * Randomly shuffles array of int. Using Durstenfeld's algorithm.
     * See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle
     *
     * @param array to shuffle
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
     * @param array to shuffle
     */
    public static void shuffle(double[] array) {
        for (int i = array.length; i > 1; i--) {
            int r = seed.nextInt(i); // r one of 0..(i-1)
            double tmp = array[i - 1];
            array[i - 1] = array[r];
            array[r] = tmp;
        }
    }

    // RANDOM CHOICE

    /**
     * Random choice from generic object array.
     *
     * @param array source array
     * @return randomly chosen element of the array
     */
    public static <T> T randomChoice(T[] array) {
        return array[seed.nextInt(array.length)];
    }

    /**
     * Random choice from List.
     *
     * @param list source List
     * @return randomly chosen element of the list
     */
    public static <T> T randomChoice(List<T> list) {
        return list.get(seed.nextInt(list.size()));
    }

    // SAMPLE WITH REPLACEMENT

    /**
     * Simple random sample with replacement (SRSWR) from generic object array.
     * See http://en.wikipedia.org/wiki/Simple_random_sample
     *
     * @param array  source array
     * @param sample target array, its size determines the size of the sample
     */
    public static <T> void sampleWithReplacement(T[] array, T[] sample) {
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

    /**
     * Simple random sample without replacement (SRSWOR) from int array.
     * See http://en.wikipedia.org/wiki/Simple_random_sample
     *
     * @param array  source array
     * @param sample target array, its size determines the size of the sample
     */
    public static void sampleWithoutReplacement(int[] array, int[] sample) {
        if (array.length < sample.length) {
            throw new IllegalArgumentException("Array size < sample size!");
        }
        if (((double) sample.length / (double) array.length) < SAMPLE_WITHOUT_REPLACEMENT_RATIO) {
            sampleWithoutReplacement2(array, sample);
        } else {
            sampleWithoutReplacement1(array, sample);
        }
    }

    //Shuffles cloned array of all numbers -> slow for small samples

    private static void sampleWithoutReplacement1(int[] array, int[] sample) {
        int[] t = array.clone();
        shuffle(t);
        System.arraycopy(t, 0, sample, 0, sample.length);
    }

    //Picks not sampled numbers -> fast for small samples

    private static void sampleWithoutReplacement2(int[] array, int[] sample) {
        int cnt = 0;
        while (cnt < sample.length) {
            int r = array[seed.nextInt(array.length)];
            boolean alreadyChosen = false;
            for (int i = 0; i < cnt; i++) {
                if (sample[i] == r) {
                    alreadyChosen = true;
                    break;
                }
            }
            if (!alreadyChosen) {
                sample[cnt++] = r;
            }
        }
    }

    public static void sampleRangeWithoutReplacement(int range, int[] sample) {
        sampleRangeWithoutReplacement(0, range - 1, sample);
    }

    public static void sampleRangeWithReplacement(int range, int[] sample) {
        for (int i = 0; i < sample.length; i++) {
            sample[i] = seed.nextInt(range);
        }
    }

    public static void sampleRangeWithoutReplacementSorted(int range, int[] sample) {
        sampleRangeWithoutReplacementSorted(0, range - 1, sample);
    }

    public static void sampleRangeWithoutReplacementSorted(int min, int max, int[] sample) {
        sampleRangeWithoutReplacement(min, max, sample);
        Arrays.sort(sample);
    }

    public static void sampleRangeWithoutReplacement(int min, int max, int[] sample) {
        int range = max - min + 1;
        if (range < 0) {
            throw new IllegalArgumentException(" Min: " + min + " max:" + max + " negative range!");
        }
        if (range < sample.length) {
            throw new IllegalArgumentException("Array size: " + range + " < sample size: " + sample.length + "!");
        }
        //the constant to switch between algorithms was chosen experimentally
        if (((double) sample.length / (double) range) < SAMPLE_RANGE_WITHOUT_REPLACEMENT_RATIO) {
            sampleRangeWithoutReplacement2(min, max, sample);
        } else {
            sampleRangeWithoutReplacement1(min, max, sample);
        }
    }

    //Shuffles array of all numbers -> slow for small samples

    private static void sampleRangeWithoutReplacement1(int min, int max, int[] sample) {
        int range = max - min + 1;

        int[] a = new int[range];
        ArrayHelper.range(a, min);
        RND.shuffle(a);
        System.arraycopy(a, 0, sample, 0, sample.length);
    }

    //Picks not sampled numbers -> fast for small samples

    private static void sampleRangeWithoutReplacement2(int min, int max, int[] sample) {
        int range = max - min + 1;
        int cnt = 0;
        while (cnt < sample.length) {
            int r = seed.nextInt(range) + min;
            boolean alreadyChosen = false;
            for (int i = 0; i < cnt; i++) {
                if (sample[i] == r) {
                    alreadyChosen = true;
                    break;
                }
            }
            if (!alreadyChosen) {
                sample[cnt++] = r;
            }
        }
    }

    public static void main(String[] args) {
        int[] a = new int[10];
        ArrayHelper.range(a, 1);
        RND.initializeTime();
        System.out.println(ArrayUtils.toString(a));
        int[] s = new int[10];
        RND.sampleWithReplacement(a, s);
        System.out.println(ArrayUtils.toString(s));
        RND.sampleWithoutReplacement2(a, s);
        System.out.println(ArrayUtils.toString(s));

        sampleRangeWithoutReplacement2(0, 9, s);
        System.out.println(ArrayUtils.toString(s));
        System.out.println("");

        //tuning sample without replacement
        for (int i = 100; i <= 1500; i += 100) {
            int[] array = new int[i];
            ArrayHelper.range(array, 1);
            for (int j = 2; j <= i; j += i / 100) {
                Bench b = new Bench();
                for (int k = 0; k <= 1000; k++) {
                    s = new int[j];
                    sampleWithoutReplacement1(array, s);
//                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
                }
                double time = b.stop() / 1000.0;

                b = new Bench();
                for (int k = 0; k <= 1000; k++) {
                    s = new int[j];
                    sampleWithoutReplacement2(array, s);
//                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
                }
                double time2 = b.stop() / 1000.0;

                b = new Bench();
                for (int k = 0; k <= 1000; k++) {
                    s = new int[j];
                    sampleRangeWithoutReplacement(0, i - 1, s);
//                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
                }
                double time3 = b.stop() / 1000.0;
                System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " TIME (s): " + time + " / " + time2 + " / " + time3);
            }
        }

//        //tuning range sample without replacement
//        for (int i = 100; i <= 1000; i += 100) {
//            for (int j = 2; j <= i; j += i / 100) {
//                Bench b = new Bench();
//                for (int k = 0; k <= 1000; k++) {
//                    s = new int[j];
//                    sampleRangeWithoutReplacement1(0, i - 1, s);
////                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
//                }
//                double time = b.stop() / 1000.0;
//
//                b = new Bench();
//                for (int k = 0; k <= 1000; k++) {
//                    s = new int[j];
//                    sampleRangeWithoutReplacement2(0, i - 1, s);
////                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
//                }
//                double time2 = b.stop() / 1000.0;
//                b = new Bench();
//                for (int k = 0; k <= 1000; k++) {
//                    s = new int[j];
//                    sampleRangeWithoutReplacement(0, i - 1, s);
////                    System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " " + ArrayUtils.toString(s));
//                }
//                double time3 = b.stop() / 1000.0;
//                System.out.println("RANGE: " + i + " SAMPLE SIZE: " + j + " TIME (s): " + time + " / " + time2 + " / " + time3);
//            }
//        }
    }
}