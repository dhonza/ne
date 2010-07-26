package hyper.evaluate;

import common.evolution.EvaluationInfo;
import hyper.substrate.ISubstrate;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 12:17:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IProblem<INet> extends Serializable {
    EvaluationInfo evaluate(INet hyperNet);

    boolean isSolved();

    void show(INet hyperNet);

    double getTargetFitness();

    ISubstrate getSubstrate();

    List<String> getEvaluationInfoItemNames();
}
