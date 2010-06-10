package common.net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 9, 2010
 * Time: 8:20:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface INet {
    int getNumInputs();

    int getNumOutputs();

    int getNumHidden();

    int getNumLinks();

    void loadInputs(double[] inputs);

    void loadInputsNotBias(double[] inputs);

    void activate();

    void reset();

    void initSetBias();

    double[] getOutputValues();
}
