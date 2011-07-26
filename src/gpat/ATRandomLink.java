package gpat;

import common.RND;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 2/23/11
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class ATRandomLink {
    public static class FreeLink {
        final private int terminalId;
        final private ATNode to;

        public FreeLink(int terminalId, ATNode to) {
            this.terminalId = terminalId;
            this.to = to;
        }

        public int getTerminalId() {
            return terminalId;
        }

        public ATNode getTo() {
            return to;
        }
    }

    public static FreeLink getFreeRandomLink(ATNodeCollection nodeCollection, List<ATNode> nodeGeneList) {
        List<FreeLink> freeLinks = new ArrayList<FreeLink>();
        //for each function node
        for (ATNode node : nodeGeneList) {
            //count how many times it is connected from each kind of terminal
            int[] terminalCounters = new int[nodeCollection.terminals.length];
            for (ATNode child : node.children) {
                if (child.isTerminal()) {
                    terminalCounters[child.getId()]++;
                }
            }
            //now check if it is still possible to connect this terminal again
            for (int i = 0; i < terminalCounters.length; i++) {
                if (nodeCollection.isInputById(i)) {//inputs, ...
                    if (terminalCounters[i] < node.repeatInput()) {
                        freeLinks.add(new FreeLink(i, node));
                    }
                } else {//constants
                    if (terminalCounters[i] < node.repeatConstant()) {
                        freeLinks.add(new FreeLink(i, node));
                    }
                }

            }


        }
        if (freeLinks.size() == 0) {
            return null;
        }
        return RND.randomChoice(freeLinks);
    }
}
