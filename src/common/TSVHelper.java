package common;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 02.03.12
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
public class TSVHelper {
    public static String arrayToTSV(int[] array) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            b.append(array[i]).append("\t");
        }
        b.append(array[array.length - 1]);
        return b.toString();
    }
}
