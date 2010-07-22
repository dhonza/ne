package hyper.evaluate.converter;

import common.evolution.GenotypeToPhenotype;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.CPPN;
import sneat.neuralnetwork.INetwork;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:43:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATGenomeToINet implements GenotypeToPhenotype<INetwork, INet> {
    final private EvaluableSubstrateBuilder substrateBuilder;

    public SNEATGenomeToINet(EvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(INetwork genome) {
        CPPN aCPPN = new BasicSNEATCPPN(genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
