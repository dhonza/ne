package divvis;

/**
 * User: drchaj1
 * Date: 27.3.2007
 * Time: 16:11:57
 */
public interface EquationsInterface {
    public int getDimension();

    public double evaluate(double[][] oD, double[] op);

    public void gradient(double[][] oD, double[] op, double[] oforce);
}
