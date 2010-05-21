package sneat.evolution;

import sneat.neatgenome.NeatGenome;

import java.util.List;

public interface IIdGeneratorFactory {
    /// <summary>
    /// Create an IdGenerator based upon the IDs within the provided population.
    /// </summary>
    /// <param name="pop"></param>
    /// <returns></returns>
    IdGenerator CreateIdGenerator(List<NeatGenome> genomeList);
}

