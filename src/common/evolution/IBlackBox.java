package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 10/13/11
 * Time: 5:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IBlackBox {
    public void loadInputs(double[] inputs);

    public void propagate();

    public double[] getOutputs();
}
