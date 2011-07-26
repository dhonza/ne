package gpat;

import common.RND;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 7/26/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATRandomLeaf {
    public static class FreeLeaf {
        final private int terminalIdx;//index of an affected incoming connection to "to" node
        final private int targetTerminalId;//the terminal type will be changed to terminal with this id
        final private ATNode to;//affected node

        public FreeLeaf(int terminalIdx, int targetTerminalId, ATNode to) {
            this.terminalIdx = terminalIdx;
            this.targetTerminalId = targetTerminalId;
            this.to = to;
        }

        public int getTerminalIdx() {
            return terminalIdx;
        }

        public int getTargetTerminalId() {
            return targetTerminalId;
        }

        public ATNode getTo() {
            return to;
        }

        @Override
        public String toString() {
            return "FreeLeaf{" +
                    "terminalIdx=" + terminalIdx +
                    ", targetTerminalId=" + targetTerminalId +
                    ", to=" + to +
                    '}';
        }
    }

    public static FreeLeaf getFreeRandomLink(ATNodeCollection nodeCollection, List<ATNode> nodeGeneList) {
        List<FreeLeaf> freeLeaves = new ArrayList<FreeLeaf>();
        //for each function node
        for (ATNode node : nodeGeneList) {
            //count how many times it is connected from each kind of a terminal
            int[] terminalCounters = new int[nodeCollection.terminals.length];
            for (ATNode child : node.children) {
                if (child.isTerminal()) {
                    terminalCounters[child.getId()]++;
                }
            }

            for (int i = 0, childrenSize = node.children.size(); i < childrenSize; i++) {
                ATNode child = node.children.get(i);//We try all possibilities of changing "child" at index "i"
                if (!child.isTerminal()) {
                    continue;
                }
                for (int j = 0; j < terminalCounters.length; j++) {//to terminal at index "j".
                    if (child.getId() != j) {//Skip if no change would happen.
                        if (nodeCollection.isInputById(j)) {//inputs, ...
                            if (terminalCounters[j] < node.repeatInput()) {
                                freeLeaves.add(new FreeLeaf(i, j, node));
                            }
                        } else {//constants
                            if (terminalCounters[j] < node.repeatConstant()) {
                                freeLeaves.add(new FreeLeaf(i, j, node));
                            }
                        }
                    }
                }
            }
        }

        if (freeLeaves.size() == 0) {
            return null;
        }

        if (freeLeaves.size() > 5) {
            System.out.println("HEEERE");
        }
        return RND.randomChoice(freeLeaves);
    }
}
