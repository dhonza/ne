package sneat.evolution;

public class IdGenerator {
    int nextGenomeId;
    int nextInnovationId;

    public IdGenerator() {
        this.nextGenomeId = 0;
        this.nextInnovationId = 0;
    }

    public IdGenerator(int nextGenomeId, int nextInnovationId) {
        this.nextGenomeId = nextGenomeId;
        this.nextInnovationId = nextInnovationId;
    }

    public int getNextGenomeId() {
        if (nextGenomeId == Integer.MAX_VALUE)
            nextGenomeId = 0;
        return nextGenomeId++;
    }

    public int getNextInnovationId()

    {
        if (nextInnovationId == Integer.MAX_VALUE)
            nextInnovationId = 0;
        return nextInnovationId++;
    }

    /// <summary>
    /// Used primarilty by the GenomeFactory so that the same innovation ID's are used for input & output nodes
    /// for all of the initial population.
    /// </summary>
    public void resetNextInnovationNumber() {
        nextInnovationId = 0;
    }
}
