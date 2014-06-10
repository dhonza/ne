package mogp;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import common.mathematica.MathematicaUtils;

/**
 * Created by drchajan on 02/06/14.
 */
public class Objectives {
    private final ImmutableList<Double> y;
    private static ImmutableList<Ordering<Objectives>> orderings = null;

    public Objectives(ImmutableList<Double> y) {
        this.y = y;
        if (orderings == null) {
            ImmutableList.Builder<Ordering<Objectives>> builder = ImmutableList.builder();
            for (int m = 0; m < y.size(); m++) {
                builder.add(ordering(m));
            }
            orderings = builder.build();
        }
    }

    public double get(int m) {
        return y.get(m);
    }

    public boolean dominates(Objectives other) {
        boolean strictlyBetter = false;
        for (int m = 0; m < this.y.size(); m++) {
            Ordering<Objectives> ordering = Objectives.ordering(m);
            if (ordering.compare(this, other) == -1) {
                return false;
            }
            if (ordering.compare(this, other) == 1) {
                strictlyBetter = true;
            }
        }
        return strictlyBetter;
    }

    /**
     * @param m objective to compare
     * @return
     */
    public static Ordering<Objectives> ordering(final int m) {
        return orderings != null ? orderings.get(m) :
                Ordering.natural().onResultOf(new Function<Objectives, Comparable>() {
                    @Override
                    public Comparable apply(Objectives objectives) {
                        return objectives.get(m);
                    }
                });
    }

    @Override
    public String toString() {
        return "Objectives{" +
                "y=" + y +
                '}';
    }

    public String toMathematica() {
        return MathematicaUtils.arrayToMathematica(Doubles.toArray(y));
    }
}
