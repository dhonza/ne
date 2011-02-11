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

    public static class Plus extends ATNode {
        public Plus(int id, int depth) {
            super(id, depth);
        }

        public Plus() {
            super();
        }

        @Override
        public String getName() {
            return "plus";
        }

        public ATNode create(int id, int depth) {
            return new Plus(id, depth);
        }

        @Override
        public double evaluate(TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return result;
        }
    }

    public static class Times extends ATNodeIgnoreConstants {
        public Times(int id, int depth) {
            super(id, depth);
        }

        public Times() {
            super();
        }

        @Override
        public String getName() {
            return "times";
        }

        public ATNode create(int id, int depth) {
            return new Times(id, depth);
        }

        @Override
        public double evaluate(TreeInputs treeInputs) {
            if(children.size() == 0) {
                return 0.0;
            }
            double result = getChild(0).evaluate(treeInputs);
            for (int i = 1; i < children.size(); i++) {
                result *= getChild(i).evaluate(treeInputs);
            }
            return result;
        }
    }

    public static class ATan extends ATNode {
        public ATan(int id, int depth) {
            super(id, depth);
        }

        public ATan() {
            super();
        }

        @Override
        public String getName() {
            return "atan";
        }

        public ATNode create(int id, int depth) {
            return new ATan(id, depth);
        }

        @Override
        public double evaluate(TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.atan(result);
        }
    }

    public static class Sin extends ATNode {
        public Sin(int id, int depth) {
            super(id, depth);
        }

        public Sin() {
            super();
        }

        @Override
        public String getName() {
            return "sin";
        }

        public ATNode create(int id, int depth) {
            return new Sin(id, depth);
        }

        @Override
        public double evaluate(TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.sin(result);
        }
    }

    public static class Gauss extends ATNode {
        public Gauss(int id, int depth) {
            super(id, depth);
        }

        public Gauss() {
            super();
        }

        @Override
        public String getName() {
            return "gauss";
        }

        public ATNode create(int id, int depth) {
            return new Gauss(id, depth);
        }

        @Override
        public double evaluate(TreeInputs treeInputs) {
            double result = 0.0;
            for (int i = 0; i < children.size(); i++) {
                result += constants.get(i) * getChild(i).evaluate(treeInputs);
            }
            return Math.exp(-(result * result));
        }
    }
}