package hyper.experiments.reco.fitness;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 14, 2009
 * Time: 10:51:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class RecognitionFitness1D {
    private HyperEvaluator1D hyperEvaluator;

    public RecognitionFitness1D(HyperEvaluator1D hyperEvaluator) {
        this.hyperEvaluator = hyperEvaluator;
    }

    public double evaluate(double[][] inputPatterns, double[][] outputPatterns) {
        hyperEvaluator.init();

        double error = 0.0;

        for (int i = 0; i < inputPatterns.length; i++) {
            hyperEvaluator.loadPatternToInputs(inputPatterns[i]);
            hyperEvaluator.activate();
            double[] outputs = hyperEvaluator.getOutputs();
            if (outputs.length != outputPatterns[0].length) {
                throw new IllegalStateException("HyperNet outputs do not match output pattern.");
            }
            for (int j = 0; j < outputPatterns[0].length; j++) {
//                double output = outputs[j] > 0.5 ? 1.0 : 0.0;
                double output = outputs[j];
                double diff = output - outputPatterns[i][j];
                error += diff * diff;
            }

        }
        return inputPatterns.length * outputPatterns[0].length - error;
    }

    public double[] propagate(double[] pattern) {
        hyperEvaluator.init();
        hyperEvaluator.loadPatternToInputs(pattern);
        hyperEvaluator.activate();
        return hyperEvaluator.getOutputs();
    }
}