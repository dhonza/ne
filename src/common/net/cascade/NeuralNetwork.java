/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common.net.cascade;

import common.net.INet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.*;

/**
 * @author Do Minh Duc
 */
public class NeuralNetwork implements Serializable, INet {
    /* VARIABLES DECLARATIONS */
    private ArrayList<NeuronLayer> neuronLayers;
    private transient BufferedWriter out;
    public int neuronId;

    private int numOfInputs = 0;
    private int numOfHidden = 0;
    private int numOfOutputs = 0;
    private int numOfLinks = 0;

    private boolean activated = false;

    /*===================== PUBLIC METHODS ============================*/

    /**
     * Neural network constructor.
     *
     * @param inputsNumber       number of inputs
     * @param outputsNumber      number of outputs
     * @param biasNumber         number of bias neurons
     * @param activationFunction activation function of neurons
     * @param connect            if true automatically connect input neurons with output neurons
     * @throws java.lang.Exception
     */
    public NeuralNetwork(int inputsNumber, int outputsNumber, int biasNumber, IActivationFunction activationFunction, boolean connect) throws Exception {
        try {
            this.neuronId = 0;
            this.neuronLayers = new ArrayList<NeuronLayer>();
            NeuronLayer inputLayer = createLayer(inputsNumber, LayerType.input, this.neuronId, biasNumber, new ActivationFunctionLinear());
            NeuronLayer outputLayer = createLayer(outputsNumber, LayerType.output, this.neuronId, 0, activationFunction);
            if (connect) {
                this.fullyConnectLayers(inputLayer, outputLayer, true);
            }
            this.neuronLayers.add(inputLayer);
            this.neuronLayers.add(outputLayer);
        } catch (Exception ex) {
            throw new Exception("NeuralNetwork: NeuralNetwork -> " + ex.getMessage());
        }
    }

    /**
     * drchaj1
     * Represenation with biases starting at idx 0
     *
     * @param layers
     * @param bias
     * @param activationFunction
     */
    public NeuralNetwork(int[] layers, boolean bias, IActivationFunction activationFunction) throws Exception {
        this.neuronId = 0;
        this.neuronLayers = new ArrayList<NeuronLayer>();
        try {
            NeuronLayer inputLayer;
            Neuron biasNeuron = null;
            if (bias) {
                inputLayer = createLayer(layers[0] + 1, LayerType.input, this.neuronId, 0, new ActivationFunctionLinear());
                biasNeuron = inputLayer.getNeuron(0);
                biasNeuron.setType(NeuronType.bias);
            } else {
                inputLayer = createLayer(layers[0], LayerType.input, this.neuronId, 0, new ActivationFunctionLinear());
            }
            this.neuronLayers.add(inputLayer);
            NeuronLayer prev = inputLayer;
            for (int i = 1; i < layers.length - 1; i++) {
                NeuronLayer hiddenLayer = createLayer(layers[i], LayerType.hidden, this.neuronId, 0, activationFunction);
                this.fullyConnectLayersWithBias(prev, hiddenLayer, biasNeuron);
                this.neuronLayers.add(hiddenLayer);
                prev = hiddenLayer;
            }
            NeuronLayer outputLayer = createLayer(layers[layers.length - 1], LayerType.output, this.neuronId, 0, activationFunction);
            this.fullyConnectLayersWithBias(prev, outputLayer, biasNeuron);
            this.neuronLayers.add(outputLayer);
        } catch (Exception ex) {
            throw new Exception("NeuralNetwork: NeuralNetwork -> " + ex.getMessage());
        }
    }

    /**
     * Creates a neuron layer of specific property
     *
     * @param neuronsNumber      number of neurons in layer
     * @param layerType          type of layer
     * @param neuronId           just for debugging, should be removed at final version
     * @param biasNumber         number of bias neurons
     * @param activationFunction activation function of neurons
     * @return newly created neuron layer
     * @throws Exception
     */
    public NeuronLayer createLayer(int neuronsNumber, LayerType layerType, int neuronId, int biasNumber, IActivationFunction activationFunction) throws Exception {
        NeuronLayer newLayer = new NeuronLayer(neuronsNumber, layerType, neuronId, biasNumber, activationFunction);
        switch (layerType) {
            case input:
                numOfInputs += neuronsNumber;
                break;
            case hidden:
                numOfHidden += neuronsNumber;
                break;
            case output:
                numOfOutputs += neuronsNumber;
                break;
            default:
                throw new IllegalStateException("Unknown type of neuron layer!");
        }
        this.neuronId += neuronsNumber + biasNumber;
        return newLayer;

    }

