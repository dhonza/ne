package sneat.neatgenome;

import java.util.Comparator;

/// <summary>

/// Compares the innovation ID of NeuronGenes.
/// </summary>
public class NeuronGeneComparer implements Comparator<NeuronGene> {

    public int compare(NeuronGene x, NeuronGene y) {
        // Test the most likely cases first.
        if (x.getInnovationId() < y.getInnovationId())
            return -1;
        else if (x.getInnovationId() > y.getInnovationId())
            return 1;
        else
            return 0;
    }

}
