package hyper.evaluate.storage;

import common.net.linked.Net;
import common.net.linked.NetStorage;
import common.pmatrix.ParameterCombination;
import common.xml.XMLSerialization;
import gp.Forest;
import gp.ForestStorage;
import neat.Genome;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.xml.XmlGenomeWriterStatic;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/11/11
 * Time: 7:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenomeStorage {
    public static void saveGenome(ParameterCombination parameters, Object genome, String fileName) {
        //TODO better use special interface for "genome"
        if (parameters.getString("SOLVER").equals("GP") || parameters.getString("SOLVER").equals("MOGP")) {
            System.out.println("Now storing only Forest (GP) implement for GEP!!!");
            if (genome instanceof Forest) {
                ForestStorage.save((Forest) genome, fileName);
            }
        } else if (parameters.getString("SOLVER").equals("GPAT")) {

        } else if (parameters.getString("SOLVER").equals("NEAT")) {
            NetStorage.save((Net) genome, fileName);
        } else if (parameters.getString("SOLVER").equals("SNEAT")) {
            XmlGenomeWriterStatic.Write(new File(fileName),
                    (NeatGenome) genome,
                    ActivationFunctionFactory.getActivationFunction("NullFn"));
        } else if (parameters.getString("SOLVER").equals("DIRECT_CMAES")) {
            XMLSerialization.save(genome, fileName);
        } else if (parameters.getString("SOLVER").equals("DIRECT_SADE")) {
            XMLSerialization.save(genome, fileName);
        } else {
            throw new IllegalStateException("Unknown SOLVER: " + parameters.getString("SOLVER"));
        }
    }

    public static Object loadGenome(ParameterCombination parameters, String fileName) {
        if (parameters.getString("SOLVER").equals("GP") || parameters.getString("SOLVER").equals("MOGP")) {
            return ForestStorage.load(fileName);
        } else if (parameters.getString("SOLVER").equals("GPAT")) {
            return XMLSerialization.load(fileName);
        } else if (parameters.getString("SOLVER").equals("NEAT")) {
            Net aCPPNNet = (Net) XMLSerialization.load(fileName);
            return new Genome(aCPPNNet);
        } else if (parameters.getString("SOLVER").equals("SNEAT")) {
            throw new IllegalStateException("Not yet implemented!");
        } else if (parameters.getString("SOLVER").equals("DIRECT_CMAES")) {
            return XMLSerialization.load(fileName);
        } else if (parameters.getString("SOLVER").equals("DIRECT_SADE")) {
            return XMLSerialization.load(fileName);
        } else {
            throw new IllegalStateException("Unknown SOLVER: " + parameters.getString("SOLVER"));
        }
    }
}
