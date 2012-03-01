package common.evolution;

import divvis.DistanceProjection;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 12:19:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IDistanceStorage {
    void recompute();

    double distance(int idxA, int idxB);

    double distanceToPrev(int idxCur, int idxPrev);

    int getPopulationSize();

    public DistanceProjection project();
}
