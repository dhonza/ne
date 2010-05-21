package sneat.neuralnetwork.concurrentnetwork;

public class Connection {
    int sourceNeuronId; // These are redundant in normal operation (we have a reference to the neurons)
    int targetNeuronId;    // but is useful when creating/loading a network.

    Neuron sourceNeuron;
    double weight;

    public Connection(int sourceNeuronId, int targetNeuronId, double weight) {
        this.sourceNeuronId = sourceNeuronId;
        this.targetNeuronId = targetNeuronId;
        this.weight = weight;
    }

    public void setSourceNeuron(Neuron neuron) {
        sourceNeuron = neuron;
    }

    public int getSourceNeuronId() {
        return sourceNeuronId;
    }

    public void getSourceNeuronId(int value) {
        sourceNeuronId = value;
    }

    public int getTargetNeuronId() {
        return targetNeuronId;
    }

    public void setTargetNeuronId(int value) {
        targetNeuronId = value;
    }

    public double getWeight() {
        return weight;
    }

    public Neuron getSourceNeuron() {
        return sourceNeuron;
    }
}