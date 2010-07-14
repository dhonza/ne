package hyper.evaluate;

import common.evolution.EvaluationInfo;
import common.net.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 14, 2010
 * Time: 10:53:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IProblemGeneralization {
    EvaluationInfo evaluateGeneralization(INet hyperNet);

    void showGeneralization(INet hyperNet);
}
