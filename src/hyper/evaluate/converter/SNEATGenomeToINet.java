package hyper.evaluate.converter;

import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.ICPPN;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:43:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATGenomeToINet implements IGenotypeToPhenotype<INetwork, INet> {
    final private IEvaluableSubstrateBuilder substrateBuilder;

    public SNEATGenomeToINet(IEvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet transform(INetwork genome) {
        ICPPN aCPPN = new BasicSNEATCPPN(genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
