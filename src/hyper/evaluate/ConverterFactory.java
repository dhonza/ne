package hyper.evaluate;

import common.evolution.GenotypeToPhenotype;
import common.pmatrix.ParameterCombination;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.evaluate.converter.DirectGenomeToINet;
import hyper.evaluate.converter.GPForestToINet;
import hyper.evaluate.converter.NEATGenomeToInet;
import hyper.evaluate.converter.SNEATGenomeToINet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 11:25:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConverterFactory {
    private ConverterFactory() {
    }

    public static GenotypeToPhenotype getConverter(ParameterCombination parameters, EvaluableSubstrateBuilder substrateBuilder, IProblem problem) {
        String name = parameters.getString("SOLVER");
        if (name.equalsIgnoreCase("GP")) {
            return new GPForestToINet(substrateBuilder);
        } else if (name.equalsIgnoreCase("NEAT")) {
            return new NEATGenomeToInet(substrateBuilder);
        } else if (name.equalsIgnoreCase("SNEAT")) {
            return new SNEATGenomeToINet(substrateBuilder);
        } else if (name.equalsIgnoreCase("DIRECT_SADE")) {
            return new DirectGenomeToINet(substrateBuilder);
        } else if (name.equalsIgnoreCase("DIRECT_CMAES")) {
            return new DirectGenomeToINet(substrateBuilder);
        } else {
            throw new IllegalStateException("Unknown converter: \"" + name + "\"");
        }
    }
}