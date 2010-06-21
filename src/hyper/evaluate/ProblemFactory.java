package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import hyper.experiments.findcluster.FindCluster;
import hyper.experiments.reco.problem.Recognition1D;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 10:52:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProblemFactory {
    private ProblemFactory() {
    }

    public static Problem getProblem(ParameterCombination parameters) {
        String name = parameters.getString("PROBLEM");
        if (name.equalsIgnoreCase("RECO")) {
            return new Recognition1D(parameters);
        } else if (name.equalsIgnoreCase("FIND_CLUSTER")) {
            return new FindCluster(parameters);
        } else {
            throw new IllegalStateException("Unknown problem: \"" + name + "\"");
        }
    }
}
