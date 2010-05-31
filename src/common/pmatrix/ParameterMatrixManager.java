package common.pmatrix;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 8:59:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterMatrixManager implements Iterable<ParameterCombination> {
    final private List<ParameterCombination> combinations;

    class ParameterManagerIterator implements Iterator<ParameterCombination> {
        private int idx = 0;

        public boolean hasNext() {
            return idx < combinations.size();
        }

        public ParameterCombination next() {
            return combinations.get(idx++);
        }

        public void remove() {
            throw new UnsupportedOperationException("Method void remove() not implemented.");
        }
    }

    ParameterMatrixManager(List<ParameterCombination> combinations) {
        this.combinations = combinations;
    }

    public Iterator<ParameterCombination> iterator() {
        return new ParameterManagerIterator();
    }

    @Override
    public String toString() {
        if (combinations.size() == 0) {
            return "NO PARAMETERS";
        }
        return combinations.get(0).toStringNotChannging();
    }

    public String toStringNewLines() {
        if (combinations.size() == 0) {
            return "NO PARAMETERS";
        }

        StringBuilder builder = new StringBuilder();
        for (String paramName : combinations.get(0)) {
            builder.append(paramName).append(" = ").append(combinations.get(0).getAsString(paramName)).append('\n');
        }
        return builder.toString();
    }
}
