package gpaac;

import gp.INode;
import gp.InnovationCounter;
import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Oct 4, 2010
 * Time: 4:46:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Functions {
    public static class Times extends AbstractNode {

        public Times(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Times() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Times(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            return getChild(0).evaluate(treeInputs) * getChild(1).evaluate(treeInputs);
        }

        @Override
        public int getArity() {
            return 2;
        }

        public String getName() {
            return "times";
        }
    }

    public static class APlus extends AbstractArbitraryArityNode {

        public APlus(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public APlus() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new APlus(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            double value = 0;
            for (int i = 0; i < getArity(); i++) {
                value += getConstants(i) * getChild(i).evaluate(treeInputs);
            }
            return value;
        }

        public String getName() {
            return "plus";
        }
    }
}
