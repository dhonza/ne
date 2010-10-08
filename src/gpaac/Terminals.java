package gpaac;

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

        public Input(int depth, long innovation, INode[] children, int idx) {
            super(depth, innovation, children);
            this.idx = idx;
        }

        public Input(int idx) {
            super();
            this.idx = idx;
        }

        public INode create(int depth, INode[] children) {
            return new Input(depth, InnovationCounter.getInstance().getNext(), null, idx);
        }

        public double evaluate(TreeInputs treeInputs) {
            return treeInputs.get(idx);
        }

        public String getName() {
            return "x" + idx;
        }
    }
}
