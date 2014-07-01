package hyper.experiments.ale;

import com.google.common.collect.ImmutableList;
import common.evolution.EvaluationInfo;
import common.evolution.IDistanceWithPrepare;

/**
 * Created by drchajan on 16/06/14.
 */
public class ALEBehavioralDistanceFactory {
    public static IDistanceWithPrepare<Object, EvaluationInfo> createDistance(ALEExperiment ale, String name) {
        switch (name) {
            case "OUTPUT":
                return new DistanceOutput(ale);

            default:
                throw new IllegalStateException("Unknown behavioral distance: " + name);

        }
    }

    protected static abstract class AbstractDistance implements IDistanceWithPrepare<Object, EvaluationInfo> {
        protected final ALEExperiment ale;

        protected AbstractDistance(ALEExperiment ale) {
            this.ale = ale;
        }
    }

    private static class DistanceOutput extends AbstractDistance {

        protected DistanceOutput(ALEExperiment ale) {
            super(ale);
        }

        @Override
        public Object prepare(EvaluationInfo evaluationInfo) {
            ImmutableList<Double> o = (ImmutableList<Double>) evaluationInfo.getInfo("OUTPUTS");
            return o;
        }

        @Override
        public double distance(Object a, Object b) {
            ImmutableList<Double> o1 = (ImmutableList<Double>) a;
            ImmutableList<Double> o2 = (ImmutableList<Double>) b;

            int s = Math.min(o1.size(), o2.size());
            double sumO = 0.0;
            for (int i = 0; i < s; i++) {
                sumO += Math.abs(o1.get(i) - o2.get(i));
            }
            sumO /= s;

            return sumO;
        }
    }
}
