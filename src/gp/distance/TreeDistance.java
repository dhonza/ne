package gp.distance;

import common.evolution.IDistance;
import gp.INode;
import gp.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 9/12/11
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeDistance implements IDistance<Tree> {
    public double distance(Tree a, Tree b) {
        Set<Long> innovationsA = new HashSet<Long>();
        populateInnovations(innovationsA, a.getRoot());
        Set<Long> innovationsB = new HashSet<Long>();
        populateInnovations(innovationsB, b.getRoot());
        double mean = (innovationsA.size() + innovationsB.size()) / 2.0;
        innovationsA.retainAll(innovationsB);
        return 1.0 - (double) innovationsA.size() / mean;
    }

    private void populateInnovations(Set<Long> innovationList, INode startNode) {
        innovationList.add(startNode.getInnovation());
        for (INode child : startNode.getChildren()) {
            populateInnovations(innovationList, child);
        }
    }
}
