package common.evolution;

import com.google.common.collect.ImmutableList;

/**
 * Created by drchajan on 16/06/14.
 */
public interface IBehavioralDiversity {
    ImmutableList<Double> behavioralDiversity(ImmutableList<EvaluationInfo> evaluationInfos);
}
