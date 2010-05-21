package gp;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 22, 2009
 * Time: 10:18:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class InnovationCounter {
    private int number = 1;
    private static InnovationCounter instance = null;

    private InnovationCounter() {
    }

    public static InnovationCounter getInstance() {
        if(instance == null) {
            instance = new InnovationCounter();
        }
        return instance;
    }

    public int getNext() {
        return number++;
    }
}
