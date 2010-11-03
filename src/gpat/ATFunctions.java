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
        protected Plus(int id, int depth) {
            super(id, depth);
        }

        protected Plus() {
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

    public static class Times extends ATNode {
        protected Times(int id, int depth) {
            super(id, depth);
        }

        protected Times() {
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
            double result = getChild(0).evaluate(treeInputs);
            for (int i = 1; i < children.size(); i++) {
                result *= getChild(i).evaluate(treeInputs);
            }
            return result;
        }
    }
}