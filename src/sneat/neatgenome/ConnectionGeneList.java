package sneat.neatgenome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionGeneList {
    static ConnectionGeneComparer connectionGeneComparer = new ConnectionGeneComparer();
//public bool OrderInvalidated=false;

    List<ConnectionGene> l;

/// <summary>

    /// Default constructor.
/// </summary>
    public ConnectionGeneList() {
        l = new ArrayList<ConnectionGene>();
    }

    public ConnectionGeneList(int count) {
        l = new ArrayList<ConnectionGene>((int) (count * 1.5));
    }

/// <summary>
/// Copy constructor.

    /// </summary>
/// <param name="copyFrom"></param>
    public ConnectionGeneList(ConnectionGeneList copyFrom) {
        int count = copyFrom.size();
        l = new ArrayList<ConnectionGene>(count);
        for (int i = 0; i < count; i++)
            this.add(new ConnectionGene(copyFrom.get(i)));
    }

    public void add(ConnectionGene connectionGene) {
        l.add(connectionGene);
    }

    public ConnectionGene get(int i) {
        return l.get(i);
    }

    public int size() {
        return l.size();
    }

    public List<ConnectionGene> getList() {
        return Collections.unmodifiableList(l);
    }

/// <summary>
/// Inserts a ConnectionGene into its correct (sorted) location within the gene list.
/// Normally connection genes can safely be assumed to have a new Innovation ID higher
/// than all existing ID's, and so we can just call Add().

    /// This routine handles genes with older ID's that need placing correctly.
/// </summary>
/// <param name="connectionGene"></param>
/// <returns></returns>
    public void insertIntoPosition(ConnectionGene connectionGene) {
        // Determine the insert idx with a linear search, starting from the end
        // since mostly we expect to be adding genes that belong only 1 or 2 genes
        // from the end at most.
        int idx = l.size() - 1;
        for (; idx > -1; idx--) {
            if (l.get(idx).getInnovationId() < connectionGene.getInnovationId()) {    // Insert idx found.
                break;
            }
        }
        l.add(idx + 1, connectionGene);
    }

    public void remove(ConnectionGene connectionGene) {
        remove(connectionGene.getInnovationId());

        // This invokes a linear search. Invoke our binary search instead.
        //InnerList.Remove(connectionGene);
    }

    public void remove(int idx) {
        l.remove(idx);
    }

    public void removeInnovation(int innovationId) {
        System.out.println("WARNING: originally RemoveAt");
        int idx = binarySearch(innovationId);
        if (idx < 0)
            throw new IllegalStateException("Attempt to remove connection with an unknown innovationId");
        else
            l.remove(idx);
    }

    public void sortByInnovationId() {
        Collections.sort(l, connectionGeneComparer);
//OrderInvalidated=false;
    }

    public int binarySearch(int innovationId) {
        int lo = 0;
        int hi = l.size() - 1;

        while (lo <= hi) {
            int i = (lo + hi) >> 1;
            int c = (l.get(i)).getInnovationId() - innovationId;
            if (c == 0) return i;

            if (c < 0)
                lo = i + 1;
            else
                hi = i - 1;
        }

        return ~lo;
    }

/// <summary>
/// For debug purposes only. Don't call this in normal circumstances as it is an

    /// expensive O(n) operation.
/// </summary>
/// <returns></returns>
    public boolean isSorted() {
        int prevId = 0;
        for (ConnectionGene gene : l) {
            if (gene.getInnovationId() < prevId)
                return false;
            prevId = gene.getInnovationId();
        }
        return true;
    }
}
