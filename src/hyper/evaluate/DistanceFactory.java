package hyper.evaluate;

import common.evolution.IDistance;
import common.pmatrix.ParameterCombination;
import gep.GEPChromosomeDistance;
import gp.ForestDistance;
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
        String solver = parameters.getString("SOLVER").toUpperCase();
        if (solver.equals("NEAT")) {
            return new GenomeDistance();
        } else if (solver.equals("GP")) {
            if (parameters.getString("GP.TYPE").equals("gep.GEP")) {
                return new GEPChromosomeDistance();
            }
            if (parameters.getString("GP.TYPE").equals("gpaac.GPAAC")) {
                return new AACForestDistance();
            }
            return new ForestDistance();
        } else if (solver.equals("GPAT")) {
            if (parameters.getString("GPAT.DISTANCE").equals("BASIC")) {
                return new ATForestDistance();
            } else if (parameters.getString("GPAT.DISTANCE").equals("CONSTANT")) {
                return new ATForestDistance(new ATTreeDistanceConstant());
            } else if (parameters.getString("GPAT.DISTANCE").equals("RANDOM")) {
                return new ATForestDistance(new ATTreeDistanceRandom());
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
