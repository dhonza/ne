package common.net.precompiled;

import common.net.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 11:39:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrecompiledFeedForwardNet implements INet {
    public int getNumInputs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumOutputs() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumHidden() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getNumLinks() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadInputs(double[] inputs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadInputsNotBias(double[] inputs) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void activate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void initSetBias() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double[] getOutputValues() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
