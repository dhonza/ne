package common.evolution;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 3:40:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEvaluable<P> extends Serializable {
    public EvaluationInfo evaluate(P individual);

    public EvaluationInfo evaluateGeneralization(P individual);

    public boolean isSolved();

    public int getNumberOfInputs();

    public int getNumberOfOutputs();
}
