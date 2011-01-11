package common.net.linked;

import common.RND;
import common.net.INet;
import common.net.linked.NetStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class Net implements INet, Serializable {
    public static final int INPUT = 1, HIDDEN = 2, OUTPUT = 3;

    class NetRuntimeException extends RuntimeException implements Serializable {
        NetRuntimeException() {
        }

        NetRuntimeException(String oerrMessage) {
            super(oerrMessage);
        }

    }

    /**
     * network id
     */
    protected int id;

    /**
     * input neurons
     */
    protected List<Neuron> inputs;

    /**
     * output neurons
     */
    protected List<Neuron> outputs;

    protected List<Neuron> hidout;

    protected List<Link> links;

    /**
     * number of neurons
     */
    protected int numNeurons = 0;

    protected int numInputs = 0;

    protected int numHidden = 0;

    protected int numOutputs = 0;

    protected int numHidOut = 0;

    /**
     * number of links
     */
    protected int numLinks = 0;

    /**
     * max depth of testRecurrent() search
     */
    private int recurrentThreshold = 15;

    /**
     * helps to testRecurrent() method
     */
    private int recurrentCount;

    /**
     * max Neuron id - useful for clonning
     */
    private int maxNeuronId = 0;

    /**
     * max Link id - useful for clonning
     */
    private int maxLinkId = 0;

    /**
     * Constructs an empty Network.
     *
     * @param oid Network id
     */
    public Net(int oid) {
        id = oid;
    }

    /**
     * Constructs an empty Network with structures initialized to given size.
     *
     * @param oid         Network id
     * @param onumInputs  number of inputs
     * @param onumHidden  number of hidden Neurons
     * @param onumOutputs number of outputs
     */
    public Net(int oid, int onumInputs, int onumHidden, int onumOutputs) {
        id = oid;
        inputs = new ArrayList<Neuron>(onumInputs);
        hidout = new ArrayList<Neuron>(onumHidden + onumOutputs);
        outputs = new ArrayList<Neuron>(onumOutputs);
        links = new ArrayList<Link>();
    }

    /**
     * Constructs a Network from file.
     *
     * @param oid       Network id
     * @param ofileName filename of network definition file - see the class
     *                  introduction
     */
    public Net(int oid, String ofileName) {
        id = oid;
        NetStorage.load(ofileName);
    }

    public int getNumHidOut() {
        return numHidOut;
    }

    public int getNumNeurons() {
        return numNeurons;
    }

    public int getMaxNeuronId() {
        return maxNeuronId;
    }

    public int getMaxLinkId() {
        return maxLinkId;
    }

    public int getId() {
        return id;
    }

    public List<Neuron> getInputs() {
        return inputs;
    }

    public List<Neuron> getHidout() {
        return hidout;
    }

    public List<Neuron> getOutputs() {
        return outputs;
    }

    public List<Link> getLinks() {
        return links;
    }

    /**
     * @return First neuron (usually bias).
     */
    public Neuron getBiasNeuron() {
        return inputs.get(0);
    }


    public void createFeedForward(int onumInputs, int[] onumHidden, int onumOutputs) {
        createFeedForward(onumInputs, onumHidden, onumOutputs, Neuron.Activation.SIGMOID);
    }

    /**
     * Creates a feed forward network with all weights set to zero.
     * BIAS!!!!!!!!!!!
     *
     * @param onumInputs  number of input neurons
     * @param onumHidden  specifies the number of neurons of each hidden layer, the size
     *                    of the array determines the number of hidden layers
     * @param onumOutputs number of output neurons
     * @see #createFullyConnected
     */
    public void createFeedForward(int onumInputs, int[] onumHidden, int onumOutputs, Neuron.Activation hidoutType) {
        numInputs = onumInputs + 1; //bias
        numHidden = 0;
        numOutputs = onumOutputs;

        int i, j, k, l, counterN = 0, counterL = 0;
        for (i = 0; i < onumHidden.length; i++) {
            numHidden += onumHidden[i];
        }
        numHidOut = numHidden + numOutputs;
        numNeurons = numInputs + numHidOut;
        maxNeuronId = numNeurons - 1;

        numLinks = 0;

        inputs = new ArrayList<Neuron>(numInputs);
        hidout = new ArrayList<Neuron>(numHidOut);
        outputs = new ArrayList<Neuron>(numOutputs);
        /** TODO precount numLinks */
        links = new ArrayList<Link>();

        ArrayList<Neuron> tmp1 = new ArrayList<Neuron>();
        ArrayList<Neuron> tmp2 = new ArrayList<Neuron>();
        Neuron tn;
        Link tl;

        //bias Neuron
        Neuron bias = new Neuron(counterN++, Neuron.Type.INPUT, Neuron.Activation.LINEAR);
        bias.setUpdated(true);
        inputs.add(bias);

        //input Neurons
        for (i = 1; i < numInputs; i++, counterN++) {
            tn = new Neuron(counterN, Neuron.Type.INPUT, Neuron.Activation.LINEAR);
            tn.setUpdated(true);
            inputs.add(tn);
            tmp1.add(tn);
        }

        // Links from input to hidden layers
        for (i = 0; i < onumHidden.length; i++) {
            // the Neurons of i-th hidden layer + Link from bias to them
            for (j = 0; j < onumHidden[i]; j++) {
                tn = new Neuron(counterN++, Neuron.Type.HIDDEN, hidoutType);
                hidout.add(tn);
                tmp2.add(tn);

                tl = new Link(counterL++, 0.0, bias, tn); // bias Link
                links.add(tl);
                bias.getOutgoing().add(tl);
                tn.getIncoming().add(tl);
            }
            numLinks += tmp1.size() * tmp2.size() + tmp2.size();
            for (k = 0; k < tmp1.size(); k++)
                // Links
                for (l = 0; l < tmp2.size(); l++) {
                    tl = new Link(counterL++, 0.0, tmp1.get(k), tmp2.get(l));
                    links.add(tl);
                    tmp1.get(k).getOutgoing().add(tl);
                    tmp2.get(l).getIncoming().add(tl);
                }
            tmp1 = (ArrayList) tmp2.clone();
            tmp2.clear();
        }

        // output Neurons + Links from bias to them
        for (j = 0; j < numOutputs; j++, counterN++) {
            tn = new Neuron(counterN, Neuron.Type.OUTPUT, hidoutType);
            hidout.add(tn);
            outputs.add(tn);
            tl = new Link(counterL++, 0.0, bias, tn);
            links.add(tl);
            bias.getOutgoing().add(tl);
            tn.getIncoming().add(tl);
        }

        numLinks += tmp1.size() * numOutputs + numOutputs;
        for (k = 0; k < tmp1.size(); k++)
            for (l = 0; l < outputs.size(); l++, counterL++) {
                tl = new Link(counterL, 0.0, tmp1.get(k), outputs.get(l));
                links.add(tl);
                tmp1.get(k).getOutgoing().add(tl);
                outputs.get(l).getIncoming().add(tl);
            }
        maxLinkId = numLinks - 1;
    }

    public void createConnectedLayers(int[] layers) {
        int[] tlayers = layers.clone();
        tlayers[0]++; //bias
        numInputs = tlayers[0];
        numHidden = 0;
        numOutputs = tlayers[tlayers.length - 1];


        for (int i = 1; i < tlayers.length - 1; i++) {
            numHidden += tlayers[i];
        }
        numHidOut = numHidden + numOutputs;
        numNeurons = numInputs + numHidOut;
        maxNeuronId = numNeurons - 1;

        numLinks = 0;

        inputs = new ArrayList<Neuron>(numInputs);
        hidout = new ArrayList<Neuron>(numHidOut);
        outputs = new ArrayList<Neuron>(numOutputs);

        links = new ArrayList<Link>();

        int counterN = 0;
        ArrayList[] layerMap = new ArrayList[tlayers.length];
        for (int i = 0; i < tlayers.length; i++) {
            ArrayList<Neuron> layerNeurons = new ArrayList<Neuron>();
            layerMap[i] = layerNeurons;
            for (int j = 0; j < tlayers[i]; j++) {
                Neuron n;
                if (i == 0) {//input layer
                    n = new Neuron(counterN++, Neuron.Type.INPUT, Neuron.Activation.LINEAR);
                    n.setUpdated(true);
                    inputs.add(n);
                } else if (i == tlayers.length - 1) {//output layer
                    n = new Neuron(counterN++, Neuron.Type.OUTPUT, Neuron.Activation.SIGMOID);
                    hidout.add(n);
                    outputs.add(n);
                } else {//hidden
                    n = new Neuron(counterN++, Neuron.Type.HIDDEN, Neuron.Activation.SIGMOID);
                    hidout.add(n);
                }
                layerNeurons.add(n);
            }
        }

        int counterL = 0;
        for (int i = 0; i < tlayers.length - 1; i++) {
            for (int j = i + 1; j < tlayers.length; j++) {
                ArrayList<Neuron> fromLayer = layerMap[i];
                ArrayList<Neuron> toLayer = layerMap[j];
                for (Neuron fromNeuron : fromLayer) {
                    for (Neuron toNeuron : toLayer) {
                        Link link = new Link(counterL++, 0.0, fromNeuron, toNeuron, false);
                        links.add(link);
                        numLinks++;
                    }
                }

            }
        }
        maxLinkId = numLinks - 1;
    }

    /**
     * Creates a fully connected network with all weights set to zero.
     *
     * @param onumInputs  number of input neurons
     * @param onumHidden  number of hidden neurons
     * @param onumOutputs number of output neurons
     * @see #createFeedForward
     */
    public void createFullyConnected(int onumInputs, int onumHidden, int onumOutputs) {
        inputs = new ArrayList<Neuron>();
        outputs = new ArrayList<Neuron>();
        hidout = new ArrayList<Neuron>();
        links = new ArrayList<Link>();

        numInputs = onumInputs + 1;
        numHidden = onumHidden;
        numOutputs = onumOutputs;
        numHidOut = onumHidden + onumOutputs;

        int i, j, counter = 0;
        Neuron tn;
        Link tl;

        for (i = 0; i < numInputs; i++, counter++) {
            tn = new Neuron(counter, Neuron.Type.INPUT, Neuron.Activation.LINEAR);
            tn.setUpdated(true);
            inputs.add(tn);
        }

        for (i = 0; i < onumHidden; i++, counter++)
            hidout.add(new Neuron(counter, Neuron.Type.HIDDEN, Neuron.Activation.SIGMOID));
        for (i = 0; i < onumOutputs; i++, counter++) {
            tn = new Neuron(counter, Neuron.Type.OUTPUT, Neuron.Activation.SIGMOID);
            outputs.add(tn);
            hidout.add(tn);
        }
        numNeurons = counter;
        counter = 0;
        maxNeuronId = numNeurons - 1;

        for (i = 0; i < numInputs; i++)
            for (j = 0; j < numHidOut; j++, counter++) {
                tl = new Link(counter, 0.0, inputs.get(i), hidout.get(j));
                links.add(tl);
                inputs.get(i).getOutgoing().add(tl);
                hidout.get(j).getIncoming().add(tl);
            }
        for (i = 0; i < numHidOut; i++)
            for (j = 0; j < numHidOut; j++, counter++) {
                tl = new Link(counter, 0.0, hidout.get(i), hidout.get(j));
                tl.setRecurrent(true);
                links.add(tl);
                hidout.get(i).getOutgoing().add(tl);
                hidout.get(j).getIncoming().add(tl);
            }
        numLinks = counter;
        maxLinkId = numLinks - 1;
    }

    /**
     * @return Returns the numInputs.
     */
    public int getNumInputs() {
        return numInputs;
    }

    /**
     * @return Returns the numOutputs.
     */
    public int getNumOutputs() {
        return numOutputs;
    }

    /**
     * @return Returns the numHidden.
     */
    public int getNumHidden() {
        return numHidden;
    }

    /**
     * @return Returns the numLinks.
     */
    public int getNumLinks() {
        return numLinks;
    }

    /**
     * Adds a new Link to the Net. Note, the Link is implicitly unconnected to
     * the net - this should be done manually. This method is used by add-link
     * mutation.
     *
     * @param olink new link
     */
    public void addLink(Link olink) {
        links.add(olink);
        numLinks++;
        if (olink.getId() > maxLinkId) {
            maxLinkId = olink.getId();
        }
    }

    public void addLink(Link olink, Neuron ofrom, Neuron oto) {
        addLink(olink);
        ofrom.getOutgoing().add(olink);
        oto.getIncoming().add(olink);
        olink.setIn(ofrom);
        olink.setOut(oto);
    }

    /**
     * Adds a new input Neuron to the Net. Note, the Link is implicitly
     * unconnected to the net - this should be done manually.
     *
     * @param oneuron new neuron
     */
    public void addInput(Neuron oneuron) {
        inputs.add(oneuron);
        numInputs++;
        numNeurons++;
        if (oneuron.getId() > maxNeuronId) {
            maxNeuronId = oneuron.getId();
        }
    }

    /**
     * Adds a new hidden Neuron to the Net. Note, the Link is implicitly
     * unconnected to the net - this should be done manually.
     *
     * @param oneuron new neuron
     */
    public void addHidden(Neuron oneuron) {
        hidout.add(oneuron);
        numHidden++;
        numHidOut++;
        numNeurons++;
        if (oneuron.getId() > maxNeuronId) {
            maxNeuronId = oneuron.getId();
        }
    }

    /**
     * Adds a new output Neuron to the Net. Note, the Link is implicitly
     * unconnected to the net - this should be done manually.
     *
     * @param oneuron new neuron
     */
    public void addOutput(Neuron oneuron) {
        hidout.add(oneuron);
        outputs.add(oneuron);
        numOutputs++;
        numHidOut++;
        numNeurons++;
        if (oneuron.getId() > maxNeuronId) {
            maxNeuronId = oneuron.getId();
        }
    }

    /**
     * Adds a structure which consists of one hidden Neuron and two Links. The
     * first {input} Link's output and the second (output) Link's input are
     * connected to the Neuron. Note, this whole structure is not connected to
     * the Net - should be done manually. This method is used by add-neuron
     * mutation.
     *
     * @param oneuron  new neuron
     * @param oinLink  input link
     * @param ooutLink output link
     */
    public void addNeuron(Neuron oneuron, Link oinLink, Link ooutLink) {
        hidout.add(oneuron);
        numHidden++;
        numHidOut++;
        numNeurons++;
        if (oneuron.getId() > maxNeuronId) {
            maxNeuronId = oneuron.getId();
        }
        if (oinLink.getId() > maxLinkId) {
            maxLinkId = oinLink.getId();
        }
        if (ooutLink.getId() > maxLinkId) {
            maxLinkId = ooutLink.getId();
        }
        links.add(oinLink);
        numLinks++;
        links.add(ooutLink);
        numLinks++;
    }


    /**
     * Activates the network.
     *
     * @see #loadInputs
     */
    public void activate() {
        for (Neuron tn : hidout) {
            tn.computeSum();
        }

        for (Neuron tn : hidout) {
            tn.computeOutput();
        }
    }

    /**
     * Resets the network. This method should be called prior to a new
     * evaluation.
     */
    public void reset() {
        for (Neuron tn : hidout) {
            tn.setUpdated(false);
            tn.setOutput(0.0);
        }
    }

    /**
     * Tests if the hypotetic link is recurrent.
     *
     * @param oin  input neuron
     * @param oout output neuron
     * @return <b>true </b> if the hypotetic link between <i>oin </i> and
     *         <i>oout </i> is recurrent
     */
    public boolean testRecurrent(Neuron oin, Neuron oout) {
        recurrentCount = 0;
        return testRecurrentHelper(oin, oout);
    }

    private boolean testRecurrentHelper(Neuron oin, Neuron oout) {
        Link tl;
        recurrentCount++;

        if (recurrentCount > recurrentThreshold) {
            new NetRuntimeException("ERROR determining the recurrence of the link -THE NETWORK IS BROKEN");
            return false; //Short out the whole thing- loop detected
        }

        if (oin == oout)
            return true;
        else {
            for (int i = 0; i < oin.getIncoming().size(); i++) {
                tl = oin.getIncoming().get(i);
                if (!tl.isRecurrent()) {
                    if (testRecurrentHelper(tl.getIn(), oout))
                        return true;
                }
            }
            return false;
        }

    }

    /**
     * Tries to find neuron of given id in hidden and output set.
     *
     * @param oid
     * @return true if found, false if not
     */
    public boolean testForHidOut(int oid) {
        for (Neuron neuron : hidout) {
            if (neuron.getId() == oid) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all the output neurons were updated.
     *
     * @return <b>true </b> if all outputs were updated, <b>false </b> if at
     *         least one wasn't
     */
    public boolean allOutputsUpdated() {
        System.out.println("ne.Net.allOutputsUpdated() NOT YET IMPLEMENTED!!!!!");
        System.exit(1);
        return false;
    }

    /**
     * Return the inputs of the network.
     *
     * @return the inputs
     */
    public double[] getInputValues() {
        double[] t = new double[numInputs];
        for (int i = 0; i < numInputs; i++)
            t[i] = inputs.get(i).getOutput();
        return t;
    }

    /**
     * Return the outputs of the network.
     *
     * @return the outputs
     */
    public double[] getOutputValues() {
        double[] t = new double[numOutputs];
        for (int i = 0; i < numOutputs; i++)
            t[i] = outputs.get(i).getOutput();
        return t;
    }

    public double[] getClampedOutputValues(double olower, double oupper) {
        double[] t = new double[numOutputs];
        double to;
        for (int i = 0; i < numOutputs; i++) {
            to = outputs.get(i).getOutput();
            if (to < olower) {
                t[i] = olower;
            } else if (to > oupper) {
                t[i] = oupper;
            }
            t[i] = to;
        }
        return t;
    }

    /**
     * Prints outputs to String.
     *
     * @return String containing all output values
     */
    public String getStringOutputs() {
        int i;
        String tmp = "[";
        for (i = 0; i < numOutputs - 1; i++)
            tmp += outputs.get(i).getOutput() + ", ";
        tmp += outputs.get(i).getOutput();
        tmp += "]";
        return tmp;
    }

    /**
     * Loads data to input neurons. Note, the neuron definition in this library
     * does not include the threshold value. Therefore it's strongly recommended
     * to use one additional input with constant value (the bias neuron) and to
     * connect this neuron to inputs of all neurons in hidden and output layers.
     *
     * @param inputs array of input values, it's preffered to have <i>inputs[0] =
     *               const. </i> for biasing
     */
    public void loadInputs(double[] inputs) {
        for (int i = 0; i < numInputs; i++) {
            this.inputs.get(i).setOutput(inputs[i]);
        }
    }

    public void loadInputsNotBias(double[] inputs) {
        for (int i = 1; i < numInputs; i++) {
            this.inputs.get(i).setOutput(inputs[i - 1]);
        }
    }

    public void initSetBias() {
        getBiasNeuron().setOutput(1.0);
        for (Neuron input : this.inputs) {
            input.setUpdated(true);
        }
        for (Neuron hidout : this.hidout) {
            if (hidout.getType() == Neuron.Type.HIDDEN) {
                hidout.setUpdated(true);
            }
        }
    }

    /**
     * Returns neuron with id equal to oid.
     *
     * @param oid the searched Neuron id
     * @return the Neuron with id equal to oid or <b>null </b>
     */
    Neuron getNeuron(int oid) {
        /** TODO make this faster? */
        int i;

        for (i = 0; i < numInputs; i++)
            if (inputs.get(i).getId() == oid)
                return inputs.get(i);
        for (i = 0; i < numHidOut; i++)
            if (hidout.get(i).getId() == oid)
                return hidout.get(i);
        return null;
    }

    /**
     * Loads the weight of each link from an array. Note, this method does not
     * check the order of the links, so use it carefuly. The method is supposed
     * to work complementary with saveWeights(). It is also useful for GAs
     * opimizing the weights.
     *
     * @param oweights the weights are replaced by this array
     * @see #saveWeights
     */
    public void loadWeights(double[] oweights) {
        for (int i = 0; i < numLinks; i++)
            links.get(i).setWeight(oweights[i]);
    }

    /**
     * Saves the weight of each link to an array. Note, this method does not
     * check the order of the links, so use it carefuly. The method is supposed
     * to work complementary with loadWeights().
     *
     * @return array filled with the weights
     * @see #loadWeights
     */
    public double[] saveWeights() {
        double[] tmp = new double[numLinks];
        for (int i = 0; i < numLinks; i++)
            tmp[i] = links.get(i).getWeight();
        return tmp;
    }

    /**
     * Randomizes all weights.
     *
     * @param omin minimum weight value
     * @param omax maximum weight value
     */
    public void randomizeWeights(double omin, double omax) {
        for (int i = 0; i < numLinks; i++)
            links.get(i).setWeight(RND.getDouble(omin, omax));
    }

    /**
     * Checks the network.
     *
     * @return <b>true </b> if the Net is OK, else <b>false </b>
     */
    public boolean check() {
        /** TODO finnish */

        //Check if numbers of neurons are correct
        if (numInputs != inputs.size() ||
                numOutputs != outputs.size() ||
                numHidden != (hidout.size() - outputs.size()) ||
                numHidOut != (hidout.size())
                ) {
            System.out.println("ERROR: numbers of neurons are incorrect");
            return false;
        }

        sort();
        //Check if all Links are included only once
        if (numLinks > 1) {
            for (int i = 1; i < numLinks; i++)
                if (links.get(i - 1).getId() >= links.get(i).getId())
                    return false;
        }
        //Check if all hidden neurons have output links
        for (int i = 0; i < numHidOut; i++) {
            Neuron tn = hidout.get(i);
            if (tn.getType() == Neuron.Type.HIDDEN && tn.getOutgoing().size() == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sorts all Neurons and Links by their ids.
     */
    public void sort() {
        /** TODO finnish */
        Collections.sort(links);
    }

    public Object clone() {
        Net t = new Net(id);
        return copyTo(t);
    }

    public Object copyTo(Net onet) {

        onet.numNeurons = numNeurons;
        onet.numInputs = numInputs;
        onet.numHidden = numHidden;
        onet.numOutputs = numOutputs;
        onet.numHidOut = numHidOut;
        onet.numLinks = numLinks;
        onet.recurrentCount = recurrentCount;
        onet.maxLinkId = maxLinkId;
        onet.maxNeuronId = maxNeuronId;
        onet.inputs = new ArrayList<Neuron>(numInputs);
        /** TODO pozor na rychlost!!!! */
        onet.outputs = new ArrayList<Neuron>(numOutputs);
        onet.hidout = new ArrayList<Neuron>(numHidOut);
        onet.links = new ArrayList<Link>(numLinks);

        /** TODO !!!maybe slow for high id's!!! memory consumption ?! */
        Neuron[] idTable = new Neuron[maxNeuronId + 1]; //table of Neurons
        // indexed by their id's
        Link tlold, tlnew;
        Neuron tn;
        int i;
        for (i = 0; i < numLinks; i++) {
            tlold = links.get(i);
            tlnew = (Link) tlold.clone();

            if (idTable[tlold.getIn().getId()] == null) { //must create input Neuron,
                // it's not in a table
                tn = (Neuron) tlold.getIn().clone(); //clone Neuron
                if (tn.getType() == Neuron.Type.INPUT)
                    onet.inputs.add(tn); //add it the lists of Neurons
                else {
                    onet.hidout.add(tn);
                    if (tn.getType() == Neuron.Type.OUTPUT)
                        onet.outputs.add(tn);
                }

                tlnew.setIn(tn); //make the input of the Link
                idTable[tlold.getIn().getId()] = tn; //update the table
                tn.getOutgoing().add(tlnew); // add the Link to the Neuron's list of
                // Links
            } else { //just use the input Neuron already in table
                tlnew.setIn(idTable[tlold.getIn().getId()]);
                idTable[tlold.getIn().getId()].getOutgoing().add(tlnew); // add the Link to the
                // Neuron's list of
                // Links
            }
            //now the same for output Neurons
            if (idTable[tlold.getOut().getId()] == null) {
                tn = (Neuron) tlold.getOut().clone();
                if (tn.getType() == Neuron.Type.INPUT)
                    onet.inputs.add(tn);
                else {
                    onet.hidout.add(tn);
                    if (tn.getType() == Neuron.Type.OUTPUT)
                        onet.outputs.add(tn);
                }

                tlnew.setOut(tn);
                idTable[tlold.getOut().getId()] = tn;
                tn.getIncoming().add(tlnew);
            } else {
                tlnew.setOut(idTable[tlold.getOut().getId()]);
                idTable[tlold.getOut().getId()].getIncoming().add(tlnew);
            }

            onet.links.add(tlnew);
        }

        return onet;
    }

    /*
    public void toVNet3d(VNet3D ovnet) {
        Neuron tn;
        Iterator it = inputs.iterator();
        int biasId = -1;

        if (it.hasNext()) {//skip bias
            tn = (Neuron) (it.next());
            biasId = tn.getId();
        }
        while (it.hasNext()) {
            tn = (Neuron) (it.next());
            ovnet.addNode(tn.getId(), VNet3D.IN);
        }

        it = hidout.iterator();
        while (it.hasNext()) {
            tn = (Neuron) (it.next());
            if (tn.getType() == Neuron.Type.HIDDEN) {
                ovnet.addNode(tn.getId(), VNet3D.HIDDEN);
            } else {
                ovnet.addNode(tn.getId(), VNet3D.OUT);
            }
        }
        Link tl;
        it = links.iterator();
        while (it.hasNext()) {
            tl = (Link) (it.next());
            if (tl.getIn().getId() != biasId) {
                ovnet.addLink(tl.getId(), tl.getIn().getId(), tl.getOut().getId(), tl.getWeight());
            } else {//this is bias neuron -> set threshold...
                ovnet.setNodeThreshold(tl.getOut().getId(), tl.getWeight());
            }
        }
    }
    */
    public double[][] toWeightMatrix() {
        //TODO note, ids must start from 0, no missing!
        double[][] m = new double[getNumNeurons()][getNumHidOut()];
        for (Link link : links) {
            m[link.getIn().getId()][link.getOut().getId() - getNumInputs()] = link.getWeight();
        }
        return m;
    }

    public String toString() {
        String tmp = "Network id:" + id + "\n" + " Neurons #" + numNeurons + " in:"
                + numInputs + " hid:" + numHidden + " out:" + numOutputs + "\n";
        for (int i = 0; i < numInputs; i++)
            tmp += "  " + inputs.get(i) + "\n";
        for (int i = 0; i < numHidOut; i++)
            tmp += "  " + hidout.get(i) + "\n";
        tmp += " Links #" + numLinks + "\n";
        for (int i = 0; i < numLinks; i++)
            tmp += new StringBuilder().append("  ").append(links.get(i)).append("\n").toString();
        return tmp;
    }
}