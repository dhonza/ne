package hyper.experiments.reco.fitness;

import neat.INet;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 10:11:32 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Warning this class uses Net and Neuron classes which are not immutable!
 * TODO does not check current state: order of method calls
 * TODO does not check sizes of pattern and output vectors
 */
public class HyperNetEvaluator1D implements HyperEvaluator1D {
    final private INet hyperNet;
    final private int activations;

    public HyperNetEvaluator1D(INet hyperNet, int activations) {
        this.hyperNet = hyperNet;
        this.activations = activations;
    }

    public void init() {
        hyperNet.initSetBias();
    }

    public void loadPatternToInputs(double[] pattern) {
        hyperNet.loadInputsNotBias(pattern);
        hyperNet.reset();
    }

    public void activate() {
        for (int i = 0; i < activations; i++) {
            hyperNet.activate();
        }
    }

    public double[] getOutputs() {
        return hyperNet.getOutputValues();
    }
}
