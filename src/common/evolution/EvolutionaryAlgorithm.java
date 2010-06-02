package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 2:44:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EvolutionaryAlgorithm {
    public void initialGeneration();

    public void nextGeneration();

    public void finished();

    public boolean hasImproved();

    public int getGeneration();

    public int getEvaluations();

    public int getLastInnovation();

    public double getMaxFitnessReached();

    public double[] getFitnessVector();

    public boolean isSolved();

    public String getConfigString();
}
