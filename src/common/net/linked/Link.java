package common.net.linked;

import neat.NEAT;

import java.io.Serializable;

/**
 * <p/>
 * Title: NeuroEvolution
 * </p>
 * <p/>
 * Description:
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company:
 * </p>
 *
 * @author Jan Drchal
 * @version 0001
 */

public class Link implements Comparable, Serializable {

    /** TODO add id? */

    /**
     * link id
     */
    private int id;

    /**
     * the weight of the link
     */
    private double weight;

    /**
     * is the link enabled?
     */
    private boolean enabled = true;

    /**
     * the link recurrence
     */
    private boolean recurrent = false;

    /**
     * input Neuron
     */
    private Neuron in;

    /**
     * output Neuron
     */
    private Neuron out;

    /**
     * Constructs new link. Note, only <b>id </b> and <b>weight </b> are set.
     *
     * @param oid     link id
     * @param oweight the weight of the link
     */
    public Link(int oid, double oweight) {
        id = oid;
        weight = oweight;
    }

    /**
     * Constructs new link.
     *
     * @param oid     link id
     * @param oweight the weight of the link
     * @param oin     the input neuron
     * @param oout    the output neuron
     */
    public Link(int oid, double oweight, Neuron oin, Neuron oout) {
        id = oid;
        weight = oweight;
        in = oin;
        out = oout;
        in.getOutgoing().add(this);
        out.getIncoming().add(this);
    }

    /**
     * Constructs new link.
     *
     * @param oid     link id
     * @param oweight the weight of the link
     * @param oin     the input neuron
     * @param oout    the output neuron
     */
    public Link(int oid, double oweight, Neuron oin, Neuron oout, boolean orecurrent) {
        id = oid;
        weight = oweight;
        in = oin;
        out = oout;
        recurrent = orecurrent;
        in.getOutgoing().add(this);
        out.getIncoming().add(this);
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight The weight to set.
     */
    public void setWeight(double weight) {
        if (weight > NEAT.getConfig().netWeightsAmplitude) {
            this.weight = NEAT.getConfig().netWeightsAmplitude;
        } else if (weight < -NEAT.getConfig().netWeightsAmplitude) {
            this.weight = -NEAT.getConfig().netWeightsAmplitude;
        } else {
            this.weight = weight;
        }
    }

    /**
     * @return Returns the in.
     */
    public Neuron getIn() {
        return in;
    }

    /**
     * @param in The in to set.
     */
    public void setIn(Neuron in) {
        this.in = in;
    }

    /**
     * @return Returns the out.
     */
    public Neuron getOut() {
        return out;
    }

    /**
     * @param out The out to set.
     */
    public void setOut(Neuron out) {
        this.out = out;
    }

    /**
     * @return Returns the recurrent.
     */
    public boolean isRecurrent() {
        return recurrent;
    }

    /**
     * @param recurrent The recurrent to set.
     */
    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Toggles the enabled flag.
     */
    public void toggleEnabled() {
        enabled = !enabled;
    }

    /**
     * Prints the neuron to a String. Used for file saving.
     *
     * @return represents the dneuron in neural network definition file
     */
    public String toFileAsString() {
        return "l  " + id + "  " + in.getId() + "  " + out.getId() + "  " + weight;
    }

    /**
     * Clones the link. Note, this method doesn assign <b>in </b> and <b>out
     * </b> neurons to the clonned link.
     *
     * @return clone of this Link
     */
    public Object clone() {
        Link tl = new Link(id, weight);
        tl.enabled = enabled;
        tl.recurrent = recurrent;
        return (Object) tl;
    }

    /**
     * Implements the <code>Comparable</code> interface. Used for sorting.
     * Note, the sorting is done in ascending order.
     *
     * @param oo the Object to be compared
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object
     */
    public int compareTo(Object oo) {
        if (id < ((Link) oo).id)
            return -1;
        else if (id > ((Link) oo).id)
            return 1;
        else
            return 0;
    }

    public String toString() {
        return "Link id:" + id + " weight:" + weight + " " + in.getId() + "---->" + out.getId() + " enabled:" + enabled + " recurrent:" + recurrent;
    }
}