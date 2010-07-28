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
public class IdentityConversion<P> implements IGenotypeToPhenotype<P, P> {
    public P transform(P genome) {
        return genome;
    }
}
