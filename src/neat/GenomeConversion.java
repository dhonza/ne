package neat;

import common.evolution.IGenotypeToPhenotype;
import common.net.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 11:49:04 AM
 * To change this template use File | Settings | File Templates.
 * <p/>
 * For algorithms which evolve phenotypes directly.
 */
public class GenomeConversion implements IGenotypeToPhenotype<Genome, INet> {
    public INet transform(Genome genome) {
        return genome.getNet();
    }
}
