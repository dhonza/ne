package hyper.evaluate.converter;

import common.evolution.GenotypeToPhenotype;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.CPPN;
import hyper.cppn.FakeArrayCPPN;
import opt.DoubleVectorGenome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:45:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectGenomeToINet implements GenotypeToPhenotype<DoubleVectorGenome, INet> {
    final private EvaluableSubstrateBuilder substrateBuilder;

    public DirectGenomeToINet(EvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(DoubleVectorGenome genome) {
        CPPN aCPPN = new FakeArrayCPPN(genome.genome, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }

    public int getNumOfLinks() {
        return substrateBuilder.getSubstrate().getNumOfLinks();
    }
}
