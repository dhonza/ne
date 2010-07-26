package hyper.substrate.layer;

import java.io.Serializable;

/**
 * This class is immutable.
 */
public class SubstrateInterLayerConnection implements IConnectable, Serializable {
    final private ISubstrateLayer from;
    final private ISubstrateLayer to;

    public SubstrateInterLayerConnection(ISubstrateLayer from, ISubstrateLayer to) {
        this.from = from;
        this.to = to;
    }

    public ISubstrateLayer getFrom() {
        return from;
    }

    public ISubstrateLayer getTo() {
        return to;
    }

    public int getNumOfLinks() {
        return from.getNumber() * to.getNumber();
    }
}
