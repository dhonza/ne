package common.net;

import common.evolution.IBlackBox;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 8:20:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface INet extends IBlackBox {
    int getNumInputs();

    int getNumOutputs();

    int getNumHidden();

    int getNumLinks();

    void loadInputsNotBias(double[] inputs);

    void activate();

    void reset();

    void initSetBias();
}
