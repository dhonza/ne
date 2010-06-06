package gp;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 20, 2009
 * Time: 1:45:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeInputs implements Serializable {
    final private int numOfInputs;
    private double[] inputs;

    public TreeInputs(int numOfInputs) {
        this.numOfInputs = numOfInputs;
    }

    public int getNumOfInputs() {
        return numOfInputs;
    }

    public void loadInputs(double[] inputs) {
        if (inputs.length != numOfInputs) {
            throw new IllegalArgumentException("Wrong size of input array: " + inputs.length + " expected was:" + numOfInputs);
        }
        this.inputs = inputs.clone();
    }

    public double get(int index) {
        return inputs[index];
    }
}
