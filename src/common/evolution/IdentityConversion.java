package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 11:49:04 AM
 * To change this template use File | Settings | File Templates.
 *
 * For algorithms which evolve phenotypes directly.
 */
public class IdentityConversion<P> implements GenotypeToPhenotype<P, P>{
    public P convert(P genome) {
        return genome;
    }
}
