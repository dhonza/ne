package common.net.cascade;

/**
 * Sigmoid activation function
 *
 * @author Do Minh Duc
 */
public class ActivationFunctionSigmoid implements IActivationFunction {
    private double steepness;

    public ActivationFunctionSigmoid() {
        this(4.924273);
    }

    public String getType() {
        return "Sigmoid";
    }

    public ActivationFunctionSigmoid(double steepness) {
        super();
        this.steepness = steepness;
    }

    private double calculateSigmoid(double input) {
        //if (input > 15) return 1;
        //if (input < -15) return 0;
        return (1 / (1 + Math.exp(-this.steepness * input)));
    }


    public double calculateOutput(double input) {
        return calculateSigmoid(input);
    }

    public double calculateDerivative(double input) {
        return calculateSigmoid(input) * (1 - calculateSigmoid(input));
    }

}
