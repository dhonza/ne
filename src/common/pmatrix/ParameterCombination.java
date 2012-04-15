package common.pmatrix;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 11:27:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterCombination implements Iterable<String>, Serializable {
    final private LinkedHashMap<String, Object> combination;
    final private LinkedHashMap<String, Object> combinationNotChanging;
    final private LinkedHashMap<String, Object> combinationOnlyChanging;

    class ParameterCombinationNameIterator implements Iterator<String> {
        Iterator<String> it = combination.keySet().iterator();

        public boolean hasNext() {
            return it.hasNext();
        }

        public String next() {
            return it.next();
        }

        public void remove() {
            throw new UnsupportedOperationException("Method void remove() not implemented.");
        }
    }

    ParameterCombination(LinkedHashMap<String, Object> combination, HashSet<String> changes) {
        this.combination = combination;
        this.combinationNotChanging = new LinkedHashMap<String, Object>();
        this.combinationOnlyChanging = new LinkedHashMap<String, Object>();
        for (String name : combination.keySet()) {
            if (changes.contains(name)) {
                combinationOnlyChanging.put(name, combination.get(name));
            } else {
                combinationNotChanging.put(name, combination.get(name));
            }
        }
    }

    public Iterator<String> iterator() {
        return new ParameterCombinationNameIterator();
    }

    public double getDouble(String name) {
        return (Double) get(name, Double.class);
    }

    public double getDouble(String name, double def) {
        if (contains(name)) {
            return (Double) get(name, Double.class);
        } else {
            return def;
        }
    }

    public int getInteger(String name) {
        return (Integer) get(name, Integer.class);
    }

    public int getInteger(String name, int def) {
        if (contains(name)) {
            return (Integer) get(name, Integer.class);
        } else {
            return def;
        }
    }

    public boolean getBoolean(String name) {
        return (Boolean) get(name, Boolean.class);
    }

    public boolean getBoolean(String name, boolean def) {
        if (contains(name)) {
            return (Boolean) get(name, Boolean.class);
        } else {
            return def;
        }
    }

    public String getString(String name) {
        return (String) get(name, String.class);
    }

    public String getString(String name, String def) {
        if (contains(def)) {
            return (String) get(name, String.class);
        } else {
            return def;
        }
    }

    public String getAsString(String name) {
        return combination.get(name).toString();
    }

    public boolean contains(String name) {
        return combination.get(name) != null;
    }

    private Object get(String name, Class cls) {
        Object o = combination.get(name);
        if (o == null) {
            throw new IllegalArgumentException("Parameter not defined: " + name);
        }
        if (o.getClass() != cls) {
            throw new IllegalArgumentException("Parameter " + name + " has different type: " + o.getClass());
        }
        return o;
    }

    @Override
    public String toString() {
        return combination.toString();
    }

    public String toStringAllSeparatedNewLines() {
        StringBuilder builder = new StringBuilder();
        builder.append("# FIXED:\n");
        builder.append(toStringNotChanngingNewLines());
        builder.append("# CHANGING:\n");
        builder.append(toStringOnlyChanngingNewLines());
        return builder.toString();

    }

    public String toStringNotChannging() {
        return combinationNotChanging.toString();
    }

    public String toStringOnlyChannging() {
        return combinationOnlyChanging.toString();
    }

    public String toStringNotChanngingNewLines() {
        StringBuilder builder = new StringBuilder();
        for (String paramName : combinationNotChanging.keySet()) {
            builder.append(paramName).append(" = ").append(paramValueAsString(paramName)).append('\n');
        }
        return builder.toString();
    }

    public String toStringOnlyChanngingNewLines() {
        StringBuilder builder = new StringBuilder();
        for (String paramName : combinationOnlyChanging.keySet()) {
            builder.append(paramName).append(" = ").append(paramValueAsString(paramName)).append('\n');
        }
        return builder.toString();
    }


    private String paramValueAsString(String paramName) {
        Object value = combination.get(paramName);
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + value.toString() + "\"";
        }
    }
}
