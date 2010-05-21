package hyper.experiments.reco.fitness;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 10:08:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HyperEvaluator1D {

    public void init();

    public void loadPatternToInputs(final double[] pattern);

    public void activate();

    public double[] getOutputs();

}
