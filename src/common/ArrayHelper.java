package common;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 15, 2010
 * Time: 12:26:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayHelper {
    public static void range(int[] array, int min) {
        for (int i = 0; i < array.length; i++) {
            array[i] = min++;
        }
    }
}
