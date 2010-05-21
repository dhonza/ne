package common.pmatrix;


import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 7:40:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterMatrixBuilder {
    final private Map<String, ArrayList<Parameter>> parameterMap;
    private List<String> params;
    private int[] indices;
    private int[] lengths;
    //contains parameters which changes, i.e. which have more than one values set
    private HashSet<String> changes;

    public ParameterMatrixBuilder() {
        this.parameterMap = new HashMap<String, ArrayList<Parameter>>();
    }

    public void add(Parameter parameter) {
        //TODO check duplicates
        ArrayList<Parameter> actualList = parameterMap.get(parameter.getName());
        if (actualList == null) {
            actualList = new ArrayList<Parameter>();
            parameterMap.put(parameter.getName(), actualList);
        } else if (getListClass(parameter.getName()) != parameter.getValue().getClass()) {
            throw new IllegalStateException("Cannot combine types of parameter values.");
        }
        actualList.add(parameter);
    }

    public void addRange(String name, double from, double to, double step) {
        int steps = (int) ((to - from) / step);
        for (int i = 0; i <= steps; i++) {
            add(new Parameter<Double>(name, from + i * step));
        }
//        for (double d = from; d <= to; d += step) {
//            add(new Parameter<Double>(name, d));
//        }
    }

    public void addRange(String name, int from, int to, int step) {
        for (int d = from; d <= to; d += step) {
            add(new Parameter<Integer>(name, d));
        }
    }

    private Class getListClass(String name) {
        Class cls = parameterMap.get(name).get(0).getValue().getClass();
        if (cls == null) {
            throw new IllegalStateException("Parameter: " + name + "not registered.");
        }
        return cls;
    }

    public ParameterMatrixManager buildManager() {
        params = new ArrayList<String>(parameterMap.keySet());
        Collections.sort(params);
        indices = new int[params.size()];
        lengths = new int[params.size()];

        changes = new HashSet<String>();
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = parameterMap.get(params.get(i)).size();
            if (lengths[i] > 1) {
                changes.add(params.get(i));
            }
        }

        List<ParameterCombination> combinations = new ArrayList<ParameterCombination>();
        storeCombination(0, combinations);

        return new ParameterMatrixManager(combinations);
    }

    private void storeCombination(int idx, List<ParameterCombination> combinations) {
        if (lengths.length == 0) {
            return;
        }
        for (int i = 0; i < lengths[idx]; i++) {
            if (idx < indices.length - 1) {
                storeCombination(idx + 1, combinations);
            } else {
                LinkedHashMap<String, Object> combination = new LinkedHashMap<String, Object>();
                for (int j = 0; j < indices.length; j++) {
                    int index = indices[j];
                    combination.put(params.get(j), parameterMap.get(params.get(j)).get(index).getValue());
                }
                combinations.add(new ParameterCombination(combination, changes));
            }
            indices[idx]++;
        }
        indices[idx] = 0;
    }

}
