package common.evolution;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 2:44:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEvolutionaryAlgorithm extends Serializable {
    public void initialGeneration();

    public void nextGeneration();

    public void performGeneralizationTest();

    public void finished();

    public boolean hasImproved();

    public int getGeneration();

    public int getEvaluations();

    public int getLastInnovation();

    public double getMaxFitnessReached();

    public EvaluationInfo[] getEvaluationInfo();

    public EvaluationInfo getGeneralizationEvaluationInfo();

    public boolean isSolved();

    public String getConfigString();
}
