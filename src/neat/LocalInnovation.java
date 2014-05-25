package neat;

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
class LocalInnovation {
    private class InnovLink {
        private Neuron from;
        private Neuron to;

        public InnovLink(Neuron from, Neuron to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InnovLink innovLink = (InnovLink) o;

            if (from != null ? !from.equals(innovLink.from) : innovLink.from != null) return false;
            if (to != null ? !to.equals(innovLink.to) : innovLink.to != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

    private Map<InnovLink, Link> linkMap = new HashMap<InnovLink, Link>();
    /**
     * References all the Links. The row represents the Link's input Neuron, the
     * column its output Neuron. The rows consist of input, hidden and output
     * neurons, the columns of only hidden and output neurons (no signal can be
     * fed to inputs). Both rows and columns are ordered by Neuron inovation
     * numbers ( <i>Neuron.id </i>). <i>linkMatrix[row][col] </i> contains the
     * reference to the corresponding Link or <b>null </b> if there is no Link
     * between two Neurons.
     */
//    private Link[][] linkMatrix;

    /**
     * References position in linkMatrix by given Neuron.
     */
//    private Map<Neuron, Integer> neuronInMatrix;

    /**
     * References Neuron by given position in linkMatrix.
     */
//    private Map<Integer, Neuron> positionInMatrix;

    /**
     * Proposed Link recurrence flag.
     */
    private boolean proposedRecurrent;

    /**
     * Used to address columns in linkMatrix.
     */

    private Net net;

    /**
     * Allocets all structures to reflect given Net.
     *
     * @param onet Net template
     */
    LocalInnovation(Net onet) {
        net = onet;
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

        linkMap.put(new InnovLink(oin.getIn(), on), oin);
        linkMap.put(new InnovLink(on, oout.getOut()), oout);
        linkMap.put(new InnovLink(obias.getIn(), on), obias);
        //TODO BIAS!!!!!!
//        linkMap.add(new InnovLink(on, oout.getOut()));
    }

    //TODO comment  + Net should have at least one non bias output

    /**
     * Proposes a new Neuron innovation. It returns an existing Link (this can be changed in future) which will be replaced
     * by a Neuron and two Links. We don't take biasing Links.
     *
     * @return Base Link for new Neuron.
     */
    Link proposeNeuronInnovation() {
        Neuron proposedIn;
        Neuron proposedOut;

        int tries = 0;

        while (tries < NEAT.getConfig().innovationMatrixTries) {

            proposedIn = net.getRandomAllNotBias();
            proposedOut = net.getRandomHidOut();

            Link tlink = linkMap.get(new InnovLink(proposedIn, proposedOut));
            if (tlink != null) { // this could be removed if we don't need new Neuron replacing existing Link
//                proposedRecurrent = net.testRecurrent(proposedIn, proposedOut);
//                if (NEAT.getConfig().recurrent || !proposedRecurrent) {
                return tlink;
//                }
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
        linkMap.put(new InnovLink(olink.getIn(), olink.getOut()), olink);
    }


    private Link getLink(int ofrom, int oto) {
        //note: neuron Id is the only key...
        return linkMap.get(new InnovLink(new Neuron(ofrom, Neuron.Type.HIDDEN, Neuron.Activation.SIGMOID),
                new Neuron(oto, Neuron.Type.HIDDEN, Neuron.Activation.SIGMOID)));
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
//            Neuron proposedIn = net.getRandomAllNotBias();
            Neuron proposedIn = net.getRandomInputNotBias();
            Neuron proposedOut = net.getRandomHidOut();
            if (getLink(proposedIn.getId(), proposedOut.getId()) == null) {

                tneurons[0] = proposedIn; // the input Neuron of the new Link
                tneurons[1] = proposedOut; // the output Neuron
//                proposedRecurrent = net.testRecurrent(tneurons[0], tneurons[1]);

                proposedRecurrent = net.testRecurrent2(tneurons[0], tneurons[1]);

                if (getLink(proposedOut.getId(), proposedIn.getId()) != null && !proposedRecurrent) {
                    System.out.println("PODEZRELE: " + proposedRecurrent + " " + proposedIn.getId() + "---->" + proposedOut.getId());
                    System.out.println(net);
                    net.testRecurrent2(tneurons[0], tneurons[1]);
                }


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