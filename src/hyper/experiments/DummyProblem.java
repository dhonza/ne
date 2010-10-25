package hyper.experiments;

import common.evolution.EvaluationInfo;
import hyper.evaluate.IProblem;
import hyper.substrate.ISubstrate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 25, 2010
 * Time: 9:18:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DummyProblem implements IProblem {
    public EvaluationInfo evaluate(Object hyperNet) {
        return null;
    }

    public boolean isSolved() {
        return false;
    }

    public void show(Object hyperNet) {
    }

    public double getTargetFitness() {
        return Double.NaN;
    }

    public ISubstrate getSubstrate() {
        return null;
    }

    public List<String> getEvaluationInfoItemNames() {
        return new ArrayList<String>();
    }
}
