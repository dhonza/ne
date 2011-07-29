package common;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 7/27/11
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListHelper {
    public static String asStringInLines(List list) {
        StringBuilder builder = new StringBuilder();
        for (Object o : list) {
            builder.append(o.toString()).append('\n');
        }
        return builder.toString();
    }
}
