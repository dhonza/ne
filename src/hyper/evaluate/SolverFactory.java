package hyper.evaluate;

import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.experiments.reco.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 23, 2010
 * Time: 5:19:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolverFactory {
    private SolverFactory() {
    }

    public static Solver getSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        String name = parameters.getString("SOLVER");
        if (name.equalsIgnoreCase("GP")) {
            return new GPSolver(parameters, stats, reportStorage);
        } else if (name.equalsIgnoreCase("NEAT")) {
            return new NEATSolver(parameters, stats, reportStorage);
        } else if (name.equalsIgnoreCase("SNEAT")) {
            return new SNEATSolver(parameters, stats, reportStorage);
        } else if (name.equalsIgnoreCase("DIRECT_SADE")) {
            return new DirectSADESolver(parameters, stats, reportStorage);
        } else {
            throw new IllegalStateException("Unknown solver: \"" + name + "\"");
        }
    }
}
