package gpaac;

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
    public static class Multiply extends AbstractNode {

        public Multiply(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Multiply() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Multiply(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            return getChildren(0).evaluate(treeInputs) * getChildren(1).evaluate(treeInputs);
        }

        @Override
        public int getArity() {
            return 2;
        }

        public String getName() {
            return "Times";
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
                value += getConstants(i) * getChildren(i).evaluate(treeInputs);
            }
            return value;
        }

        public String getName() {
            return "Plus";
        }
    }
}
