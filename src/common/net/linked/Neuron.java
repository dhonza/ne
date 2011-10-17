package common.net.linked;

import common.RND;
import neat.NEAT;

import java.io.Serializable;
import java.util.ArrayList;

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

public class Neuron implements Serializable {
    public enum Type implements Serializable {
        /**
         * the input neuron
         */
        INPUT,
        /**
         * the hidden neuron
         */
        HIDDEN,
        /**
         * the output neuron
         */
        OUTPUT
    }

    public enum Activation implements Serializable {
        SIGMOID,
        BIPOLAR_SIGMOID,
        LINEAR,
        GAUSS,
        ABS,
        SIN,
        COS,
        SQR,
        SQRT,
        MULT;


        public static Activation getRandom() {
//            Activation[] all = new Activation[]{SIGMOID, BIPOLAR_SIGMOID, LINEAR, GAUSS, ABS, SIN, COS, SQR, SQRT, MULT};
            Activation[] all = new Activation[]{BIPOLAR_SIGMOID, LINEAR, GAUSS, SIN, MULT};
            return RND.randomChoice(all);
        }

        public static Activation getRandomWeighted() {
            System.out.println("NOT ALL NODES IMPLEMENTED!");
            System.exit(1);
            double r = RND.getDouble();
            double t = NEAT.getConfig().activationSigmoidProbability;
            if (r < t) {
                return SIGMOID;
            }
            t += NEAT.getConfig().activationBipolarSigmoidProbability;
            if (r < t) {
                return BIPOLAR_SIGMOID;
            }
            t += NEAT.getConfig().activationLinearProbability;
            if (r < t) {
                return LINEAR;
            }
            t += NEAT.getConfig().activationGaussProbability;
            if (r < t) {
                return GAUSS;
            }
            t += NEAT.getConfig().activationAbsProbability;
            if (r < t) {
                return ABS;
            }
            t += NEAT.getConfig().activationSinProbability;
            if (r < t) {
                return SIN;
            }
            t += NEAT.getConfig().activationCosProbability;
            if (r < t) {
                return COS;
            }
            t += NEAT.getConfig().activationSqrProbability;
            if (r < t) {
                return SQR;
            }
            return SQRT;
        }
    }

    /**
     * neuron id
     */
    private int id;

    /**
     * type
     */
    private Type type;

    private Activation activation;

    /**
     * the sum of weighted incoming signals
     */
    private double sum = 0.0;

    /**
     * the output of the neuron
     */
    private double output = 0.0;

    /**
     * how many times has been the neuron activated
     */
    private int activationCount = 0;

    /**
     * a signal entered the neuron - used in network.activate()
     */
    private boolean updated = false;

    /**
     * gain of an activation function
     */
    private double alpha = 4.924273;
    //    private double alpha = 0.7;
    /**
     * this is used for visualisation
     */
    // TODO remove
    private double x = 0.0;
    private double y = 0.0;

    /** TODO tune capacity */
    /**
     * incoming links
     */
    private ArrayList<Link> incoming = new ArrayList<Link>();

    /**
     * outgoing links
     */
    private ArrayList<Link> outgoing = new ArrayList<Link>();


    public boolean isUpdated() {
        return updated;
    }

    /**
     * @param oisUpdated The updated to set.
     */
    public void setUpdated(boolean oisUpdated) {
        updated = oisUpdated;
    }

    /**
     * Constructs new Neuron with id and type.
     *
     * @param oid   neuron identification number
     * @param otype neuron type
     */
    public Neuron(int oid, Type otype, Activation oactivation) {
        id = oid;
        type = otype;
        activation = oactivation;
    }

    /**
     * Overwrites <b>Object.clone() </b>. Note this method copies only neurons
     * <b>id </b>, <b>type </b> and <b>updated </b> flag. It also initialises
     * <b>incoming </b> and <b>outgoing </b>, but the are not filled.
     * DOPLNIT!!!!!!!!!!!!1
     *
     * @return clone of this Neuron
     */
    public Object clone() {
        Neuron t = new Neuron(id, type, activation);
        t.incoming = new ArrayList<Link>(incoming.size());
        t.outgoing = new ArrayList<Link>(outgoing.size());
        t.updated = updated;
        return t;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Neuron neuron = (Neuron) o;

        return id == neuron.id;

    }

    public int hashCode() {
        return id;
    }

    public String toString() {
        return "Neuron id:" + id + " type:" + type + " act:" + activation + " sum:" + sum + " output:" + output + " activationCount:"
                + activationCount + " updated:" + updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public double getSum() {
        return sum;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public ArrayList<Link> getIncoming() {
        return incoming;
    }

    public ArrayList<Link> getOutgoing() {
        return outgoing;
    }

    public void computeSum() {
        Link tl;
        setUpdated(false);
        sum = 0.0;
        if (activation != Activation.MULT) {
            for (int i = 0; i < getIncoming().size(); i++) {
                tl = getIncoming().get(i);
                if (tl.isEnabled()) { //only if the Link is enabled
                    sum += tl.getIn().output * tl.getWeight();
                    if (tl.getIn().isUpdated()) {
                        setUpdated(true);
                    }
                }
            }
        } else {
            sum = 1.0;
            for (int i = 0; i < getIncoming().size(); i++) {
                tl = getIncoming().get(i);
                if (tl.isEnabled()) { //only if the Link is enabled
                    sum *= tl.getIn().output * tl.getWeight();
                    if (tl.getIn().isUpdated()) {
                        setUpdated(true);
                    }
                }
            }
        }
    }

    /**
     * Computs activation sigmoidal function.
     * TODO correct
     * Functional value of <i>1/( 1 + exp( -ALPHA/sum ) ) </i>
     */

    public void computeOutput() {
        //System.out.println(sum);
        if (isUpdated()) {
            switch (activation) {
                case SIGMOID:
                    output = 1.0 / (1.0 + Math.exp(-alpha * sum));
                    break;
                case BIPOLAR_SIGMOID:
                    output = 2.0 / (1.0 + Math.exp(-alpha * sum)) - 1.0;
                    break;
                case LINEAR:
                    output = sum;
                    break;
                case GAUSS:
                    output = Math.exp(-2.5 * x * x);
                    break;
                case ABS:
                    output = Math.abs(sum);
                    break;
                case SIN:
                    output = Math.sin(sum);
                    break;
                case COS:
                    output = Math.cos(sum);
                    break;
                case SQR:
                    output = sum * sum;
                    break;
                case SQRT:
                    output = Math.sqrt(Math.abs(sum));
                    break;
                case MULT://special type sum already contains multiplication of inputs and the weights
                    output = sum;
                    break;
                default:
                    System.out.println("Unknown activation funcion: " + activation);
                    System.exit(1);
            }

        }
    }

    public double computeOutputD() {
        return output * (1.0 - output);
    }
}