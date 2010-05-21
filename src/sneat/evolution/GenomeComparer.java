package sneat.evolution;

import java.util.Comparator;

/// <summary>
/// Sort by Fitness(Descending). Genomes with like fitness are then sorted by genome size(Ascending).

/// This means the selection routines are more likely to select the fit AND the smallest first.
/// </summary>
public class GenomeComparer implements Comparator<IGenome> {
    public int compare(IGenome x, IGenome y) {
        double fitnessDelta = y.getFitness() - x.getFitness();
        if (fitnessDelta < 0.0D)
            return -1;
        else if (fitnessDelta > 0.0D)
            return 1;

        long ageDelta = x.getGenomeAge() - y.getGenomeAge();

        // Convert result to an int.
        if (ageDelta < 0)
            return -1;
        else if (ageDelta > 0)
            return 1;

        return 0;
    }
}
