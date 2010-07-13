package hyper.experiments.findcluster2;

import common.evolution.EvaluationInfo;
import common.mathematica.MathematicaUtils;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.Problem;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.Substrate;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 13, 2010
 * Time: 10:31:26 AM
 * Adapted from  Colin Green's SharpNEAT 2 HyperNEAT (Box Discrimination Task)
 * TODO Differs in substrate type uses 2D coordinates instead of 3D
 */
public class FindCluster2 implements Problem {
    private static class TrialInfo {
        double distance;
        double activationRange;

        private TrialInfo(double distance, double activationRange) {
            this.distance = distance;
            this.activationRange = activationRange;
        }
    }

    private static class ActivationOutputInfo {
        Point point;
        double minActivation;
        double maxActivation;
        double[] outputValues;

        private ActivationOutputInfo(Point point, double minActivation, double maxActivation, double[] outputValues) {
            this.point = point;
            this.minActivation = minActivation;
            this.maxActivation = maxActivation;
            this.outputValues = outputValues;
        }
    }

    final private int activations;

    private final ReportStorage reportStorage;

    public FindCluster2(ParameterCombination parameters, ReportStorage reportStorage) {
        visualFieldResolution = parameters.getInteger("FIND_CLUSTER2.VISUAL_FIELD_RESOLUTION");
        visualPixelSize = visualFieldEdgeLength / visualFieldResolution;
        visualOriginPixelXY = -1.0 + (visualPixelSize / 2.0);
        this.activations = parameters.getInteger("NET_ACTIVATIONS");
        this.reportStorage = reportStorage;
    }

    // Evaluate the provided INet against the problem domain and return its fitness score.
    //
    // Fitness value explanation:
    // 1) Max distance from target position in each trial is sqrt(2)*visualFieldEdgeLength (target in one corner and selected target in
    // opposite corner).
    //
    // 2) An agent is scored by squaring the distance of its selected target from the actual target, squaring the value,
    // taking the average over all test cases and then taking the square root. This is referred to as the root mean squared distance (RMSD)
    // and is effectively an  implementation of least squares (least squared error). The square root term converts values back into units
    // of distance (rather than squared distance)
    //
    // 3) An agent selecting points at random will score visualFieldEdgeLength * meanLineInSquareRootMeanSquareLength. Any agent scoring
    // this amount or less is assigned a fitness of zero. All other scores are scaled and translated into the range 0-100 where 0 is no better
    // or worse than a random agent, and 100 is perfectly selecting the correct target for all test cases (distance of zero between target and
    // selected target).
    //
    // 4)  In addition to this the range of output values is scaled to 0-10 and added to the final score, this encourages solutions with a wide
    // output range between the highest activation (the selected pixel) and the lowest activation (this encourages prominent/clear selection).
    //
    // An alternative scheme is fitness = 1/RMSD  (separately handling the special case where RMSD==0).
    // However, this gives a non-linear increase in fitness as RMSD decreases linearly, which in turns produces a 'spikier' fitness landscape
    // which is more likely to cause genomes and species to get caught in a local maximum.    

