package hyper.evaluate;

import common.evolution.StopCondition;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 4:15:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolvedStopCondition implements StopCondition {
    final private IProblem problem;

    public SolvedStopCondition(IProblem problem) {
        this.problem = problem;
    }

    public boolean isMet() {
        return problem.isSolved();
    }
}
