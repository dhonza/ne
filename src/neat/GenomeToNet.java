package neat;

import common.evolution.GenotypeToPhenotype;
import common.net.linked.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 11:02:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenomeToNet implements GenotypeToPhenotype<Genome, Net> {
    public Net convert(Genome genome) {
        return genome.getNet();
    }
}
