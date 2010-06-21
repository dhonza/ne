package hyper.evaluate;

import common.evolution.Evaluable;
import common.pmatrix.ParameterCombination;
import hyper.builder.EvaluableSubstrateBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 11:25:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluableFactory {
    private EvaluableFactory() {
    }

    public static Evaluable getEvaluable(ParameterCombination parameters, EvaluableSubstrateBuilder substrateBuilder, Problem problem) {
        String name = parameters.getString("SOLVER");
        if (name.equalsIgnoreCase("GP")) {
            return new GPEvaluator(substrateBuilder, problem);
        } else if (name.equalsIgnoreCase("NEAT")) {
            return new NEATEvaluator(substrateBuilder, problem);
        } else if (name.equalsIgnoreCase("SNEAT")) {
            System.out.println("EvaluableFactory!");
            System.exit(1);
//            return new SNEATNetworkEvaluator(substrateBuilder, problem);
        } else {
            throw new IllegalStateException("Unknown solver: \"" + name + "\"");
        }
        return null;
    }
}