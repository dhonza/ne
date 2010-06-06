package common.stats;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 2, 2010
 * Time: 12:54:46 PM
 * To change this template use File | Settings | File Templates.
 */
class Stat implements Serializable {
    private String name;
    private String description;
    private boolean enabled;
    private String scope;
    private StatSeries data;

    public Stat(String name, boolean enabled, String scope, String description, StatSeries data) {
        this.name = name;
        this.enabled = enabled;
        this.scope = scope;
        this.description = description;
        this.data = data;
        reset();
    }

    public Stat(String name, String scope, String description, StatSeries data) {
        this(name, true, scope, description, data);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public StatSeries getStatSeries() {
        return data;
    }

    public void reset() {
        if (data != null) {
            data.clear();
        }
    }

    public String toString() {
        return name + data.getStatString();
    }
}
