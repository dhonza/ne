package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 21.05.12
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
public interface IDistanceByOutput<T> extends IDistance<T> {
    double distance(T a, T b, int output);
}
