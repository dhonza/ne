package hyper.builder;

import common.pmatrix.ParameterCombination;
import hyper.builder.precompiled.PrecompiledFeedForwardSubstrateBuilder;
import hyper.substrate.Substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2010
 * Time: 3:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubstrateBuilderFactory {
    public static EvaluableSubstrateBuilder createEvaluableSubstrateBuilder(Substrate substrate, ParameterCombination parameters) {
        String type = parameters.getString("BUILDER").toLowerCase();
        if (type.equals("basic")) {
            return new NetSubstrateBuilder(substrate);
        } else if (type.equals("precompiled")) {
            return new PrecompiledFeedForwardSubstrateBuilder(substrate);
        } else {
            throw new IllegalStateException("Invalid BUILDER option in configuration: " + type);
        }
    }
}
