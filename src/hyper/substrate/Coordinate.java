package hyper.substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:50:50 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Implementing class must be immutable.
 */

public interface Coordinate {
    public double[] asArray();

    public int getDimension();

    public double get(int i);
}
