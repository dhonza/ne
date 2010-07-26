package hyper.builder;

import common.pmatrix.ParameterCombination;
import hyper.builder.precompiled.PrecompiledFeedForwardSubstrateBuilder;
import hyper.substrate.ISubstrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2010
 * Time: 3:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubstrateBuilderFactory {
    public static IEvaluableSubstrateBuilder createEvaluableSubstrateBuilder(ISubstrate substrate, ParameterCombination parameters) {
        IWeightEvaluator weightEvaluator = WeightEvaluatorFactory.createWeightEvaluator(parameters);
        String type = parameters.getString("BUILDER").toLowerCase();
        if (type.equals("basic")) {
            return new NetSubstrateBuilder(substrate, weightEvaluator);
        } else if (type.equals("precompiled")) {
            return new PrecompiledFeedForwardSubstrateBuilder(substrate, weightEvaluator);
        } else if (type.equals("cascade")) {
            return new CascadeNetBuilder(substrate, weightEvaluator);
        } else {
            throw new IllegalStateException("Invalid BUILDER option in configuration: " + type);
        }
    }
}
