package neat;

public interface Evaluable {
    /**
     * Evaluates the Genome (the associated neural network). This method is used when oevaluateAll is set to false in
     * NE.run().
     *
     * @param og Genome to evaluate
     * @return the Genome's fitness
     * @see #evaluateAll
     * @see ne.NE#run
     */
    public double evaluate(Genome og);

    /**
     * Evaluates the whole Population - useful for Co-evolution. For example: put all robots to arena to fight with each
     * other. This method is used when oevaluateAll is set to true in NE.run().
     * @param opop all Genomes in population
     * @param ofitnessValues array which should be filled with fitness values of the whole population
     * @see #evaluate
     * @see ne.NE#run
     */
    public void evaluateAll(Genome[] opop, double[] ofitnessValues);


    /**
     * Returns the number of ANN's inputs excluding bias (input No. 0);
     *
     * @return number of inputs
     */
    public int getNumberOfInputs();

    /**
     * Returns the number of ANN's outputs.
     *
     * @return number of outputs
     */
    public int getNumberOfOutputs();

    public void storeEvaluation(Genome og);

    public double[][][] getStoredInputs();

    public double[][][] getStoredOutputs();
}