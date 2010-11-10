package gpat;

import common.RND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 10, 2010
 * Time: 12:32:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATSpecies {
    final private int id;
    private ATForest representative;
    final private List<ATForest> members;
    private double averageFitness;
    private int estimatedOffspring;
    private int reproductionThreshold;

    public ATSpecies(int id) {
        this.id = id;
        members = new ArrayList<ATForest>();
    }

    public ATForest getRepresentative() {
        return representative;
    }

    public void setRepresentative(ATForest forest) {
        representative = forest;
    }

    public int getSize() {
        return members.size();
    }

    public double getAverageFitness() {
        return averageFitness;
    }

    public int getEstimatedOffspring() {
        return estimatedOffspring;
    }

    public void setEstimatedOffspring(int estimatedOffspring) {
        this.estimatedOffspring = estimatedOffspring;
    }

    public void addMember(ATForest forest) {
        members.add(forest);
    }

    ATForest getRandomMember() {
        return members.get(RND.getInt(0, reproductionThreshold));
    }

    public void resetSpecies() {
        representative = RND.randomChoice(members);
        members.clear();
    }

    public void sort() {
        Collections.sort(members);
    }

    public void computeAverageFitness() {
        averageFitness = 0.0;
        for (ATForest member : members) {
            averageFitness += member.getFitness();
        }
        averageFitness /= members.size();
    }

    void markForReproduction() {
        /** TODO Maybe let reproduce at least one */
        reproductionThreshold = (int) (GPAT.SPECIES_REPRODUCTION_RATIO * members.size()) - 1;
        if (reproductionThreshold < 0)
            reproductionThreshold = 0;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer("SPECIES: ");
        b.append(id).append(" SIZE: ").append(members.size());
        b.append(" AVG: ").append(averageFitness);
        b.append(" EST: ").append(estimatedOffspring);
        return b.toString();
    }
}
