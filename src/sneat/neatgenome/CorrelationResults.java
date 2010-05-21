package sneat.neatgenome;

import java.util.ArrayList;
import java.util.List;

public class CorrelationResults {
    CorrelationStatistics correlationStatistics = new CorrelationStatistics();
    List<CorrelationItem> correlationItemList = new ArrayList<CorrelationItem>();

    public CorrelationStatistics getCorrelationStatistics() {
        return correlationStatistics;
    }

    public List<CorrelationItem> getCorrelationItemList() {
        return correlationItemList;
    }

    public boolean performIntegrityCheck() {
        long prevInnovationId = -1;

        for (CorrelationItem correlationItem : correlationItemList) {
            if (correlationItem.correlationItemType == CorrelationItemType.MatchedConnectionGenes) {
                if (correlationItem.connectionGene1 == null || correlationItem.connectionGene2 == null)
                    return false;

                if ((correlationItem.connectionGene1.getInnovationId() != correlationItem.connectionGene2.getInnovationId())
                        || (correlationItem.connectionGene1.getSourceNeuronId() != correlationItem.connectionGene2.getSourceNeuronId())
                        || (correlationItem.connectionGene1.getTargetNeuronId() != correlationItem.connectionGene2.getTargetNeuronId()))
                    return false;

                // Innovation ID's should be in order and not duplicated.
                if (correlationItem.connectionGene1.getInnovationId() <= prevInnovationId)
                    return false;

                prevInnovationId = correlationItem.connectionGene1.getInnovationId();
            } else // Disjoint or excess gene.
            {
                if ((correlationItem.connectionGene1 == null && correlationItem.connectionGene2 == null)
                        || (correlationItem.connectionGene1 != null && correlationItem.connectionGene2 != null)) {    // Precisely one gene should be present.
                    return false;
                }
                if (correlationItem.connectionGene1 != null) {
                    if (correlationItem.connectionGene1.getInnovationId() <= prevInnovationId)
                        return false;

                    prevInnovationId = correlationItem.connectionGene1.getInnovationId();
                } else // ConnectionGene2 is present.
                {
                    if (correlationItem.connectionGene2.getInnovationId() <= prevInnovationId)
                        return false;

                    prevInnovationId = correlationItem.connectionGene2.getInnovationId();
                }
            }
        }
        return true;
    }
}