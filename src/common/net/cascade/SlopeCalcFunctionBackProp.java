/*
 * PartialDerivativeFunctionBackProp.java
 *
 * Created on 15 d�cembre 2008, 11:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package common.net.cascade;

import java.util.Iterator;

/**
 * @author Puchina
 */
public class SlopeCalcFunctionBackProp implements ISlopeCalcFunction {

    private void calculateDelta(SlopeCalcParams params, TrainingPattern trainingPattern) throws Exception {
        try {
            params.neuralNetwork.injectInput(trainingPattern.getInputPattern());
            params.neuralNetwork.bubbleThrough();
            this.calculateDeltaForOutputs(params, trainingPattern.getDesiredOutputs());
            this.calculateDeltaForHiddens(params);
        }
        catch (Exception e) {
            throw new Exception("SlopeCalcFunctionBackProp: calculateDelta -> " + e.getMessage());
        }

    }

    private void calculateDeltaForOutputs(SlopeCalcParams params, Pattern desiredOutputs) throws Exception {
        NeuralNetwork neuralNetwork = params.neuralNetwork;
        Iterator<Neuron> neuronIterator = neuralNetwork.getOutputLayer().neuronList().iterator();
        int index = 0;
        while (neuronIterator.hasNext()) {
            neuronIterator.next().calculateDelta(desiredOutputs.get(index++));
        }
    }

    private void calculateDeltaForHiddens(SlopeCalcParams params) {
        NeuralNetwork neuralNetwork = params.neuralNetwork;
        Iterator<NeuronLayer> layerIterator = neuralNetwork.getHiddenLayers().iterator();
        while (layerIterator.hasNext()) {
            Iterator<Neuron> neuronIterator = layerIterator.next().neuronList().iterator();
            while (neuronIterator.hasNext()) {
                neuronIterator.next().calculateDelta();
            }
        }
    }

    private void calculateSlope(Synapse synapse, SlopeCalcParams params, boolean accumulate) {
        double slope = synapse.destinationNeuron().currentDelta() * synapse.sourceNeuron().currentOutput();
        if (accumulate) synapse.setCurrentSlope(slope + synapse.currentSlope());
        else synapse.setCurrentSlope(slope);
    }

    public void calculateSlope(SlopeCalcParams params, TrainingSet trainingSet) throws Exception {
        params.neuralNetwork.storeLastSlope();
        params.neuralNetwork.resetDeltas();
        params.neuralNetwork.resetSlopes();
        for (int i = 0; i < trainingSet.size(); i++) {
            try {
                TrainingPattern trainingPattern = trainingSet.getTrainingPattern(i);
                this.calculateDelta(params, trainingPattern);
            }
            catch (Exception e) {
                throw new Exception("SlopeCalcFunctionBackProp: calculateSlope -> " + e.getMessage());
            }
            boolean accumulate = true;
            Iterator<Synapse> synapseIterator = params.synapsesToTrain.iterator();
            while (synapseIterator.hasNext()) {
                this.calculateSlope(synapseIterator.next(), params, accumulate);
            }
        }
        //params.neuralNetwork.makePartialDerivativeAverage(params.synapsesToTrain, trainingSet.getSize());

    }

    public void calculateSlope(SlopeCalcParams params, TrainingPattern trainingPattern) throws Exception {
        params.neuralNetwork.storeLastSlope();
        params.neuralNetwork.resetDeltas();
        params.neuralNetwork.resetSlopes();
        try {
            this.calculateDelta(params, trainingPattern);
        }
        catch (Exception e) {
            throw new Exception("SlopeCalcFunctionBackProp: calculateSlope -> " + e.getMessage());
        }
        boolean accumulate = false;
        Iterator<Synapse> synapseIterator = params.synapsesToTrain.iterator();
        while (synapseIterator.hasNext()) {
            this.calculateSlope(synapseIterator.next(), params, accumulate);
        }
    }
}
