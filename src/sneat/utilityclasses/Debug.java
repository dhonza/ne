package sneat.utilityclasses;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Feb 12, 2010
 * Time: 11:52:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class Debug {

    //TODO remove SharpNEAT util
    public static void Assert(boolean cond, String message) {
        if (!cond) {
            System.out.println("Debug.assert: " + message);
            System.exit(1);
        }
    }
}
