package sneat.neatgenome;

public class ConnectionGene {
    private int innovationId;
    private int sourceNeuronId;
    private int targetNeuronId;
    //		bool	enabled;
    private double weight;
    private boolean fixedWeight = false;

    /// <summary>
    /// Used by the connection mutation routine to flag mutated connections so that they aren't
    /// mutated more than once.
    /// </summary>
    private boolean isMutated = false;

    /// <summary>
    /// Copy constructor.
    /// </summary>
    /// <param name="copyFrom"></param>
    public ConnectionGene(ConnectionGene copyFrom) {
        this.innovationId = copyFrom.innovationId;
        this.sourceNeuronId = copyFrom.sourceNeuronId;
        this.targetNeuronId = copyFrom.targetNeuronId;
//			this.enabled = copyFrom.enabled;
        this.weight = copyFrom.weight;
        this.fixedWeight = copyFrom.fixedWeight;
    }

    public ConnectionGene(int innovationId, int sourceNeuronId, int targetNeuronId, double weight) {
        this.innovationId = innovationId;
        this.sourceNeuronId = sourceNeuronId;
        this.targetNeuronId = targetNeuronId;
//			this.enabled = enabled;
        this.weight = weight;
    }

    public int getInnovationId() {
        return innovationId;
    }

    public void setInnovationId(int innovationId) {
        this.innovationId = innovationId;
    }

    public int getSourceNeuronId() {
        return sourceNeuronId;
    }

    public void setSourceNeuronId(int sourceNeuronId) {
        this.sourceNeuronId = sourceNeuronId;
    }

    public int getTargetNeuronId() {
        return targetNeuronId;
    }

    public void setTargetNeuronId(int targetNeuronId) {
        this.targetNeuronId = targetNeuronId;
    }

//		public bool	Enabled
//		{
//			get
//			{
//				return enabled;
//			}
//			set
//			{
//				enabled = value;
//			}
//		}


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isFixedWeight() {
        return fixedWeight;
    }

    public void setFixedWeight(boolean fixedWeight) {
        this.fixedWeight = fixedWeight;
    }

    public boolean isMutated() {
        return isMutated;
    }

    public void setMutated(boolean mutated) {
        isMutated = mutated;
    }
}
