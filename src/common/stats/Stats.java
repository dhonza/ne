package common.stats;

import java.util.*;

/**
 * User: honza
 * Date: Aug 10, 2006
 * Time: 1:24:20 PM
 */
public class Stats {

    public class Stat {
        private String name;
        private String description;
        private boolean enabled;
        private String scope;
        private StatSeries data;

        public Stat(String name, boolean enabled, String scope, String description) {
            this.name = name;
            this.enabled = enabled;
            this.scope = scope;
            this.description = description;
            data = new StatSeries();
            reset();
        }

        public Stat(String name, String scope, String description) {
            this(name, true, scope, description);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public StatSeries getData() {
            return data;
        }

        public void reset() {
            if (data != null) {
                data.clear();
            }
        }

        public String toString() {
            return name + " Mean:" + data.getMean() + " Std.Dev.(SQRT):" + data.getMeanDeviationSqrt() + " Std.Dev.:" + data.getMeanDeviation() + " Median:" + data.getMedian();
        }
    }

    private LinkedHashMap<String, Stat> stats;

    public Stats() {
        stats = new LinkedHashMap<String, Stat>();
    }

    public Stat getStat(String ostatName) {
        return stats.get(ostatName);
    }

    public void createStat(String ostatName, String oscope, String odescription) {
        Stat stat = new Stat(ostatName, oscope, odescription);
        stats.put(ostatName, stat);

    }

    public void resetScope(String oscope) {
        for (Stat stat : stats.values()) {
            if (stat.getScope().equals(oscope)) {
                stat.reset();
            }
        }
    }

    public void resetStat(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            stat.reset();
        }
    }

    public void addSample(String ostatName, double osample) {
        Stat stat = stats.get(ostatName);
        if (stat != null && stat.enabled) {
            stat.getData().addSample(osample);
        }
    }

    public void incrementOneToSample(String ostatName, int oidx) {
        Stat stat = stats.get(ostatName);
        if (stat != null && stat.enabled) {
            stat.getData().incrementOneToSample(oidx);
        }
    }

    public String getDescription(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            return stat.getDescription();
        }
        return "no description";
    }

    public double getSample(String ostatName, int oidx) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {//TODO maybe change this behaviour
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData().getSample(oidx);
        }
        return Double.NaN;
    }

    public double getMean(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData().getMean();
        }
        return Double.NaN;
    }

    public double getMeanDeviation(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData().getMeanDeviation();
        }
        return Double.NaN;
    }

    public double getMeanDeviationSqrt(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData().getMeanDeviationSqrt();
        }
        return Double.NaN;
    }

    public double getMedian(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData().getMedian();
        }
        return Double.NaN;
    }

    public StatSeries getStatSeries(String ostatName) {
        Stat stat = stats.get(ostatName);
        if (stat != null) {
            if (!stat.enabled) {
                throw new IllegalStateException("Stat not enabled.");
            }
            return stat.getData();
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
        int len = sstats.get(0).getData().getDataArray().length;
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
                builder.append(stat.getData().getDataArray()[i]).append("\t");
            }
            builder.append(lastStat.getData().getDataArray()[i]).append("\n");
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