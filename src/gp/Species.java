package gp;

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
public class Species {
    final private int id;
    private Forest representative;
    final private List<Forest> members;
    private double averageFitness;
    private int estimatedOffspring;
    private int reproductionThreshold;
    private int elitistSize;

    public Species(int id) {
        this.id = id;
        members = new ArrayList<Forest>();
    }

    public Forest getRepresentative() {
        return representative;
    }

    public void setRepresentative(Forest forest) {
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

    public int getElitistSize() {
        return elitistSize;
    }

    public void setElitistSize(int elitistSize) {
        this.elitistSize = elitistSize;
    }

    public void addMember(Forest forest) {
        members.add(forest);
    }

    Forest getMember(int idx) {
        return members.get(idx);
    }

    void setMember(int idx, Forest forest) {
        members.set(idx, forest);
    }

    Forest getRandomMember() {
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
        for (Forest member : members) {
            averageFitness += member.getFitness();
        }
        averageFitness /= members.size();
    }

    void markForReproduction() {
        /** TODO Maybe let reproduce at least one */
        /** why -1?: indices count from 0 */
        reproductionThreshold = (int) (GPEFS.SPECIES_REPRODUCTION_RATIO * members.size()) - 1;
        if (reproductionThreshold < 0)
            reproductionThreshold = 0;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer("SPECIES: ");
        b.append(id).append(" SIZE: ").append(members.size());
        b.append(" BST: ").append(members.get(0).getFitness());
        b.append(" AVG: ").append(averageFitness);
        b.append(" EST: ").append(estimatedOffspring);
        b.append(" ELI: ").append(elitistSize);
        return b.toString();
    }
}
