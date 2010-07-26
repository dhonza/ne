package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 22, 2010
 * Time: 11:00:16 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGenotypeToPhenotype<G, P> {
    P convert(G genome);
}
