package hyper.experiments.reco.fitness;

import neat.Net;
import neat.Neuron;

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
    final private Net hyperNet;
    final private int bias;
    final private int inStart;
    final private int outStart;
    final private int activations;
    private Neuron[] neurons;

    public HyperNetEvaluator1D(Net hyperNet, int activations) {
        this.hyperNet = hyperNet;
        this.bias = 0;
        this.inStart = 1;
        this.outStart = hyperNet.getNumInputs() + hyperNet.getNumHidden();
        this.activations = activations;
    }

    public void init() {
        neurons = hyperNet.getAllNeurons();
        neurons[bias].setOutput(1.0);
        neurons[bias].setUpdated(true);
        for (int i = inStart; i < outStart; i++) {
            neurons[i].setUpdated(true);
        }
    }

    public void loadPatternToInputs(double[] pattern) {
        int cnt = inStart;
        for (double patternItem : pattern) {
            neurons[cnt++].setOutput(patternItem);
        }
        hyperNet.reset();
    }

    public void activate() {
        for (int i = 0; i < activations; i++) {
            hyperNet.activate();
        }
    }

    public double[] getOutputs() {
        double[] outputs = new double[hyperNet.getNumOutputs()];
        for (int i = 0; i < 0 + hyperNet.getNumOutputs(); i++) {
            outputs[i] = neurons[i + outStart].getOutput();
        }
        return outputs;
    }
}
