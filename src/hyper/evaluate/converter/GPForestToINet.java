package hyper.evaluate.converter;

import common.evolution.GenotypeToPhenotype;
import common.net.INet;
import gp.Forest;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.CPPN;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:29:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPForestToINet implements GenotypeToPhenotype<Forest, INet> {
    final private EvaluableSubstrateBuilder substrateBuilder;

    public GPForestToINet(EvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(Forest genome) {
        CPPN aCPPN = new BasicGPCPPN(genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
