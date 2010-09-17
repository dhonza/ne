package hyper.evaluate;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gep.GEPChromosomeDistance;
import gp.ForestDistance;
import neat.GenomeDistance;
import opt.DoubleVectorGenomeDistance;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 2, 2010
 * Time: 11:43:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class DistanceFactory {
    private DistanceFactory() {
    }

    public static IDistance createGenomeDistance(ParameterCombination parameters) {
        String solver = parameters.getString("SOLVER").toUpperCase();
        if (solver.equals("NEAT")) {
            return new GenomeDistance();
        } else if (solver.equals("GP")) {
            if (parameters.getString("GP.TYPE").equals("gep.GEP")) {
                return new GEPChromosomeDistance();
            }
            return new ForestDistance();
        } else if (solver.equals("SNEAT")) {
            return new ForestDistance();
        } else if (solver.equals("DIRECT_CMAES")) {
            return new DoubleVectorGenomeDistance();
        } else if (solver.equals("DIRECT_SADE")) {
            return new DoubleVectorGenomeDistance();
        } else {
            throw new IllegalStateException("Invalid SOLVER: " + solver);
        }
    }
}
