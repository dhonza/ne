package sneat.experiments;

import sneat.neuralnetwork.INetwork;

abstract public class AbstractExperimentView {
    /// <summary>
    /// Refresh the view using the provided network. The intention here is that the current best network
    /// will be passed in. We can then update the view showing how well that network performs.
    /// </summary>
    /// <param name="network"></param>
    abstract public void RefreshView(INetwork network);
}
