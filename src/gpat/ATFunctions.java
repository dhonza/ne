package gpat;

import common.RND;
import gp.GP;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 4:46:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFunctions {
    private ATFunctions() {
    }

    public static class Plus extends ATNodeImpl {

        public String getName() {
            return "plus";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return sum(node, treeInputs);
        }

        @Override
        public boolean hasConstants() {
            return true;
        }
    }

    public static class Times extends ATNodeImpl {
        public String getName() {
            return "times";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return times(node, treeInputs);
        }

        @Override
        public boolean hasConstants() {
            return false;
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }
    }

    public static class ATan extends ATNodeImpl {
        public String getName() {
            return "atan";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            if (node.getArity() == 0) {
                return 0.0;
            }
            return Math.atan(sum(node, treeInputs));
        }

        @Override
        public boolean hasConstants() {
            return false;
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }
    }

    public static class Sin extends ATNodeImpl {
        public String getName() {
            return "sin";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            if (node.getArity() == 0) {
                return 0.0;
            }
            return Math.sin(sum(node, treeInputs));
        }

        @Override
        public boolean hasConstants() {
            return false;
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }
    }

    public static class Gauss extends ATNodeImpl {
        public String getName() {
            return "gauss";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            if (node.getArity() == 0) {
                return 0.0;
            }
            double result = (sum(node, treeInputs));
            return Math.exp(-(result * result));
        }

        @Override
        public boolean hasConstants() {
            return false;
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }
    }

    public static class Random extends ATNodeImpl {
        final private double value;

        public Random() {
            this.value = RND.getDouble(-GP.CONSTANT_AMPLITUDE, GP.CONSTANT_AMPLITUDE);
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

        @Override
        public boolean hasConstants() {
            return false;
        }
    }
}