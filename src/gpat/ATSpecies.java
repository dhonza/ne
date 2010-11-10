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

    public void addMember(ATForest forest) {
        members.add(forest);
    }

    public void resetSpecies() {
        representative = RND.randomChoice(members);
        members.clear();
    }

    public void sort() {
        Collections.sort(members);
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer("SPECIES: ");
        b.append(id).append(" SIZE: ").append(members.size());
        return b.toString();
    }
}
