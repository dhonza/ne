package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.experiments.findcluster.FindCluster;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.Substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 23, 2010
 * Time: 5:19:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolverFactory {
    public static Solver getSolver(ParameterCombination parameters, Substrate substrate, Stats stats, ReportStorage reportStorage) {
//                Problem problem = new Recognition1D(combination);
        Problem problem = new FindCluster(parameters);

        String name = parameters.getString("SOLVER");
        if (name.equalsIgnoreCase("GP")) {
            return new GPSolver(parameters, substrate, stats, reportStorage);
        } else if (name.equalsIgnoreCase("NEAT")) {
            System.out.println("SolverFactory!");
            System.exit(1);
            return new NEATSolver(parameters, substrate, stats, problem, reportStorage);
        } else if (name.equalsIgnoreCase("SNEAT")) {
            System.out.println("SolverFactory!");
            System.exit(1);
            return new SNEATSolver(parameters, substrate, stats, problem, reportStorage);
        } else {
            throw new IllegalStateException("Unknown solver: \"" + name + "\"");
        }
    }
}
