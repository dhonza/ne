package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 6/20/11
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum GenomeCounter {
    INSTANCE;

    private int cnt = 1;

    public int getNext() {
        return cnt++;
    }
}
