package common.mathematica;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2010
 * Time: 11:06:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class MathematicaUtils {
    public static String toMathematica(double n) {
        return Double.toString(n).toUpperCase().replace("E", "*10^");    
    }
}
