package common.evolution;

import common.mathematica.MathematicaUtils;
import divvis.DistanceProjection;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 11:25:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDistanceStorage<D> implements IDistanceStorage {
    final private List<D> individuals;
    final private List<D> prevIndividuals;
    final private IDistance<D> distance;

    private double[][] distanceMatrix;
    final private int N;

    public SimpleDistanceStorage(List<D> individuals, List<D> prevIndividuals, IDistance<D> distance) {
        this.individuals = individuals;
        this.prevIndividuals = prevIndividuals;
        this.distance = distance;
        this.N = this.individuals.size();
        distanceMatrix = new double[N - 1][];
        for (int i = 0; i < distanceMatrix.length; i++) {
            distanceMatrix[i] = new double[N - i - 1];
        }
    }

    public void recompute() {
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                distanceMatrix[i][j] = distance.distance(
                        individuals.get(i), individuals.get(j + i + 1));
            }
        }
    }

    public double distance(int idxA, int idxB) {
        int a = (idxA <= idxB ? idxA : idxB);
        int b = (idxA <= idxB ? idxB : idxA);
        if (a == b) {
            return 0.0;
        }
        return distanceMatrix[a][b - a - 1];
    }

    public double distanceToPrev(int idxCur, int idxPrev) {
        return distance.distance(individuals.get(idxCur), prevIndividuals.get(idxPrev));
    }

    public int getPopulationSize() {
        return N;
    }

    public double[][] makeCompleteDistanceMatrix() {
        double[][] d = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                d[i][j] = distance(i, j);
            }
        }
        return d;
    }

    public DistanceProjection project() {
        DistanceProjection prj = new DistanceProjection(makeCompleteDistanceMatrix());
        prj.project();
        return prj;
    }

    @Override
    public String toString() {
        return MathematicaUtils.matrixToMathematica(makeCompleteDistanceMatrix());
    }
}
