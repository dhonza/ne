package neat;

/**
 * <p>Title: </p>
 * <p>Description: Records innovations</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public abstract class GlobalInnovation {

    /**
     * If a Link innovation doesn't exist then <code>checkLink</code> returns this constant.
     *
     * @see #checkLink
     */
    static final int FREE = -1;

    /**
     * The current global Link innovation number.
     */
    int linkInnovation;

    /**
     * The current global Neuron innovation number.
     */
    int neuronInnovation;

    int generation;

    public GlobalInnovation(int olinkInnovation, int oneuronInnovation) {
        linkInnovation = olinkInnovation;
        neuronInnovation = oneuronInnovation;
        //stats = new GlobalInnovationStats(false); //TODO parameter
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * Records new Link.
     *
     * @param oin  the input Neuron
     * @param oout the output Neuron
     * @return new Link innovation number
     */
    abstract int addLink(int oin, int oout);

    /**
     * Records new Neuron.
     *
     * @return new Neuron innovation number
     */
    abstract int addNeuron(int oin, int oout, int ogeneration, Net ooriginalNet);

    /**
     * Gets actual Link innovation.
     *
     * @return actual Link innovation
     */
    public int getLinkInnovation() {
        return linkInnovation;
    }

    /**
     * Gets actual Neuron innovation.
     *
     * @return actual Neuron innovation
     */
    public int getNeuronInnovation() {
        return neuronInnovation;
    }

    /**
     * Returns the number of stored Link innovation records.
     *
     * @return number of stored innovation records
     */
    public int getNumOfRecords() {
        return 0;
    }

    /**
     * Checks if the Link is already recorded.
     *
     * @param oin  input Neuron id
     * @param oout output Neuron id
     * @return if Link already exists it's innovation Number, otherwise <code>FREE</code>
     * @see #FREE
     */
    abstract int checkLink(int oin, int oout);

    /**
     * Deletes all innovation records.
     */
    abstract void cleanHistory();

}