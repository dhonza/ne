package common.evolution;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 1, 2010
 * Time: 4:44:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluationInfo extends BasicInfo {
    final private double fitness;

    public EvaluationInfo(double fitness) {
        this.fitness = fitness;
    }

    private EvaluationInfo() {
        fitness = 0.0;
    }

    public EvaluationInfo(double fitness, Map<String, Object> infoMap) {
        super(infoMap);
        this.fitness = fitness;
    }

    public static EvaluationInfo mixTwo(EvaluationInfo first, EvaluationInfo second) {
        EvaluationInfo t = new EvaluationInfo(second.getFitness(), first.infoMap);
        t.infoMap.putAll(second.infoMap);
        return t;
    }

    public double getFitness() {
        return fitness;
    }
}
