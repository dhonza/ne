package hyper.substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 7:06:36 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class Coordinate1D implements ICoordinate {
    final private double x;

    public Coordinate1D(double x) {
        this.x = x;
    }

    public double[] asArray() {
        return new double[]{x};
    }

    public int getDimension() {
        return 1;
    }

    public double get(int i) {
        if (i != 0) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        return x;
    }

    @Override
    public String toString() {
        return String.valueOf(x);
    }
}