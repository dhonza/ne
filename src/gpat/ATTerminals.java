package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 6:00:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATTerminals {
    private ATTerminals() {
    }

    public static class Input implements IATNodeImpl {
        final private int idx;

        public Input(int idx) {
            this.idx = idx;
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return treeInputs.get(idx);
        }

        public String getName() {
            return "x" + idx;
        }
    }

    public static class Constant implements IATNodeImpl {
        final private double value;

        public Constant(double value) {
            this.value = value;
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return value;
        }

        public String getName() {
            if (value == Math.round(value)) {
                return Integer.toString((int) value);
            }
            return Double.toString(value);
        }
    }
}