package sneat.neatgenome;

import java.util.Comparator;

/// <summary>

/// Compares the innovation ID of ConnectionGenes.
/// </summary>
public class ConnectionGeneComparer implements Comparator<ConnectionGene> {
    public int compare(ConnectionGene x, ConnectionGene y) {
        // Test the most likely cases first.
        if ((x).getInnovationId() < (y).getInnovationId())
            return -1;
        else if ((x).getInnovationId() > (y).getInnovationId())
            return 1;
        else
            return 0;
    }
}
