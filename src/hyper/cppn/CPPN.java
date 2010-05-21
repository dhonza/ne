package hyper.cppn;

import hyper.substrate.Coordinate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 12:42:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CPPN {
    public double evaluate(final int outputId, final Coordinate from, final Coordinate to);

    public int getNumInputs();
}