    /**
     * Connects neurons of one layer with neurons of other layer.
     *
     * @param layerFrom    neuron layer where synapse connections originate
     * @param layerTo      neuron layer which synapse connections enter
     * @param randomWeight if true synapse weight are randomly generated
     * @throws java.lang.Exception
     */
    public void fullyConnectLayers(NeuronLayer layerFrom, NeuronLayer layerTo, boolean randomWeight) throws Exception {
        for (Neuron fromNeuron : layerFrom.neuronList()) {
            for (Neuron toNeuron : layerTo.neuronList()) {
                this.connectNeurons(fromNeuron, toNeuron, randomWeight);
            }
        }
    }

    /**
     * drchaj1 connect layers + connection from a single bias neuron (index 0 in input layer)
     *
     * @param layerFrom
     * @param layerTo
     * @param biasNeuron
     * @throws Exception
     */
    public void fullyConnectLayersWithBias(NeuronLayer layerFrom, NeuronLayer layerTo, Neuron biasNeuron) throws Exception {
        if (biasNeuron != null) {
            for (Neuron toNeuron : layerTo.neuronList()) {
                this.connectNeurons(biasNeuron, toNeuron, true);
            }
        }
        for (Neuron fromNeuron : layerFrom.neuronList()) {
            for (Neuron toNeuron : layerTo.neuronList()) {
                if (fromNeuron.type() != NeuronType.bias) {
                    this.connectNeurons(fromNeuron, toNeuron, true);
                }
            }
        }
    }

    /**
     * Adds new hidden layer to neural network at specific position.
     *
     * @param layer neuron layer to be added
     * @param index position where neuron layer will be added
     * @return true if processed without error
     */
    public boolean addHiddenLayer(NeuronLayer layer, int index) {
        if (index < 1 || index >= this.neuronLayers.size()) {
            return false;
        }
        if (layer.type() == LayerType.input || layer.type() == LayerType.output) {
            return false;
        }
        this.neuronLayers.add(index, layer);
        return true;
    }

