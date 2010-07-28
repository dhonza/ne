package hyper.evaluate.converter;

import common.evolution.IGenotypeToPhenotype;
import common.net.INet;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.ICPPN;
import neat.Genome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:38:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATGenomeToInet implements IGenotypeToPhenotype<Genome, INet> {
    final private IEvaluableSubstrateBuilder substrateBuilder;

    public NEATGenomeToInet(IEvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet transform(Genome genome) {
        ICPPN aCPPN = new BasicNetCPPN(genome.getNet(), substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
