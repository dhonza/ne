package hyper.builder;

import common.pmatrix.ParameterCombination;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 5, 2010
 * Time: 6:03:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WeightEvaluatorFactory {
    public static IWeightEvaluator createWeightEvaluator(ParameterCombination parameters) {
        String type = parameters.getString("WEIGHT_EVALUATOR").toLowerCase();
        String solver = parameters.getString("SOLVER").toUpperCase();
        if (solver.startsWith("DIRECT_") || type.equals("identity")) {
            return new IdentityWeightEvaluator();
        } else if (type.equals("basic")) {
            return new BasicWeightEvaluator();
        } else if (type.equals("original")) {
            return new OriginalWeightEvaluator();
        } else if (type.equals("biased")) {
            return new BiasedWeightEvaluator();
        } else {
            throw new IllegalStateException("Invalid BUILDER option in configuration: " + type);
        }
    }
}
