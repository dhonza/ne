package neat;

import common.net.linked.Neuron;
import common.pmatrix.ParameterCombination;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 10/17/11
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class OutputNeuronTypeFactory {
    private OutputNeuronTypeFactory() {
    }

    public static Neuron.Activation getOutputNeuron(ParameterCombination parameters) {
        String outputType;
        if (!parameters.contains("NEAT.OUTPUT_TYPE")) {
            outputType = "BIPOLAR_SIGMOID";
        } else {
            outputType = parameters.getString("NEAT.OUTPUT_TYPE");
        }
        if (outputType.equals("LINEAR")) {
            return Neuron.Activation.LINEAR;
        } else if (outputType.equals("BIPOLAR_SIGMOID")) {
            return Neuron.Activation.BIPOLAR_SIGMOID;
        } else {
            throw new IllegalStateException("Unknown output type: " + outputType);
        }
    }
}
