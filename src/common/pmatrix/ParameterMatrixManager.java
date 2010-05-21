package common.pmatrix;

import java.util.*;

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
            return combinations.get(idx++);  //To change body of implemented methods use File | Settings | File Templates.
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
        if(combinations.size() == 0) {
            return "NO PARAMETERS";
        }
        return combinations.get(0).toStringNotChannging();
    }
}
