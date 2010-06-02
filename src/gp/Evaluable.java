package gp;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 3:40:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Evaluable {
    public double evaluate(Forest forest);

    public boolean isSolved();

    public int getNumberOfInputs();

    public int getNumberOfOutputs();
}
