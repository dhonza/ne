package common.evolution;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 3:40:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Evaluable<T> extends Serializable {
    public double evaluate(T individual);

    public boolean isSolved();

    public int getNumberOfInputs();

    public int getNumberOfOutputs();
}
