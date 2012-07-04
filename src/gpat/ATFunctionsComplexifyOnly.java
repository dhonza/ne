package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 4:46:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFunctionsComplexifyOnly {
    private ATFunctionsComplexifyOnly() {
    }

    public static class Plus extends ATNodeImpl {

        public String getName() {
            return "plus";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return sum(node, treeInputs);
        }

        @Override
        boolean isActive(ATNode node, int idx) {
            return node.getChild(idx).getImpl() instanceof ATTerminals.ConstantMarker;
        }

        @Override
        public int maxArity() {
            return 2;
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
            double result;
            if (node.isActive(0)) {
                result = node.getConstant(0) * node.getChild(0).evaluate(treeInputs);
            } else {
                result = node.getChild(0).evaluate(treeInputs);
            }
            for (int i = 1; i < node.getArity(); i++) {
                if (node.isActive(i)) {
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

        @Override
        boolean isActive(ATNode node, int idx) {
            return node.getChild(idx).getImpl() instanceof ATTerminals.ConstantMarker;
        }

        @Override
        public int maxArity() {
            return 2;
        }
    }

    public static class ATan extends ATNodeImpl {
        public String getName() {
            return "atan";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return Math.atan(sum(node, treeInputs));
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }

        @Override
        boolean isActive(ATNode node, int idx) {
            return node.getChild(idx).getImpl() instanceof ATTerminals.ConstantMarker;
        }

        @Override
        public int maxArity() {
            return 1;
        }
    }

    public static class Sin extends ATNodeImpl {
        public String getName() {
            return "sin";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            return Math.sin(sum(node, treeInputs));
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }

        @Override
        boolean isActive(ATNode node, int idx) {
            return node.getChild(idx).getImpl() instanceof ATTerminals.ConstantMarker;
        }

        @Override
        public int maxArity() {
            return 1;
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
            double result = sum(node, treeInputs);
            return Math.exp(-(result * result));
        }

        @Override
        public int repeatInput() {
            return Integer.MAX_VALUE;
//            return 1;
        }

        @Override
        boolean isActive(ATNode node, int idx) {
            return node.getChild(idx).getImpl() instanceof ATTerminals.ConstantMarker;
        }

        @Override
        public int maxArity() {
            return 1;
        }
    }
}