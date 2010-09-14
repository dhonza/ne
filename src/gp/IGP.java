package gp;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 8:55:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IGP<T extends IGPForest> {
    int getGeneration();

    int getEvaluations();

    T getBestSoFar();

    T getBestOfGeneration();

    int getLastInnovation();
}
