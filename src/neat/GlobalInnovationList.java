package neat;

import common.RND;
import common.net.linked.Net;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

class GlobalInnovationList extends GlobalInnovation {

    /**
     * Represents the Link item of GlobalInnovationList
     */
    class InnovationLinkItem {
        int linkInnovation; //the link innovation id
        int in, out; //input and output neuron id

        InnovationLinkItem(int olinkInnovation, int oin, int oout) {
            linkInnovation = olinkInnovation;
            in = oin;
            out = oout;
        }

        public String toString() {
            return "LI:" + linkInnovation + " IN:" + in + " OUT:" + out;
        }
    }

    /**
     * Represents the Neuron item of GlobalInnovationList
     */
    class InnovationNeuronItem {

        class HistoryItem {
            private int neuronInnovation; //new Neuron id
            private int generation; //the Generation number

            public HistoryItem(int oneuronInnovation, int ogeneration) {
                this.neuronInnovation = oneuronInnovation;
                this.generation = ogeneration;
            }

            public int getNeuronInnovation() {
                return neuronInnovation;
            }

            public int getGeneration() {
                return generation;
            }
        }

        int in, out; //input and output Neuron id of the replaced Link

        List<HistoryItem> history;

        InnovationNeuronItem(int oneuronInnovation, int oin, int oout, int ogeneration) {
            in = oin;
            out = oout;

            history = new ArrayList<HistoryItem>();
            history.add(new HistoryItem(oneuronInnovation, ogeneration));
        }

        /**
         * Constructor for searching purposes.
         *
         * @param oin
         * @param oout
         */
        InnovationNeuronItem(int oin, int oout) {
            in = oin;
            out = oout;
        }


        void insertVersion(int onew, int ogeneration) {
            history.add(new HistoryItem(onew, ogeneration));
//            stats.incrementNewVersionGNI(ogeneration);
        }

        /**
         * @return Random version, newer version are more probable to be selected.
         */
        public int getRandomVersion(int ogeneration) {
            int n = history.size(); //number of versions
//            stats.incrementReusedGNI(ogeneration);

            return history.get(RND.getInt(0, n - 1)).getNeuronInnovation();

        }

        public int getRandomVersion2(int ogeneration) {
            int n = history.size(); //number of versions
//            if (n >= 4) {
//                System.out.println("LARGE");
//            }
            double s = 0.0;
            for (int i = 1; i <= n; i++) {
                s += Math.pow(NEAT.getConfig().globalNeuronInnovationAttentuationRatio, (double) i); //TODO test others
            }

            double a = 1 / s;

            double r = RND.getDouble();
            s = 0;

//            stats.incrementReusedGNI(ogeneration);
            for (int i = 1; i <= n; i++) {
                s += a * Math.pow(NEAT.getConfig().globalNeuronInnovationAttentuationRatio, (double) i);
                if (r < s) {
                    return history.get(n - i).getNeuronInnovation();
                }
            }

            return history.get(0).getNeuronInnovation();

        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final InnovationNeuronItem that = (InnovationNeuronItem) o;

            if (in != that.in) return false;
            if (out != that.out) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = in;
            result = 29 * result + out;
            return result;
        }

        public String toString() {
            return "NI:" + linkInnovation + " IN:" + in + " OUT:" + out;
        }
    }


    /**
     * List to store innovations
     */
    private LinkedList<InnovationLinkItem> linkInnov;

    private HashMap<InnovationNeuronItem, InnovationNeuronItem> neuronInnov;

    public GlobalInnovationList(int olinkInnovation, int oneuronInnovation) {
        super(olinkInnovation, oneuronInnovation);
        linkInnov = new LinkedList<InnovationLinkItem>();
        neuronInnov = new HashMap<InnovationNeuronItem, InnovationNeuronItem>();
    }

    /**
     * Adds the new link with a new innovation number. <i>checkLink()</i> should precede. Don't add already added Link - this
     * method doesn't check this behavior (however there should be no harm).
     *
     * @param oin  input Neuron id
     * @param oout output Neuron id
     * @return new Link innovation number
     * @see #checkLink
     */
    int addLink(int oin, int oout) {

        try {
//            stats.incrementNewGLI(generation);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        linkInnovation++;
        linkInnov.add(new InnovationLinkItem(linkInnovation, oin, oout));
        return linkInnovation;
    }

    /**
     * Adds new Neuron.
     *
     * @return new Neuron GlobalInnovation number
     */
    int addNeuron(int oin, int oout, int ogeneration, Net ooriginalNet) {
        InnovationNeuronItem template = new InnovationNeuronItem(oin, oout);
        InnovationNeuronItem tmp = neuronInnov.get(template);
        if (tmp != null) { // Neurons oin and oout were already connected in history
            if (RND.getDouble() < NEAT.getConfig().globalNeuronInnovationAcceptNewRatio) { // add a new version
                tmp.insertVersion(++neuronInnovation, ogeneration);
                return neuronInnovation;
            } else { // we'll try to chose some older version of this connection
                int rndVersion = tmp.getRandomVersion(ogeneration);
                if (ooriginalNet.testForHidOut(rndVersion)) { // do not add version which is already in the net
                    tmp.insertVersion(++neuronInnovation, ogeneration);
                    return neuronInnovation;
                }
                return rndVersion;
            }
        }
        InnovationNeuronItem tmp2 = new InnovationNeuronItem(++neuronInnovation, oin, oout, ogeneration);
        neuronInnov.put(tmp2, tmp2);
//        stats.incrementNewInnovationGNI(ogeneration);
        return neuronInnovation; //introduce new innovation
    }

    /**
     * Checks if the innovation is really new.
     *
     * @param oin  input Neuron id
     * @param oout output Neuron id
     * @return number of Link innovation if it already exists, otherwise <i>FREE</i>
     * @see #FREE
     */
    int checkLink(int oin, int oout) {
        Iterator it = linkInnov.iterator();
        InnovationLinkItem ii;
        while (it.hasNext()) {
            ii = (InnovationLinkItem) it.next();
            if ((ii.in == oin) && (ii.out == oout)) {
//        System.out.println( "  innovation already used: " + ii.linkInnovation );
//                stats.incrementReusedGLI(generation);
                return ii.linkInnovation;
            }
        }
        return GlobalInnovation.FREE;
    }

    /**
     * Cleans the whole history. Note, the innovation matrix is not reallocated.
     * The maximum possible neuronInnovatiion is given in constructor.
     */
    void cleanHistory() {
        linkInnov.clear();
        neuronInnov.clear();
//        System.out.println("  linkInnov = " + linkInnov.size());
    }

    /**
     * Returns the number of stored Link innovation records.
     *
     * @return number of stored innovation records
     */
    public int getNumOfRecords() {
        return linkInnov.size();
    }

    public String toString() {
        String t = "INNOVS:" + getNumOfRecords() + " LI:" + linkInnovation + " NI:" + neuronInnovation;
        Iterator it = linkInnov.iterator();
        InnovationLinkItem ii;
        while (it.hasNext()) {
            ii = (InnovationLinkItem) it.next();
            t += "\n " + ii;
        }
        return t;
    }
}