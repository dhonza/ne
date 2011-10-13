package gp;

import common.evolution.EvaluationInfo;
import common.evolution.IBlackBox;
import common.evolution.IMathematicaPrintable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Sep 14, 2010
 * Time: 9:06:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IGPForest extends IBlackBox, IMathematicaPrintable {
    int getNumOfInputs();

    double getFitness();

    void setFitness(double fitness);

    EvaluationInfo getEvaluationInfo();

    void setEvaluationInfo(EvaluationInfo evaluationInfo);

    String[] getOrigins();

    double[] getConstants();

    void setConstants(double[] constants);

    double getAverageArity();

    int getMaxTreeDepth();

    int getNumOfConstants();

    int getNumOfLeaves();

    int getNumOfNodes();

    IGPForest copy();
}
