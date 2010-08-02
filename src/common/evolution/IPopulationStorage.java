package common.evolution;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 27, 2010
 * Time: 10:30:19 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IPopulationStorage<G, P, D> {
    G getGenome(int idx);
    List<G> getGenomes();
    
    P getPhenome(int idx);
    List<P> getPhenomes();

    /**
     * Simplified version of phenotype, used to compute distances.
     * @param idx index of individual in the population
     * @return distance phenome
     */
    D getDistancePhenome(int idx);
    List<D> getDistancePhenomes();

    void loadGenomes(List<G> genomes);
    void convert();
}
