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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubstrateInterLayerConnection that = (SubstrateInterLayerConnection) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}
