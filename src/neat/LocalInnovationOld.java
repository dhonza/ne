package neat;

import common.RND;
import common.net.linked.Link;
import common.net.linked.Net;
import common.net.linked.Neuron;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages assignment of new structural innovations. It stores all Links of a Net in a 2D matriz representing
 * from(input Neuron) ---> to(output Neuron). The algorithm fo proposing of a new Link innovation tries until succeeds or
 * the number of this tests is greater than NE.INNOVATION_MATRIX_TRIES.
 */
class LocalInnovationOld {
    /**
     * References all the Links. The row represents the Link's input Neuron, the
     * column its output Neuron. The rows consist of input, hidden and output
     * neurons, the columns of only hidden and output neurons (no signal can be
     * fed to inputs). Both rows and columns are ordered by Neuron inovation
     * numbers ( <i>Neuron.id </i>). <i>linkMatrix[row][col] </i> contains the
     * reference to the corresponding Link or <b>null </b> if there is no Link
     * between two Neurons.
     */
    private Link[][] linkMatrix;

    /**
     * References position in linkMatrix by given Neuron.
     */
    private Map<Neuron, Integer> neuronInMatrix;

    /**
     * References Neuron by given position in linkMatrix.
     */
    private Map<Integer, Neuron> positionInMatrix;

    /**
     * Proposed Link recurrence flag.
     */
    private boolean proposedRecurrent;

    /**
     * Used to address columns in linkMatrix.
     */
    private int offsetColumn;

    private int numNeurons;

    private Net net;

    /**
     * Allocets all structures to reflect given Net.
     *
     * @param onet Net template
     */
    LocalInnovationOld(Net onet) {
        net = onet;

        // +1 reserve for mutateAddNeuron()
        int inputs = net.getNumNeurons() + 1;
        int outputs = net.getNumHidOut() + 1;
        linkMatrix = new Link[inputs][outputs];

        neuronInMatrix = new HashMap(inputs);
        positionInMatrix = new HashMap(inputs);

        offsetColumn = inputs - outputs;
        numNeurons = inputs;

        // +1 reserve for mutateAddNeuron()
        //TODO prob. slow, use arrays?
        int i = 0;
        for (Neuron tn : net.getInputs()) {
            neuronInMatrix.put(tn, i);
            positionInMatrix.put(i, tn);
            i++;
        }

        for (Neuron tn : net.getHidout()) {
            neuronInMatrix.put(tn, i);
            positionInMatrix.put(i, tn);
            i++;
        }
    }

    /**
     * Adds a new Neuron plus two Links which connect it to the Net plus biasing Links.
     *
     * @param on
     * @param oin
     * @param oout
     * @param obias if null nothing is added
     */
    void addNeuron(Neuron on, Link oin, Link oout, Link obias) {
        int row = numNeurons - 1;
        int col = row - offsetColumn;
        neuronInMatrix.put(on, row);
        positionInMatrix.put(row, on);

        linkMatrix[neuronInMatrix.get(oin.getIn())][col] = oin;
        linkMatrix[row][neuronInMatrix.get(oout.getOut()) - offsetColumn] = oout;

        linkMatrix[0][col] = obias; // bias
    }

    //TODO comment  + Net should have at least one non bias output

    /**
     * Proposes a new Neuron innovation. It returns an existing Link (this can be changed in future) which will be replaced
     * by a Neuron and two Links. We don't take biasing Links.
     *
     * @return Base Link for new Neuron.
     */
    Link proposeNeuronInnovation() {
        Link tlink;
        int proposedIn;
        int proposedOut;

        int tries = 0;

        while (tries < NEAT.getConfig().innovationMatrixTries) {

            proposedIn = RND.getInt(1, net.getNumNeurons() - 1); // don't take BIASes
            proposedOut = RND.getInt(0, net.getNumHidOut() - 1);

            tlink = linkMatrix[proposedIn][proposedOut];

            if (tlink != null) { // this could be removed if we don't need new Neuron replacing existing Link

                proposedRecurrent = net.testRecurrent(positionInMatrix.get(proposedIn), positionInMatrix.get(proposedOut + offsetColumn));

                if (NEAT.getConfig().recurrent || !proposedRecurrent) {
                    return tlink;
                }
            }
            tries++;
        }
        return null;
    }

    /**
     * Adds a new Link.
     *
     * @param olink Link to add.
     */
    void addLink(Link olink) {
        int from, to;
        from = neuronInMatrix.get(olink.getIn());
        to = neuronInMatrix.get(olink.getOut()) - offsetColumn;
        linkMatrix[from][to] = olink;
    }


    private Link getLink(int ofrom, int oto) {
        return linkMatrix[ofrom][oto];
    }

    /**
     * Proposes a new Link innovation. It is a random hypothetic connenction of two yet unconnected Neurons.
     *
     * @return Input and output Neurons of hypothetic Link.
     */
    Neuron[] proposeLinkInnovation() {
        Neuron[] tneurons = new Neuron[2];
        tneurons[0] = null;
        tneurons[1] = null;
        int tries = 0;

        /** TODO speed up recurrence tests */
        while (tries < NEAT.getConfig().innovationMatrixTries) {
            int proposedIn = RND.getInt(0, net.getNumNeurons() - 1);
            int proposedOut = RND.getInt(0, net.getNumHidOut() - 1);
            if (getLink(proposedIn, proposedOut) == null) {

                tneurons[0] = positionInMatrix.get(proposedIn); // the input Neuron of the new Link
                tneurons[1] = positionInMatrix.get(proposedOut + offsetColumn); // the output Neuron
//                proposedRecurrent = net.testRecurrent(tneurons[0], tneurons[1]);

                proposedRecurrent = net.testRecurrent2(tneurons[0], tneurons[1]);

                if (NEAT.getConfig().recurrent)
                    break;
                else {
                    if (!proposedRecurrent)
                        break;
                }
            }
            tries++;
        }

        if (tries == NEAT.getConfig().innovationMatrixTries) {
            System.out.println(" ERROR: maximum number of NE.INNOVATION_MATRIX_TRIES exceeded!");
            return null;
        }

        return tneurons;
    }


    /**
     * Checks the proposed Link's recurrence.
     *
     * @return True if recurrent, false if forward.
     */
    boolean isProposedRecurrent() {
        return proposedRecurrent;
    }

}