package sneat.evolution.neatparameters;

public class ConnectionMutationParameterGroup {
    /// <summary>
    /// This group's activation proportion - relative to the totalled
    /// ActivationProportion for all groups.
    /// </summary>
    public double activationProportion;

    /// <summary>
    /// The type of mutation that this group represents.
    /// </summary>
    public ConnectionPerturbationType perturbationType;

    /// <summary>
    /// The type of connection selection that this group represents.
    /// </summary>
    public ConnectionSelectionType selectionType;

    /// <summary>
    /// Specifies the proportion for SelectionType.Proportional
    /// </summary>
    public double Proportion;

    /// <summary>
    /// Specifies the quantity for SelectionType.FixedQuantity
    /// </summary>
    public int Quantity;

    /// <summary>
    /// The perturbation factor for ConnectionPerturbationType.JiggleEven.
    /// </summary>
    public double PerturbationFactor;

    /// <summary>
    /// Sigma for for ConnectionPerturbationType.JiggleND.
    /// </summary>
    public double Sigma;

    public ConnectionMutationParameterGroup(double activationProportion,
                                            ConnectionPerturbationType perturbationType,
                                            ConnectionSelectionType selectionType,
                                            double proportion,
                                            int quantity,
                                            double perturbationFactor,
                                            double sigma) {
        this.activationProportion = activationProportion;
        this.perturbationType = perturbationType;
        this.selectionType = selectionType;
        Proportion = proportion;
        Quantity = quantity;
        PerturbationFactor = perturbationFactor;
        Sigma = sigma;
    }

    /// <summary>
    /// Copy constructor.
    /// </summary>
    /// <param name="copyFrom"></param>
    public ConnectionMutationParameterGroup(ConnectionMutationParameterGroup copyFrom) {
        activationProportion = copyFrom.activationProportion;
        perturbationType = copyFrom.perturbationType;
        selectionType = copyFrom.selectionType;
        Proportion = copyFrom.Proportion;
        Quantity = copyFrom.Quantity;
        PerturbationFactor = copyFrom.PerturbationFactor;
        Sigma = copyFrom.Sigma;
    }


//		public void Mutate(double pValueJiggle)
//		{
//			// Determine which parameter to mutate.
//			int possibleOutcomes=2;
//			if(PerturbationType!=ConnectionPerturbationType.Reset)
//				possibleOutcomes++;
//
//			int outcome = RouletteWheel.singleThrowEven(possibleOutcomes);
//			bool resetOnly=(Utilities.nextDouble() < pValueJiggle);
//			
//			switch(outcome)
//			{
//				case 0: // ActivationProportion.
//				{
//					if(resetOnly)
//					{
//						ActivationProportion = Utilities.nextDouble();
//					}
//					else
//					{
//
//					}
//				}
//				case 1:	// In scope SelectionType parameter.
//				{
//					if(resetOnly)
//					{
//						switch(SelectionType)
//						{
//							case ConnectionSelectionType.FixedQuantity:
//								Quantity
//							case ConnectionSelectionType.Proportional:
//								Proportion
//
//						}
//					}
//					else
//					{
//
//					}
//				}
//				case 2:	// In scope PerturbationType parameter.
//				{
//					if(resetOnly)
//					{
//
//					}
//					else
//					{
//
//					}
//				}
//			}
//		}

}