    public EvaluationInfo evaluate(INet hyperNet) {
        // Accumulate square distance from eac
        // h test case.
        double acc = 0.0;
        double activationRangeAcc = 0.0;
        TestCaseField testCaseField = new TestCaseField();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 25; j++) {
                TrialInfo trialInfo = runTrial(hyperNet, testCaseField, i);
                acc += trialInfo.distance;
                activationRangeAcc += trialInfo.activationRange;
            }
        }

        // Calc root mean squared distance (RMSD) and calculate fitness based comparison to the random agent.
        double threshold = visualFieldEdgeLength * 0.5772;
        double rmsd = Math.sqrt(acc / 75.0);
        double fitness;
        if (rmsd > threshold) {
            fitness = 0.0;
        } else {
            fitness = (((threshold - rmsd) * 100.0) / threshold) + (activationRangeAcc / 7.5);
        }

        // Set stop flag when max fitness is attained.
        if (!stopConditionSatisfied && fitness == maxFitness) {
            stopConditionSatisfied = true;
        }

        Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
        infoMap.put("RMSD", rmsd);

        return new EvaluationInfo(fitness, infoMap);
    }

    public boolean isSolved() {
        return stopConditionSatisfied;
    }

    public void show(INet hyperNet) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n{\n");

        TestCaseField testCaseField = new TestCaseField();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 25; j++) {
                showTrial(hyperNet, testCaseField, i, builder);
                if (i == 2 && j == 24) {
                    builder.append("\n}\n");
                } else {
                    builder.append("\n},\n{\n");
                }
            }
        }

        builder.append("}\n");
        reportStorage.storeSingleRunAuxiliaryFile("find_cluster2_", builder.toString());
    }

    public double getTargetFitness() {
        return maxFitness;
    }

    public Substrate getSubstrate() {
        return FindCluster2SubstrateFactory.createInputToOutput(visualFieldResolution);
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> listOfNames = new ArrayList<String>();
        listOfNames.add("RMSD");
        return listOfNames;
    }

    // Width and length of the visual field in the 'real' coordinate system that
    // substrate nodes are located within (and therefore sensor and output pixels).
    public final double visualFieldEdgeLength = 2.0;

    // The root mean square distance (rmsd) between two random points in the unit square.
    // An agent that attempts this problem domain by selecting random points will produce this value as a score
    // when the size of the visual field is 1x1 (the unit square). For other visual field sizes we can obtain the
    // random agent's score  by simply multiplying this value by the edge length of the visual field (the score scales
    // linearly with the edge length).
    //
    // This value can be derived starting with the function for the mean length of a line between two random points
    // in the unit square, as given in: http://mathworld.wolfram.com/SquareLinePicking.html
    //
    // Alternatively the value can be experimentally determined/approximated. The value here was found computationally.
    private final double meanLineInSquareRootMeanSquareLength = 0.5773;

    // Maximum fitness for this evaulator. Problem domain is considered perfectly 'solved' if this score is achieved.
    private final double maxFitness = 110.0;

    // The resoltion of the visual and output fields.
    private int visualFieldResolution;

    // The width and height of a visual field pixel in the real coordinate system.
    private double visualPixelSize;

    // The X and Y position of the visual field's origin pixel in the real coordinate system (the center position of the origin pixel).
    private double visualOriginPixelXY;

    // Indicates if some stop condition has been achieved.
    private boolean stopConditionSatisfied;

    // Apply the provided test case to the provided black box's inputs (visual input field).

    public static double[] applyVisualFieldToHyperNet(TestCaseField testCaseField, INet hyperNet, int visualFieldResolution, double visualOriginPixelXY, double visualPixelSize) {
        double[] in = new double[visualFieldResolution * visualFieldResolution];
        int inputIdx = 0;
        double yReal = visualOriginPixelXY;
        for (int y = 0; y < visualFieldResolution; y++, yReal += visualPixelSize) {
            double xReal = visualOriginPixelXY;
            for (int x = 0; x < visualFieldResolution; x++, xReal += visualPixelSize, inputIdx++) {
                in[inputIdx] = testCaseField.getPixel(xReal, yReal);
            }
        }

        if (hyperNet.getNumInputs() != in.length) {
            System.out.println("Probably a bias indexation problem!!!");
            System.exit(1);
        }

        hyperNet.loadInputsNotBias(in);
        hyperNet.initSetBias();//TODO zbytecne - prozkoumat a zdokumentovat, co se v tech sitich deje?
        hyperNet.loadInputsNotBias(in);
//        hyperNet.loadInputs(in);
        return in;
    }

    // Determine the coordinate of the pixel with the highest activation.

    public static ActivationOutputInfo findMaxActivationOutput(INet hyperNet, int visualFieldResolution) {
        double minActivation = hyperNet.getOutputValues()[0];
        double maxActivation = minActivation;

        int maxOutputIdx = 0;

        int len = hyperNet.getOutputValues().length;
        for (int i = 1; i < len; i++) {
            double val = hyperNet.getOutputValues()[i];

            if (val > maxActivation) {
                maxActivation = val;
                maxOutputIdx = i;
            } else if (val < minActivation) {
                minActivation = val;
            }
        }

        int y = maxOutputIdx / visualFieldResolution;
        int x = maxOutputIdx - (y * visualFieldResolution);
        return new ActivationOutputInfo(new Point(x, y), minActivation, maxActivation, hyperNet.getOutputValues());
    }


    // Run a single trial
    // 1) Generate random test case with the box orientation specified by largeBoxRelativePos.
    // 2) Apply test case visual field to black box inputs.
    // 3) Activate black box.
    // 4) Determine black box output with highest output, this is the selected pixel.
    //
    // Returns square of distance between target pixel (center of large box) and pixel selected by the black box.

    private TrialInfo runTrial(INet hyperNet, TestCaseField testCaseField, int largeBoxRelativePos) {
        // Generate random test case. Also gets the center position of the large box.
        Point targetPos = testCaseField.initTestCase(0);

//        System.out.println(targetPos);
//        printTestCase(testCaseField);

        // Apply test case visual field to black box inputs.
        applyVisualFieldToHyperNet(testCaseField, hyperNet, visualFieldResolution, visualOriginPixelXY, visualPixelSize);

        // Clear any pre-existign state and activate.
        hyperNet.reset();
        for (int i = 0; i < activations; i++) {
            hyperNet.activate();
        }
//        if (!hyperNet.IsStateValid) {   // Any black box that gets itself into an invalid state is unlikely to be
//             any good, so lets just bail out here.
//            activationRange = 0.0;
//            return 0.0;
//        }

        // Find output pixel with highest activation.
        ActivationOutputInfo info = findMaxActivationOutput(hyperNet, visualFieldResolution);
        Point highestActivationPoint = info.point;
        double activationRange = Math.max(0.0, info.maxActivation - info.minActivation);

        // Get the distance between the target and activated pixels, in the real coordinate space.
        // We actually want squared distance (not distance) thus we can skip taking the square root (expensive CPU operation).
        return new TrialInfo(calcRealDistanceSquared(targetPos, highestActivationPoint), activationRange);
    }

    private void showTrial(INet hyperNet, TestCaseField testCaseField, int largeBoxRelativePos, StringBuilder builder) {
        Point targetPos = testCaseField.initTestCase(0);
        double[] in = applyVisualFieldToHyperNet(testCaseField, hyperNet, visualFieldResolution, visualOriginPixelXY, visualPixelSize);
        printMatrix(builder, in, visualFieldResolution);
        builder.append(",\n");

        hyperNet.reset();
        for (int i = 0; i < activations; i++) {
            hyperNet.activate();
        }

        ActivationOutputInfo info = findMaxActivationOutput(hyperNet, visualFieldResolution);

        printMatrix(builder, info.outputValues, visualFieldResolution);

    }

    private double calcRealDistanceSquared(Point a, Point b) {
        // We can skip calculating abs(val) because we square the values.
        double xdelta = (a.x - b.x) * visualPixelSize;
        double ydelta = (a.y - b.y) * visualPixelSize;
        return xdelta * xdelta + ydelta * ydelta;
    }

    public void printTestCase(TestCaseField testCaseField) {
        StringBuilder builder = new StringBuilder();
        int inputIdx = 0;
        double yReal = visualOriginPixelXY;
        for (int y = 0; y < visualFieldResolution; y++, yReal += visualPixelSize) {
            double xReal = visualOriginPixelXY;
            for (int x = 0; x < visualFieldResolution; x++, xReal += visualPixelSize, inputIdx++) {
                builder.append(String.format("%.0f ", testCaseField.getPixel(xReal, yReal)));
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    public static void printMatrix(StringBuilder builder, double[] m, int resolution) {
        builder.append("{{");
        for (int i = 0; i < m.length; i++) {
            if ((i + 1) % resolution != 0) {
                builder.append(MathematicaUtils.toMathematica(1 * m[i])).append(", ");
            } else {
                builder.append(MathematicaUtils.toMathematica(1 * m[i]));
            }
            if ((i + 1) % resolution == 0) {
                builder.append("}");
                if (i < m.length - 1) {
                    builder.append(",\n{");
                }
            }
        }
        builder.append("}");
    }
}
