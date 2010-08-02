package common.evolution;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 11:25:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDistanceStorage<D> implements IDistanceStorage {
    final private IPopulationStorage<?, ?, D> populationStorage;
    final private IDistance<D> distance;

    private double[][] distanceMatrix;
    final private int N;

    public SimpleDistanceStorage(IPopulationStorage<?, ?, D> populationStorage, IDistance<D> distance) {
        this.populationStorage = populationStorage;
        this.distance = distance;
        this.N = this.populationStorage.getDistancePhenomes().size();
        distanceMatrix = new double[N - 1][];
        for (int i = 0; i < distanceMatrix.length; i++) {
            distanceMatrix[i] = new double[N - i - 1];
        }
    }

    public void recompute() {
        List<D> individuals = this.populationStorage.getDistancePhenomes();
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                distanceMatrix[i][j] = distance.distance(
                        individuals.get(i), individuals.get(j + i + 1));
            }
        }
    }

    public double distance(int idxA, int idxB) {
        int a = idxA <= idxB ? idxA : idxB;
        int b = idxA <= idxB ? idxB : idxA;
        if (a == b) {
            return 0.0;
        }
        return distanceMatrix[a][b - a - 1];
    }

    public int getPopulationSize() {
        return N;
    }
}