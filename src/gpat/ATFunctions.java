package gpat;

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
            double result = 0.0;
            for (int i = 0; i < node.getArity(); i++) {
                result += node.getConstant(i) * node.getChild(i).evaluate(treeInputs);
            }
            return result;
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
            double result = node.getChild(0).evaluate(treeInputs);
            for (int i = 1; i < node.getArity(); i++) {
                result *= node.getChild(i).evaluate(treeInputs);
            }
            return result;
        }

        @Override
        public boolean hasConstants() {
            return false;
        }
    }

/*
    public static class ATan implements IATNodeImpl {
        public String getName() {
            return "atan";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.atan(result);
        }
    }

    public static class Sin implements IATNodeImpl {
        public String getName() {
            return "sin";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.sin(result);
        }
    }

    public static class Gauss implements IATNodeImpl {
        public String getName() {
            return "gauss";
        }

        public double evaluate(IATNode node, TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.exp(-(result * result));
        }
    }
*/
}