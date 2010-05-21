package hyper.substrate.layer;

/**
 * This class is immutable.
 */
public class SubstrateInterLayerConnection implements Connectable {
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
}
