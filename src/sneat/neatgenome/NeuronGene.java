package sneat.neatgenome;

import sneat.neuralnetwork.IActivationFunction;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;

public class NeuronGene {
    // Although this id is allocated from the global innovation ID pool, neurons do not participate
    // in compatibility measurements and so it is not used as an innovation ID. It is used as a unique
    // ID to distinguish between neurons.
    int innovationId;
    NeuronType neuronType;
    IActivationFunction activationFunction;

    /// <summary>
    /// Copy constructor.
    /// </summary>
    /// <param name="copyFrom"></param>
    public NeuronGene(NeuronGene copyFrom) {
        this.innovationId = copyFrom.innovationId;
        this.neuronType = copyFrom.neuronType;
        this.activationFunction = copyFrom.activationFunction;
    }

    public NeuronGene(int innovationId, NeuronType neuronType, IActivationFunction activationFunction) {
        this.innovationId = innovationId;
        this.neuronType = neuronType;
        this.activationFunction = activationFunction;
    }

    public int getInnovationId() {
        return innovationId;
    }

    public void setInnovationId(int innovationId) {
        this.innovationId = innovationId;
    }

    public NeuronType getNeuronType() {
        return neuronType;
    }

    public void setNeuronType(NeuronType neuronType) {
        this.neuronType = neuronType;
    }


    public IActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(IActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

}
