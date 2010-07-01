package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 1, 2010
 * Time: 4:44:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluationInfo {
    final private double fitness;


    public EvaluationInfo(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }
}