    /**
     * Adds new hidden layer to neural network at specific position
     *
     * @param neuronsNumber      number of neurons
     * @param activationFunction activation function
     * @param index              position where neuron layer will be added
     * @return true if processed without error
     * @throws java.lang.Exception
     */
    public boolean addHiddenLayer(int neuronsNumber, IActivationFunction activationFunction, int index) {
        if (index < 1 || index >= this.neuronLayers.size()) return false;
        try {
            NeuronLayer newHiddenLayer = new NeuronLayer(neuronsNumber, LayerType.hidden, neuronId, 0, activationFunction);
            this.fullyConnectLayers(this.neuronLayers.get(index - 1), newHiddenLayer, true);
            this.fullyConnectLayers(newHiddenLayer, this.neuronLayers.get(index), true);
            this.neuronLayers.add(index, newHiddenLayer);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * Adds new hidden layer to neural network at position behind all currently existed hidden layers
     *
     * @param neuronsNumber      number of neurons
     * @param activationFunction activation function
     * @return true if processed without error
     */
    public boolean addHiddenLayer(int neuronsNumber, IActivationFunction activationFunction) {
        return this.addHiddenLayer(neuronsNumber, activationFunction, this.neuronLayers.size() - 1);
    }

    /**
     * Connects 2 neurons
     *
     * @param sourceNeuron      source neuron
     * @param destinationNeuron destination neuron
     * @param randomWeight      if true weight of synapse connection is generated randomly
     */
    public void connectNeurons(Neuron sourceNeuron, Neuron destinationNeuron, boolean randomWeight) {
        Synapse newSynapse = new Synapse(sourceNeuron, destinationNeuron);
        if (randomWeight) {
            newSynapse.generateWeight();
        }
        numOfLinks++;
    }

    /**
     * Connects 2 neurons
     *
     * @param sourceNeuron      source neuron
     * @param destinationNeuron destionation neuron
     * @param weight            weight of synapse connection
     */
    public void connectNeurons(Neuron sourceNeuron, Neuron destinationNeuron, double weight) {
        new Synapse(sourceNeuron, destinationNeuron, weight);
        numOfLinks++;
    }

    /**
     * Returns input neuron layer
     *
     * @return input neuron layer
     */
    public NeuronLayer getInputLayer() {
        return this.neuronLayers.get(0);
    }

    /**
     * Returns output neuron layer
     *
     * @return output neuron layer
     */
    public NeuronLayer getOutputLayer() {
        return this.neuronLayers.get(this.neuronLayers.size() - 1);

    }

    /**
     * Returns hidden neuron layers
     *
     * @return arraylist of hidden neuron layers
     */
    public ArrayList<NeuronLayer> getHiddenLayers() {
        List<NeuronLayer> temp = this.neuronLayers.subList(1, neuronLayers.size() - 1);
        return new ArrayList<NeuronLayer>(temp);
    }

    /**
     * Returns all neuron layers
     *
     * @return arraylist of all neuron layers
     */
    public ArrayList<NeuronLayer> getLayers() {
        return this.neuronLayers;
    }

    /**
     * Returns number of neuron layers
     *
     * @return number of neuron layers
     */
    public int getNumberOfLayers() {
        return this.neuronLayers.size();
    }

    /**
     * Returns all synapse connections
     *
     * @return arraylist of all synapse connections
     */
    public ArrayList<Synapse> getSynapses() {
        ArrayList<Synapse> synapses = new ArrayList<Synapse>();
        for (NeuronLayer neuronLayer : this.neuronLayers) {
            for (Neuron neuron : neuronLayer.neuronList()) {
                synapses.addAll(neuron.incomingSynapses());
            }
        }
        return synapses;
    }

    public void setSynapseWeights(double[] weights) {
        int cnt = 0;
        for (NeuronLayer neuronLayer : this.neuronLayers) {
            for (Neuron neuronTo : neuronLayer.neuronList()) {
                for (Synapse synapse : neuronTo.incomingSynapses()) {
                    synapse.setWeight(weights[cnt++]);
                }
            }
        }
    }


    /**
     * Bubbles the input from inputs through network to outputs
     */
    public void bubbleThrough() {
        ArrayList<NeuronLayer> layers = this.getHiddenLayers();
        layers.add(this.getOutputLayer());
        for (NeuronLayer layer : layers) {
            for (Neuron neuron : layer.neuronList()) {
                neuron.calculateNetInput();
                neuron.calculateOutput();
                neuron.calculateDerivative();
            }
        }
    }


    /**
     * Resets partial derivatives of all synapses by setting to 0
     */
    public void resetSlopes() {
        this.resetSlopes(this.getSynapses());
    }

    /**
     * Resets partial derivatives of synapses specified in parameter
     *
     * @param synapsesToReset synapses which partial derivatives are to be reset
     */
    public void resetSlopes(ArrayList<Synapse> synapsesToReset) {
        for (Synapse aSynapsesToReset : synapsesToReset) {
            aSynapsesToReset.setCurrentSlope(0);
        }
    }

    /**
     * Resets deltas of all synapses by setting to 0
     */
    public void resetDeltas() {
        for (NeuronLayer neuronLayer : this.neuronLayers) {
            for (Neuron neuron : neuronLayer.neuronList()) {
                neuron.setCurrentDelta(0);
            }
        }

    }

    /**
     * Processes inputs in training set and returns correspondent outputs
     *
     * @param trainingSet training set
     * @return array of outputs of all input patterns
     * @throws java.lang.Exception
     */
    public double[][] getOutputs(TrainingSet trainingSet, int numberOfOutputs) throws Exception {
        double[][] outputs;
        outputs = new double[trainingSet.size()][numberOfOutputs];
        int patternIndex = 0;
        for (TrainingPattern trainingPattern : trainingSet.getTraningSet()) {
            outputs[patternIndex] = this.getOutputs(trainingPattern);
            patternIndex++;
        }
        return outputs;
    }

    public double[] getOutputs(TrainingPattern trainingPattern) throws Exception {
        try {
            this.injectInput(trainingPattern.getInputPattern());
        } catch (Exception ex) {
            throw new Exception("NeuralNetwork: getOutputs -> " + ex.getMessage());
        }
        this.bubbleThrough();
        return this.getOutputs();
    }

    public double[] getOutputs() {
        double[] outputs = new double[this.getOutputLayer().size()];
        int index = 0;
        for (Neuron neuron : this.getOutputLayer().neuronList()) {
            outputs[index++] = neuron.currentOutput();
        }
        return outputs;
    }

    /**
     * Calculates residual error at outputs
     *
     * @param desiredOutput desired values of outputs
     * @param outputIndex
     * @return residual error
     * @throws java.lang.Exception
     */
    public double calculateOutputResidualError(double desiredOutput, int outputIndex) throws Exception {
        Neuron outputNeuron = this.getOutputLayer().getNeuron(outputIndex);
        double derivative = outputNeuron.calculateDerivative();
        double outputValue = outputNeuron.currentOutput();
        double residualError = (outputValue - desiredOutput) * derivative;
        return residualError;
    }

    /**
     * Calculates sum square error for training pattern
     *
     * @param trainingPattern training pattern
     * @return sum square error
     * @throws java.lang.Exception
     */
    public double calculateSquaredError(TrainingPattern trainingPattern) throws Exception {
        double patternSumSquareError = 0;
        NeuronLayer outputLayer = this.getOutputLayer();
        this.injectInput(trainingPattern.getInputPattern());
        this.bubbleThrough();
        Pattern desiredOutputs = trainingPattern.getDesiredOutputs();
        if (desiredOutputs.size() != outputLayer.size())
            throw new Exception("NeuralNetwork: calculateNetworkSumSquareError: number of training pattern outputs doesn't match the network");
        else {
            Iterator<Neuron> neuronIterator = outputLayer.neuronList().iterator();
            int index = 0;
            while (neuronIterator.hasNext()) {
                double outputValue = neuronIterator.next().currentOutput();
                double pom = outputValue - desiredOutputs.get(index++);
                patternSumSquareError += Math.pow(pom, 2);
            }
        }
        return patternSumSquareError;
    }

    /**
     * Calculates sum square error for training set
     *
     * @param trainingSet training set
     * @return sum square error
     * @throws java.lang.Exception
     */
    public double calculateSquaredError(TrainingSet trainingSet) throws Exception {
        double squaredError = 0;
        for (TrainingPattern trainingPattern : trainingSet.getTraningSet()) {
            squaredError += calculateSquaredError(trainingPattern);
        }
        return squaredError;
    }

    public double calculateMeanSquaredError(TrainingSet trainingSet) throws Exception {
        return this.calculateSquaredError(trainingSet) / trainingSet.size();
    }

    /**
     * stores all last partial derivatives
     */
    public void storeLastSlope() {
        this.storeLastSlope(this.getSynapses());
    }

    /**
     * stores last partial derivatives of synapses specified in parameter
     *
     * @param synapsesToStore synapses which partial derivatives are to be stored
     */
    public void storeLastSlope(ArrayList<Synapse> synapsesToStore) {
        for (Synapse aSynapsesToStore : synapsesToStore) {
            aSynapsesToStore.storeCurrentSlope();
        }
    }

    /*
     * check if really not needed
    public void makePartialDerivativeAverage(ArrayList<Synapse> synapsesToTrain, int trainingPatternNumber){
        for (int i = 0; i < synapsesToTrain.size(); i++){
            Synapse synapse = synapsesToTrain.get(i);
            synapse.setPartialDerivative(synapse.getPartialDerivative()/trainingPatternNumber);
        }


    }
    */

    /*
     * check if really not needed
    public double calculateNeuronOutputValue(TrainingPattern trainingPattern, Neuron neuron)throws Exception{
        this.injectInput(trainingPattern.getInputPattern());
        this.bubbleOuput();
        return neuron.getCurrentOutputValue();
    }
     * */


    /*============== PRIVATE METHODS ==================================*/

    /**
     * Checks consistency of neural network
     *
     * @return true if no error found
     */
    private boolean checkNeuralNetworkCorectness() {
        return (checkInputLayer() && checkOutputLayer() && checkHiddenLayers());
    }

    /**
     * Checks consistency of input layer of neural network.
     * Checks if the first layer is an input type layer.
     *
     * @return true if no error found
     */
    private boolean checkInputLayer() {
        return this.neuronLayers.get(0).type() == LayerType.input;
    }

    /**
     * Checks consistency of output layer of neural network.
     * Checks if the last layer is an output type layer.
     *
     * @return true if no error found
     */
    private boolean checkOutputLayer() {
        return this.neuronLayers.get(this.neuronLayers.size() - 1).type() == LayerType.output;
    }

    /**
     * Checks consistency of hidden layers of neural network.
     * Checks if all hidden layers are hidden type layers.
     *
     * @return true if no error found
     */
    private boolean checkHiddenLayers() {
        ArrayList<NeuronLayer> hiddenLayers = this.getHiddenLayers();
        for (NeuronLayer hiddenLayer : hiddenLayers) {
            if (!this.checkHiddenLayer(hiddenLayer)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks consistency of hidden layer passed in parameter.
     * Checks if the passed layer is hidden type layer.
     *
     * @param hiddenLayer hidden layer to be checked
     * @return true if no error found
     */
    private boolean checkHiddenLayer(NeuronLayer hiddenLayer) {
        return hiddenLayer.type() == LayerType.hidden;
    }

    /**
     * Checks if number of inputs in training pattern matches number of inputs of neural network.
     *
     * @param pattern input pattern to be checked
     * @return true if number of inputs matches
     */
    private boolean checkPatternInputNumber(Pattern pattern) {
        return pattern.size() == this.getInputLayer().size() - this.getInputLayer().biasNumber();
    }

    /**
     * Injects pattern inputs into network
     *
     * @param inputPattern input pattern
     * @return true if injection was successful
     * @throws java.lang.Exception
     */
    public boolean injectInput(Pattern inputPattern) throws Exception {
        if (!this.checkPatternInputNumber(inputPattern)) {
            return false;
        } else {
            NeuronLayer inputLayer = this.getInputLayer();
            for (int i = 0; i < inputPattern.size(); i++) {
                try {
                    inputLayer.getNeuron(i).setOutput(inputPattern.get(i));
                } catch (Exception ex) {
                    throw new Exception("NeuralNetwork: injectInput -> " + ex.getMessage());
                }
            }

            return true;
        }
    }


    /**
     * Checks if number of outputs in training pattern matches number of outputs of neural network.
     *
     * @param pattern pattern to be checked
     * @return true if no error found
     */
    private boolean checkPatternOutputNumber(Pattern pattern) {
        return pattern.size() == this.getOutputLayer().size();
    }

    public void printError(TrainingSet trainingSet) throws Exception {
        try {
            System.out.println("E = " + this.calculateSquaredError(trainingSet));
        } catch (Exception ex) {
            throw new Exception("NeuralNetwork: printError -> " + ex.getMessage());
        }
    }

    public void printNetworkToFile(String fileName, boolean append) {
        try {
            FileWriter fstream = new FileWriter(fileName, append);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("number of layers: " + this.neuronLayers.size());
            out.newLine();
            Iterator<NeuronLayer> layerIterator = this.neuronLayers.iterator();
            int index = 0;
            while (layerIterator.hasNext()) {
                NeuronLayer layer = layerIterator.next();
                out.write("layer " + index++ + ":  " + layer.size() + " neurons");
                out.newLine();
                for (Neuron neuron : layer.neuronList()) {
                    out.write("    neuron " + neuron.id() + ":");
                    out.newLine();
                    for (Synapse synapse : neuron.outgoingSynapses()) {
                        out.write("             === (" + synapse.weight() + " / " + synapse.lastWeightChange() + " / " + synapse.currentSlope() + " / " + synapse.stepSize() + ")==> neuron " + synapse.destinationNeuron().id());
                        out.newLine();
                        out.write("             slope = " + synapse.currentSlope());
                        out.write("    stepsize = " + synapse.stepSize());
                        out.newLine();
                        out.write("            delta w = " + synapse.lastWeightChange());
                        out.write("    w = " + synapse.weight());
                        out.newLine();
                    }
                }
                out.write(".................................................");
                out.newLine();
            }
            out.write("======================================================");
            out.newLine();
            out.newLine();
            out.close();
        } catch (Exception e) {
            System.out.println("NeuralNetwork: printNetwork: " + e.getMessage());
        }

    }

    /**
     * Added by drchaj1
     */
    public void printToMathematica() {
        StringBuilder builder = new StringBuilder("vertices := {");
        int layerCnt = 0;
        for (NeuronLayer layer : neuronLayers) {
            int neuronCnt = 0;
            for (Neuron neuron : layer.neuronList()) {
                int synapseCnt = 0;
                for (Synapse synapse : neuron.outgoingSynapses()) {
                    builder.append("{" + (neuron.id() + 1) + "->" + (synapse.destinationNeuron().id() + 1) + ", " + synapse.weight() + "}");
                    if (layerCnt != neuronLayers.size() - 2 || neuronCnt != layer.neuronList().size() - 1 || synapseCnt != neuron.outgoingSynapses().size() - 1) {
                        builder.append(", ");
                    }
                    synapseCnt++;
                }
                neuronCnt++;
            }
            layerCnt++;
        }

        builder.append("}\ncoords := {");
        layerCnt = 0;
        for (NeuronLayer layer : neuronLayers) {
            int neuronCnt = 0;
            for (Neuron neuron : layer.neuronList()) {
                double x, y;
                if (neuron.type() == NeuronType.bias) {
                    x = 0.0;
                    y = 0.0;
                } else {
                    x = neuronCnt;
                    y = layerCnt + 1;
                }
                builder.append(neuron.id() + 1).append("->{").append(x).append(",").append(y).append("}");
                if (layerCnt != neuronLayers.size() - 1 || neuronCnt != layer.neuronList().size() - 1) {
                    builder.append(", ");
                }
                neuronCnt++;
            }
            layerCnt++;
        }

        builder.append("}");
        System.out.println(builder);
    }

    //TODO quick an dirty :(

    public void storeHyperTrainingSet(String fileName) {
        int layerCnt = 0;
        Map<Neuron, double[]> coords = new HashMap<Neuron, double[]>();
        for (NeuronLayer layer : neuronLayers) {
            int neuronCnt = 0;
            for (Neuron neuron : layer.neuronList()) {
                double x, y;
                if (layerCnt == 0) {
                    if (neuronCnt == layer.neuronList().size() - 1) {
                        x = 0.0;
                        y = 0.0;
                    } else {
                        x = neuronCnt;
                        y = layerCnt + 1;
                    }
                } else {
                    x = neuronCnt;
                    y = layerCnt + 1;
                }
                coords.put(neuron, new double[]{x, y});
                neuronCnt++;
            }
            layerCnt++;
        }

        int totalSynapses = 0;
        for (NeuronLayer layer : neuronLayers) {
            for (Neuron neuron : layer.neuronList()) {
                totalSynapses += neuron.outgoingSynapses().size();
            }
        }


        layerCnt = 0;
        for (NeuronLayer layer : neuronLayers) {
            int neuronCnt = 0;
            for (Neuron neuron : layer.neuronList()) {
                int synapseCnt = 0;
                for (Synapse synapse : neuron.outgoingSynapses()) {
                    Neuron dstNeuron = synapse.destinationNeuron();
                    System.out.println(coords.get(neuron)[0] + "(" + (neuron.id() + 1) + ") " +
                            coords.get(dstNeuron)[0] + " " + "(" + (dstNeuron.id() + 1) + ") " +
                            (int) coords.get(neuron)[1] + " =  " + synapse.weight());
                }
            }
            layerCnt++;
        }
    }

    public int getNumInputs() {
        return numOfInputs;
    }

    public int getNumOutputs() {
        return numOfOutputs;
    }

    public int getNumHidden() {
        return numOfHidden;
    }

    public int getNumLinks() {
        return numOfLinks;
    }

    public void loadInputsWithBias(double[] inputs) {
        NeuronLayer inputLayer = this.getInputLayer();
        for (int i = 0; i < inputs.length; i++) {
            inputLayer.getNeuron(i).setOutput(inputs[i]);
        }
    }

    public void loadInputs(double[] inputs) {
        NeuronLayer inputLayer = this.getInputLayer();
        inputLayer.getNeuron(0).setOutput(1.0);
        for (int i = 0; i < inputs.length; i++) {
            inputLayer.getNeuron(i + 1).setOutput(inputs[i]);
        }
    }

    public void activate() {
        if (activated) {
            return;
        }
        bubbleThrough();
        activated = true;
    }

    public void reset() {
        if (activated) {
            activated = false;
        }
    }

    public void initSetBias() {
        //not needed here
    }
}
