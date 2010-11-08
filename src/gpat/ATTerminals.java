package gpat;

import gp.TreeInputs;
import gp.terminals.ITerminal;

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

    public static class Input extends ATNode implements ITerminal {
        final private int idx;

        public Input(int id, int depth, int idx) {
            super(id, depth);
            this.idx = idx;
        }

        public Input(int idx) {
            super();
            this.idx = idx;
        }

        public ATNode create(int id, int depth) {
            return new Input(id, depth, idx);
        }

        public double evaluate(TreeInputs treeInputs) {
            return treeInputs.get(idx);
        }

        public String getName() {
            return "x" + idx;
        }
    }

    public static class Constant extends ATNode implements ITerminal {
        final private double value;

        public Constant(int id, int depth, double value) {
            super(id, depth);
            this.value = value;
        }

        public Constant(double value) {
            super();
            this.value = value;
        }

        public double evaluate(TreeInputs treeInputs) {
            return value;
        }

        public ATNode create(int id, int depth) {
            return new Constant(id, depth, value);
        }

        public String getName() {
            if (value == Math.round(value)) {
                return Integer.toString((int) value);
            }
            return Double.toString(value);
        }
    }
}