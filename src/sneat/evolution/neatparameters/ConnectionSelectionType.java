package sneat.evolution.neatparameters;

/// <summary>
/// Different systems of determining which connection weights will be selected

/// for mutation.
/// </summary>
public enum ConnectionSelectionType {
    /// <summary>
    /// Select a proportion of the weights in a genome.
    /// </summary>
    PROPORTIONAL,

    /// <summary>
    /// Select a fixed number of weights in a genome.
    /// </summary>
    FIXED_QUANTITY
}
