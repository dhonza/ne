package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 29, 2010
 * Time: 7:25:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class DistanceUtils {
    public static double average(IDistanceStorage distanceStorage) {
        int N = distanceStorage.getPopulationSize();
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N - i - 1; j++) {
                sum += distanceStorage.distance(i, j);
                count++;
            }
        }
        return sum / count;
    }
}
