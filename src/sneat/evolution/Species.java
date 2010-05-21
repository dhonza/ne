package sneat.evolution;

import java.util.ArrayList;
import java.util.List;

public class Species {
    public long speciesAge = 0;
    public long ageAtLastImprovement = 0;
    public double maxFitnessEver = 0.0;

    public int speciesId = -1;
    public ArrayList<IGenome> members = new ArrayList<IGenome>();
    public double totalFitness;
    public double meanFitness;

    /// <summary>
    /// The target size for this species, as determined by the fitness sharing technique.
    /// </summary>
    public int targetSize;

    /// <summary>
    /// The number of orgainisms that are elite and should not be culled.
    /// </summary>
    public int elitistSize;

    /// <summary>
    /// The number of top scoring genomes we can should select from.
    /// </summary>
    public int selectionCount;

    /// <summary>
    /// The total fitness of all of the genomes that can be selected from.
    /// </summary>
    public double selectionCountTotalFitness;

    public int totalNeuronCount;
    public int totalConnectionCount;

    /// <summary>
    /// TotalNeuronCount + TotalConnectionCount.
    /// </summary>
    public int totalStructureCount;

    /// <summary>
    /// Indicates that this species is a candidate for species culling. This will normally occur when the
    /// species has not improved for a number of generations.
    /// </summary>
    public boolean cullCandidateFlag = false;

    public List<IGenome> getMembers() {
        return members;
    }

    public void resetFitnessValues() {
        totalFitness = 0;
        meanFitness = 0;
    }
}
