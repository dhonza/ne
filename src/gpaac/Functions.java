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
    public static class Atan extends AbstractNode {

        public Atan(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Atan() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Atan(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            return Math.atan(getChild(0).evaluate(treeInputs));
        }

        @Override
        public int getArity() {
            return 1;
        }

        public String getName() {
            return "atan";
        }
    }

    public static class Gauss extends AbstractNode {

        public Gauss(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Gauss() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Gauss(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            double x = getChild(0).evaluate(treeInputs);
            return Math.exp(-(x * x));
        }

        @Override
        public int getArity() {
            return 1;
        }

        public String getName() {
            return "gauss";
        }
    }

    public static class Plus extends AbstractNode {

        public Plus(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Plus() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Plus(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            return getChild(0).evaluate(treeInputs) + getChild(1).evaluate(treeInputs);
        }

        @Override
        public int getArity() {
            return 2;
        }

        public String getName() {
            return "plus";
        }
    }

    public static class Sin extends AbstractNode {

        public Sin(int depth, long innovation, INode[] children) {
            super(depth, innovation, children);
        }

        public Sin() {
            super();
        }

        public INode create(int depth, INode[] children) {
            return new Sin(depth, InnovationCounter.getInstance().getNext(), children);
        }

        public double evaluate(TreeInputs treeInputs) {
            return Math.sin(getChild(0).evaluate(treeInputs));
        }

        @Override
        public int getArity() {
            return 1;
        }

        public String getName() {
            return "sin";
        }
    }

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
            double value = 0.0;
            for (int i = 0; i < getArity(); i++) {
                value += getConstants(i) * getChild(i).evaluate(treeInputs);
            }
            return value;
        }

        public String getName() {
            return "aplus";
        }
    }
}
