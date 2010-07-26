package hyper.evaluate.converter;

import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import gp.Forest;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.cppn.BasicGPCPPN;
import hyper.cppn.ICPPN;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:29:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPForestToINet implements IGenotypeToPhenotype<Forest, INet> {
    final private IEvaluableSubstrateBuilder substrateBuilder;

    public GPForestToINet(IEvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(Forest genome) {
        ICPPN aCPPN = new BasicGPCPPN(genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
