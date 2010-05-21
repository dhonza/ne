package sneat.evolution;

/// <summary>
/// Used primarily as a key into a hashtable that uniquely identifies connections

/// by their end points.
/// </summary>
public class ConnectionEndpointsStruct {
    public int sourceNeuronId;
    public int targetNeuronId;

    public ConnectionEndpointsStruct() {
    }

    public ConnectionEndpointsStruct(int sourceNeuronId, int targetNeuronId) {
        this.sourceNeuronId = sourceNeuronId;
        this.targetNeuronId = targetNeuronId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionEndpointsStruct that = (ConnectionEndpointsStruct) o;

        if (sourceNeuronId != that.sourceNeuronId) return false;
        if (targetNeuronId != that.targetNeuronId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // Point uses x^y far a hash, but this is actually an extremely poor hash function
        // for a pair of coordinates. Here we swpa the low and high 16 bits of one of the
        // Id's to generate a much better hash for our (and most other likely) circumstances.
        return sourceNeuronId ^ ((targetNeuronId >> 16) + (targetNeuronId << 16));
    }
}
