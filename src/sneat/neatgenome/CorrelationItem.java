package sneat.neatgenome;

public class CorrelationItem {
    CorrelationItemType correlationItemType;
    ConnectionGene connectionGene1;
    ConnectionGene connectionGene2;

    public CorrelationItem(CorrelationItemType correlationItemType, ConnectionGene connectionGene1, ConnectionGene connectionGene2) {
        this.correlationItemType = correlationItemType;
        this.connectionGene1 = connectionGene1;
        this.connectionGene2 = connectionGene2;
    }

    public CorrelationItemType getCorrelationItemType() {
        return correlationItemType;
    }

    public void setCorrelationItemType(CorrelationItemType correlationItemType) {
        this.correlationItemType = correlationItemType;
    }

    public ConnectionGene getConnectionGene1() {
        return connectionGene1;
    }

    public void setConnectionGene1(ConnectionGene connectionGene1) {
        this.connectionGene1 = connectionGene1;
    }

    public ConnectionGene getConnectionGene2() {
        return connectionGene2;
    }

    public void setConnectionGene2(ConnectionGene connectionGene2) {
        this.connectionGene2 = connectionGene2;
    }
}
