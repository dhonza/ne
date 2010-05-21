package sneat.neatgenome;

import sneat.evolution.IIdGeneratorFactory;
import sneat.evolution.IdGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdGeneratorFactory implements IIdGeneratorFactory {
    /// <summary>
    /// Create an IdGeneratoy by interrogating the provided population of Genomes.
    /// This routine also fixes any duplicate IDs that are found in the
    /// population.
    /// </summary>
    /// <param name="pop"></param>
    /// <returns></returns>
    public IdGenerator CreateIdGenerator(List<NeatGenome> genomeList) {
        int maxGenomeId = 0;
        int maxInnovationId = 0;

        // First pass: Determine the current maximum genomeId and innovationId.
        for (NeatGenome genome : genomeList) {
            if (genome.getGenomeId() > maxGenomeId)
                maxGenomeId = genome.getGenomeId();

            // Neuron IDs actualy come from the innovation IDs generator, so although they
            // aren't used as historical markers we should count them as innovation IDs here.
            for (NeuronGene neuronGene : genome.getNeuronGeneList().getList()) {
                if (neuronGene.getInnovationId() > maxInnovationId)
                    maxInnovationId = neuronGene.getInnovationId();
            }

            for (ConnectionGene connectionGene : genome.getConnectionGeneList().getList()) {
                if (connectionGene.getInnovationId() > maxInnovationId)
                    maxInnovationId = connectionGene.getInnovationId();
            }
        }

        if (maxGenomeId == Integer.MAX_VALUE) {     //reset to zero.
            maxGenomeId = 0;
        } else {    // Increment to next available ID.
            maxGenomeId++;
        }

        if (maxInnovationId == Integer.MAX_VALUE) {     //reset to zero.
            maxInnovationId = 0;
        } else {    // Increment to next available ID.
            maxInnovationId++;
        }

        // Create an IdGenerator using the discovered maximum IDs.
        IdGenerator idGenerator = new IdGenerator(maxGenomeId, maxInnovationId);

        // Second pass: Check for duplicate genome IDs.
        Set<Integer> genomeIdTable = new HashSet<Integer>();
        //TODO X nasldeujici radek je asi k nicemu
//        Hashtable innovationIdTable = new Hashtable();

        for (NeatGenome genome : genomeList) {
            if (genomeIdTable.contains(genome.getGenomeId())) {    // Assign this genome a new Id.
                genome.setGenomeId(idGenerator.getNextGenomeId());
            }
            //Register the ID.
            genomeIdTable.add(genome.getGenomeId());
        }

        return idGenerator;
    }


    /// <summary>
    /// Create an IdGeneratoy by interrogating the provided Genome.
    /// </summary>
    /// <param name="pop"></param>
    /// <returns></returns>
    public IdGenerator CreateIdGenerator(NeatGenome genome) {
        int maxGenomeId = 0;
        int maxInnovationId = 0;

        // First pass: Determine the current maximum genomeId and innovationId.
        if (genome.getGenomeId() > maxGenomeId)
            maxGenomeId = genome.getGenomeId();

        // Neuron IDs actualy come from the innovation IDs generator, so although they
        // aren't used as historical markers we should count them as innovation IDs here.
        for (NeuronGene neuronGene : genome.getNeuronGeneList().getList()) {
            if (neuronGene.getInnovationId() > maxInnovationId)
                maxInnovationId = neuronGene.getInnovationId();
        }

        for (ConnectionGene connectionGene : genome.getConnectionGeneList().getList()) {
            if (connectionGene.getInnovationId() > maxInnovationId)
                maxInnovationId = connectionGene.getInnovationId();
        }

        if (maxGenomeId == Integer.MAX_VALUE) {     //reset to zero.
            maxGenomeId = 0;
        } else {    // Increment to next available ID.
            maxGenomeId++;
        }

        if (maxInnovationId == Integer.MAX_VALUE) {     //reset to zero.
            maxInnovationId = 0;
        } else {    // Increment to next available ID.
            maxInnovationId++;
        }

        // Create an IdGenerator using the discovered maximum IDs.
        return new IdGenerator(maxGenomeId, maxInnovationId);
    }
}