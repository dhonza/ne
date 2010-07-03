package hyper.substrate.layer;

import java.io.Serializable;

/**
 * This class is immutable.
 */
public class SubstrateInterLayerConnection implements Connectable, Serializable {
    final private SubstrateLayer from;
    final private SubstrateLayer to;

    public SubstrateInterLayerConnection(SubstrateLayer from, SubstrateLayer to) {
        this.from = from;
        this.to = to;
    }

    public SubstrateLayer getFrom() {
        return from;
    }

    public SubstrateLayer getTo() {
        return to;
    }

    public int getNumOfLinks() {
        return from.getNumber() * to.getNumber();
    }
}
