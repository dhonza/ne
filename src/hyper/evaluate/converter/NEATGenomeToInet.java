package hyper.evaluate.converter;

import common.evolution.GenotypeToPhenotype;
import common.net.INet;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.cppn.BasicNetCPPN;
import hyper.cppn.CPPN;
import neat.Genome;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 12:38:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEATGenomeToInet implements GenotypeToPhenotype<Genome, INet> {
    final private EvaluableSubstrateBuilder substrateBuilder;

    public NEATGenomeToInet(EvaluableSubstrateBuilder substrateBuilder) {
        this.substrateBuilder = substrateBuilder;
    }

    public INet convert(Genome genome) {
        CPPN aCPPN = new BasicNetCPPN(genome.getNet(), substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        return substrateBuilder.getNet();
    }
}
