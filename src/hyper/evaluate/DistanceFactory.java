package hyper.evaluate;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gep.GEPChromosomeDistance;
import gp.ForestDistance;
import gp.distance.*;
import gpaac.AACForestDistance;
import gpat.ATForestDistance;
import gpat.distance.*;
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
        if (parameters == null) {
            //TODO remove
            return new GenomeDistance();
        }
        String solver = parameters.getString("SOLVER").toUpperCase();
        if (solver.equals("NEAT")) {
            return new GenomeDistance();
        } else if (solver.equals("GP")) {
            if (parameters.getString("GP.TYPE").equals("gp.GP")) {
                return new ForestDistance();
            } else if (parameters.getString("GP.TYPE").equals("gep.GEP")) {
                return new GEPChromosomeDistance();
            } else if (parameters.getString("GP.TYPE").equals("gpaac.GPAAC")) {
                return new AACForestDistance();
            } else if (parameters.getString("GP.TYPE").equals("gp.GPEFS")) {
                if (parameters.getString("GP.DISTANCE").equals("BASIC")) {
                    return new ForestDistance();
                } else if (parameters.getString("GP.DISTANCE").equals("RANDOM")) {
                    return new ForestDistance(new TreeDistanceRandom());
                } else if (parameters.getString("GP.DISTANCE").equals("RESTO2")) {
                    return new ForestDistance(new TreeDistanceOnlyRest2());
                } else if (parameters.getString("GP.DISTANCE").equals("TREEP")) {
                    return new ForestDistance(new TreeDistanceTreeplicity(parameters));
                } else if (parameters.getString("GP.DISTANCE").equals("TREEP2")) {
                    return new ForestDistance(new TreeDistanceTreeplicity2(parameters));
                } else if (parameters.getString("GP.DISTANCE").equals("GENERAL")) {
                    return new ForestDistance(new TreeDistanceGeneral(parameters));
                } else if (parameters.getString("GP.DISTANCE").equals("NODES")) {
                    return new ForestDistance(new TreeDistanceNodes());
                } else if (parameters.getString("GP.DISTANCE").equals("ISO")) {
                    return new ForestDistance(new TreeDistanceIsomorphs());
                } else if (parameters.getString("GP.DISTANCE").equals("PHENO")) {
                    return new ForestDistance(new TreeDistancePhenotypic(parameters));
                } else {
                    throw new IllegalStateException("GP.DISTANCE: " + parameters.getString("GP.DISTANCE"));
                }
            } else {
                throw new IllegalStateException("Unknown GP.TYPE: " + parameters.getString("GP.TYPE"));
            }
        } else if (solver.equals("GPAT")) {
            if (parameters.getString("GPAT.DISTANCE").equals("BASIC")) {
                return new ATForestDistance();
            } else if (parameters.getString("GPAT.DISTANCE").equals("CONSTANT")) {
                return new ATForestDistance(new ATTreeDistanceConstant());
            } else if (parameters.getString("GPAT.DISTANCE").equals("RANDOM")) {
                return new ATForestDistance(new ATTreeDistanceRandom());
            } else if (parameters.getString("GPAT.DISTANCE").equals("GENERAL")) {
                return new ATForestDistance(new ATTreeDistanceGeneral(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("ROOTO")) {
                return new ATForestDistance(new ATTreeDistanceRootsOnly());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE")) {
                return new ATForestDistance(new ATTreeDistanceSimple());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE2")) {
                return new ATForestDistance(new ATTreeDistanceSimple2());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC2")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent2());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC3")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent3());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC4")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent4());
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC5")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent5(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC6")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent6(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("SIMPLE_REC7")) {
                return new ATForestDistance(new ATTreeDistanceSimpleRecurrent7(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("WEIGHTED")) {
                return new ATForestDistance(new ATTreeDistanceWeighted(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("WEIGHTEDO")) {
                return new ATForestDistance(new ATTreeDistanceWeightedOnly(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("WEIGHTEDO2")) {
                return new ATForestDistance(new ATTreeDistanceWeightedOnly2(parameters));
            } else if (parameters.getString("GPAT.DISTANCE").equals("RESTO")) {
                return new ATForestDistance(new ATTreeDistanceOnlyRest());
            } else if (parameters.getString("GPAT.DISTANCE").equals("RESTO2")) {
                return new ATForestDistance(new ATTreeDistanceOnlyRest2());
            } else if (parameters.getString("GPAT.DISTANCE").equals("PHENO")) {
                return new ATForestDistance(new ATTreeDistancePhenotypic(parameters));
            } else {
                throw new IllegalStateException("GPAT.DISTANCE: " + parameters.getString("GPAT.DISTANCE"));
            }
        } else if (solver.equals("GPATS")) {
            return new ATForestDistance();
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
