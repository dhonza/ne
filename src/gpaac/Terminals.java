package gpaac;

import common.RND;
import gp.GP;
import gp.INode;
import gp.InnovationCounter;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 6:00:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Terminals {
    public static class Input extends AbstractNode {
        final private int idx;

        public Input(int depth, long innovation, int idx) {
            super(depth, innovation, new INode[]{});
            this.idx = idx;
        }

        public Input(int idx) {
            super();
            this.idx = idx;
        }

        public INode create(int depth, INode[] children) {
            return new Input(depth, InnovationCounter.getInstance().getNext(), idx);
        }

        public double evaluate(TreeInputs treeInputs) {
            return treeInputs.get(idx);
        }

        public String getName() {
            return "x" + idx;
        }
    }

    public static class Constant extends AbstractNode {
        final private double value;

        public Constant(int depth, long innovation, double value) {
            super(depth, innovation, new INode[]{});
            this.value = value;
        }

        public Constant(double value) {
            super();
            this.value = value;
        }

        public double evaluate(TreeInputs treeInputs) {
            return value;
        }

        public INode create(int depth, INode[] children) {
            return new Constant(depth, InnovationCounter.getInstance().getNext(), value);
        }

        public String getName() {
            if (value == Math.round(value)) {
                return Integer.toString((int) value);
            }
            return Double.toString(value);
        }
    }

    public static class Random extends AbstractNode {
        private double value;

        public Random(int depth, long innovation) {
            super(depth, innovation, new INode[]{});
            this.value = RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE);
        }

        public Random() {
            super();
            this.value = 1.0;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double evaluate(TreeInputs treeInputs) {
            return value;
        }

        public INode create(int depth, INode[] children) {
            return new Random(depth, InnovationCounter.getInstance().getNext());
        }

        public String getName() {
            if (value == Math.round(value)) {
                return Integer.toString((int) value);
//                return "C" + Integer.toString((int) value);
            }
//            return "C" + Double.toString(value);
            return Double.toString(value);
        }
    }
}
