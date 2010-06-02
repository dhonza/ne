package common.stats;

import java.util.*;

/**
 * User: honza
 * Date: Aug 10, 2006
 * Time: 1:24:20 PM
 */
public class Stats {

    private LinkedHashMap<String, Stat> stats;

    public Stats() {
        stats = new LinkedHashMap<String, Stat>();
    }

    public void createDoubleStat(String statName, String scope, String description) {
        Stat stat = new Stat(statName, scope, description, new DoubleStatSeries());
        stats.put(statName, stat);
    }

    public void createDoubleStatIfNotExists(String statName, String scope, String description) {
        if (!stats.containsKey(statName)) {
            createDoubleStat(statName, scope, description);
        }
    }

    public void createBooleanStat(String statName, String scope, String description) {
        Stat stat = new Stat(statName, scope, description, new BooleanStatSeries());
        stats.put(statName, stat);
    }

    public void createBooleanStatIfNotExists(String statName, String scope, String description) {
        if (!stats.containsKey(statName)) {
            createBooleanStat(statName, scope, description);
        }
    }

    private BooleanStatSeries getBooleanStatSeries(Stat stat) {
        if (stat.getStatSeries() instanceof BooleanStatSeries) {
            return (BooleanStatSeries) stat.getStatSeries();
        }
        throw new IllegalStateException("Not a boolean stat series: " + stat.getName());
    }

    private DoubleStatSeries getDoubleStatSeries(Stat stat) {
        if (stat.getStatSeries() instanceof DoubleStatSeries) {
            return (DoubleStatSeries) stat.getStatSeries();
        }
        throw new IllegalStateException("Not a boolean stat series: " + stat.getName());
    }

    public void resetScope(String scope) {
        for (Stat stat : stats.values()) {
            if (stat.getScope().equals(scope)) {
                stat.reset();
            }
        }
    }

    public void resetStat(String statName) {
        Stat stat = stats.get(statName);
        if (stat != null) {
            stat.reset();
        }
    }

    public void addSample(String statName, double sample) {
        Stat stat = stats.get(statName);
        if (stat != null && stat.isEnabled()) {
            getDoubleStatSeries(stat).addSample(sample);
        }
    }

    public void addSample(String statName, boolean sample) {
        Stat stat = stats.get(statName);
        if (stat != null && stat.isEnabled()) {
            getBooleanStatSeries(stat).addSample(sample);
        }
    }

    public void incrementOneToSample(String statName, int idx) {
        Stat stat = stats.get(statName);
        if (stat != null && stat.isEnabled()) {
            getDoubleStatSeries(stat).incrementOneToSample(idx);
        }
    }

    public String getDescription(String statName) {
        Stat stat = stats.get(statName);
        if (stat != null) {
            return stat.getDescription();
        }
        return "no description";
    }

    public StatSeries getStatSeries(String statName) {
        Stat stat = stats.get(statName);
        if (stat != null) {
            if (!stat.isEnabled()) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getStatSeries();
        }
        return null;
    }

    public int getStatsCount() {
        return stats.size();
    }

    public Set<String> getStatsKeys() {
        return stats.keySet();
    }

    public String scopeToString(String scope) {
        StringBuilder builder = new StringBuilder();
        for (Stat stat : getStatsInScope(scope)) {
            builder.append(stat).append("\n");
        }
        return builder.toString();
    }

    public String dataToString(String scope) {
        List<Stat> sstats = getStatsInScope(scope);
        if (sstats.size() == 0) {
            return "";
        }
        int len = sstats.get(0).getStatSeries().getSize();
        StringBuilder builder = new StringBuilder();

        List<Stat> sstatsWithoutLast = sstats.subList(0, sstats.size() - 1);
        Stat lastStat = sstats.get(sstats.size() - 1);

        //header
        for (Stat stat : sstatsWithoutLast) {
            builder.append(stat.getName()).append("\t");
        }
        builder.append(lastStat.getName()).append("\n");

        //data
        for (int i = 0; i < len; i++) {
            for (Stat stat : sstatsWithoutLast) {
                builder.append(stat.getStatSeries().getValue(i)).append("\t");
            }
            builder.append(lastStat.getStatSeries().getValue(i)).append("\n");
        }

        return builder.toString();
    }

    private List<Stat> getStatsInScope(String scope) {
        Collection<Stat> tstats = stats.values();
        List<Stat> sstats = new ArrayList<Stat>();
        for (Stat tstat : tstats) {
            if (tstat.getScope().equals(scope)) {
                sstats.add(tstat);
            }
        }
        return sstats;
    }
}