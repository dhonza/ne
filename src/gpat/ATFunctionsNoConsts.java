package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 4:46:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFunctionsNoConsts {
    private ATFunctionsNoConsts() {
    }

    public static class Plus extends ATNodeImpl {

        public String getName() {
            return "plus";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return sums(node, treeInputs);
        }
    }

    public static class Times extends ATNodeImpl {
        public String getName() {
            return "times";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            if (node.getArity() == 0) {
                return 0.0;
            }
            /*
            double result = node.getChild(0).evaluate(treeInputs);
            for (int i = 1; i < node.getArity(); i++) {
                result *= node.getChild(i).evaluate(treeInputs);
            }
            return result;
            */
//            /*
            ATNode child = (ATNode) (node.getChild(0));
            double result;
            if (child.getImpl() instanceof ATTerminals.ConstantMarker) {
                result = node.getConstant(0) * node.getChild(0).evaluate(treeInputs);
            } else {
                result = node.getChild(0).evaluate(treeInputs);
            }
            for (int i = 1; i < node.getArity(); i++) {
                child = (ATNode) (node.getChild(i));
                if (child.getImpl() instanceof ATTerminals.ConstantMarker) {
                    result *= node.getConstant(i) * node.getChild(i).evaluate(treeInputs);
                } else {
                    result *= node.getChild(i).evaluate(treeInputs);
                }
            }
            return result;
//            */
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
            return Math.atan(sums(node, treeInputs));
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
            return Math.sin(sums(node, treeInputs));
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
            double result = sums(node, treeInputs);
            return Math.exp(-(result * result));
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }
    }

    private static double sums(IATNode node, TreeInputs treeInputs) {
        double result = 0.0;
        for (int i = 0; i < node.getArity(); i++) {
            ATNode child = (ATNode) (node.getChild(i));
            if (child.getImpl() instanceof ATTerminals.ConstantMarker) {
                result += node.getConstant(i) * node.getChild(i).evaluate(treeInputs);
            } else {
                result += node.getChild(i).evaluate(treeInputs);
            }
        }
        return result;
    }
}