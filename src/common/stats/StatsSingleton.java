package common.stats;

/**
 * User: honza
 * Date: Aug 10, 2006
 * Time: 2:28:08 PM
 */
public class StatsSingleton {
    private static Stats instance = null;

    public static Stats getInstance() {
        if (instance == null) {
            instance = new Stats();
        }
        return instance;
    }
}