package gpat;

import gp.TreeInputs;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/16/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ATNodeImpl {
    abstract public String getName();

    abstract public double evaluate(IATNode node, TreeInputs treeInputs);

    public boolean hasConstants() {
        //DO NOT CHANGE!
        return false;//DEFAULT
    }

    public boolean isTerminal() {
        return false;//DEFAULT
    }

    public int repeatConstant() {
        return Integer.MAX_VALUE;//DEFAULT
//        return 1;
    }

    public int repeatInput() {
        return 1;//DEFAULT
    }

    public int maxArity() {
        return Integer.MAX_VALUE;//DEFAULT
    }

    protected double innerPotential(IATNode node, TreeInputs treeInputs) {
        if (hasConstants()) {
            double result = node.getConstant(0) * node.getChild(0).evaluate(treeInputs);
            for (int i = 1; i < node.getArity(); i++) {
                result += node.getConstant(i) * node.getChild(i).evaluate(treeInputs);
            }
            return result;
        } else {
            double result = node.getChild(0).evaluate(treeInputs);
            for (int i = 1; i < node.getArity(); i++) {
                result += node.getChild(i).evaluate(treeInputs);
            }
            return result;
        }
    }
}
