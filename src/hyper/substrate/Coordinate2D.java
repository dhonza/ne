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
public class Coordinate2D implements ICoordinate {
    final private double x;
    final private double y;

    public Coordinate2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double[] asArray() {
        return new double[]{x, y};
    }

    public int getDimension() {
        return 2;
    }

    public double get(int i) {
        if (i == 0) {
            return x;
        } else if (i == 1) {
            return y;
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}
