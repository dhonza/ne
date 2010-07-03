/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common.net.cascade;

/**
 * @author Administrator
 */
public interface ISlopeCalcFunction {
    public void calculateSlope(SlopeCalcParams params, TrainingSet trainingSet) throws Exception;

    public void calculateSlope(SlopeCalcParams params, TrainingPattern traningPattern) throws Exception;
}
