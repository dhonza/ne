package common.evolution;

import com.google.common.collect.ImmutableList;

/**
 * Created by drchajan on 24/06/14.
 */
public class BehavioralDiversityUtils {
    public static ImmutableList<Double> behavioralDiversity(IDistanceWithPrepare<Object, EvaluationInfo> behavioralDistance, ImmutableList<EvaluationInfo> evaluationInfos) {
        ImmutableList.Builder<Double> builder = ImmutableList.builder();
        Double[][] dists = new Double[evaluationInfos.size()][evaluationInfos.size()];

        for (int i = 0; i < evaluationInfos.size(); i++) {
            EvaluationInfo e1 = evaluationInfos.get(i);
            Object a = behavioralDistance.prepare(e1);

            double sum = 0.0;
            for (int j = 0; j < evaluationInfos.size(); j++) {
                EvaluationInfo e2 = evaluationInfos.get(j);
                if (e1 != e2) {
                    Object b = behavioralDistance.prepare(e2);
                    double d;
                    if (i <= j) {
                        d = behavioralDistance.distance(a, b);
                        dists[i][j] = d;
                    } else {
                        d = dists[j][i];
                    }
                    sum += d;
                }
            }
            builder.add(sum / evaluationInfos.size());
        }
        return builder.build();
    }
}
