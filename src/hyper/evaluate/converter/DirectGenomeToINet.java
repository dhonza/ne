package hyper.evaluate.converter;

import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.cppn.ICPPN;
import hyper.cppn.FakeArrayCPPN;
import opt.DoubleVectorGenome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:45:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectGenomeToINet implements IGenotypeToPhenotype<DoubleVectorGenome, INet> {
    final private IEvaluableSubstrateBuilder substrateBuilder;

    public DirectGenomeToINet(IEvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(DoubleVectorGenome genome) {
        ICPPN aCPPN = new FakeArrayCPPN(genome.genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }

    public int getNumOfLinks() {
        return substrateBuilder.getSubstrate().getNumOfLinks();
    }
}
