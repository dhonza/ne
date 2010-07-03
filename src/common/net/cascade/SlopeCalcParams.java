/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common.net.cascade;

import java.util.ArrayList;

/**
 * @author Administrator
 */
public class SlopeCalcParams {
    public NeuralNetwork neuralNetwork;
    public double decay;
    public TrainMode mode;
    public ArrayList<Synapse> synapsesToTrain;
}
