package mogp;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import gp.Forest;

/**
 * Created by drchajan on 02/06/14.
 */
public class Solution {
    private final Forest x;
    private Optional<Objectives> y = Optional.absent();
    private Optional<Integer> rank = Optional.absent();
    private Optional<Double> crowdingDistance = Optional.absent();

    public Solution(Forest x) {
        this.x = x;
    }

    public boolean betterByCrowdedComparisonThan(Solution other) {
        return this.getRank() < other.getRank() ||
                (this.getRank() == other.getRank() && this.getCrowdingDistance() > other.getCrowdingDistance());
    }

    public Forest getX() {
        return x;
    }

    public int getRank() {
        return rank.get();
    }

    public void setRank(int rank) {
        this.rank = Optional.of(rank);
    }

    public double getCrowdingDistance() {
        return crowdingDistance.get();
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = Optional.of(crowdingDistance);
    }

    public Objectives getObjectives() {
        return y.get();
    }

    public void setObjectives(Objectives y) {
        this.y = Optional.of(y);
    }

    public static Ordering<Solution> ordering(final int m) {
        return new Ordering<Solution>() {
            @Override
            public int compare(Solution s1, Solution s2) {
                return Objectives.ordering(m).compare(s1.getObjectives(), s2.getObjectives());
            }
        };
    }

    public static Ordering<Solution> orderingCrowdingDistance() {
        return new Ordering<Solution>() {
            @Override
            public int compare(Solution s1, Solution s2) {
                return Double.compare(s2.getCrowdingDistance(), s1.getCrowdingDistance());
            }
        };
    }

    public boolean dominates(Solution other) {
        return this.y.get().dominates(other.y.get());
    }

    @Override
    public String toString() {
        return "Solution{" +
                "x=" + x +
                ", y=" + y +
                ", rank=" + rank +
                '}';
    }
}
