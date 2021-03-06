package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.ale.ALEExperiment;
import hyper.experiments.findcluster.FindCluster;
import hyper.experiments.findcluster2.FindCluster2;
import hyper.experiments.octopusArm.OctopusArm;
import hyper.experiments.reco.problem.Recognition1D;
import hyper.experiments.robots.Robots;

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

    public static IProblem getProblem(ParameterCombination parameters, ReportStorage reportStorage) {
        String name = parameters.getString("PROBLEM");
        if (name.equalsIgnoreCase("RECO")) {
            return new Recognition1D(parameters);
        } else if (name.equalsIgnoreCase("XOR")) {
            return new Recognition1D(parameters);
        } else if (name.equalsIgnoreCase("AND")) {
            return new Recognition1D(parameters);
        } else if (name.equalsIgnoreCase("FIND_CLUSTER")) {
            return new FindCluster(parameters, reportStorage);
        } else if (name.equalsIgnoreCase("FIND_CLUSTER2")) {
            return new FindCluster2(parameters, reportStorage);
        } else if (name.equalsIgnoreCase("ROBOTS")) {
            return new Robots(parameters, reportStorage);
        } else if (name.equalsIgnoreCase("OCTOPUS")) {
            return new OctopusArm(parameters, reportStorage);
        } else if (name.equalsIgnoreCase("ALE")) {
            return new ALEExperiment(parameters, reportStorage);
        } else {
            throw new IllegalStateException("Unknown problem: \"" + name + "\"");
        }
    }
}
