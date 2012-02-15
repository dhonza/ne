package hyper.experiments.octopusArm;

import common.net.INet;
import hyper.experiments.octopusArm.model.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 15.02.12
 * Time: 12:24
 * This class is based on the Brian Woolley's LidarOnAllSegments2_GE class.
 */
public class INetAgentAdapter implements Agent {
    private static final int SEGMENT_COUNT = OctopusArm.getSegmentCount();
    private static final int MUSCLE_COUNT = (SEGMENT_COUNT) * 3;

    private final RangeSensorArray f_targetSensor;
    private static final double MAX_RANGE = OctopusArm.getRangeSensorMax();
    private static final int RESOLUTION = OctopusArm.getRangeSensorResolution();

    private INet hyperNet;
    int fitness = 0;

    public INetAgentAdapter(INet hyperNet) {
        this.hyperNet = hyperNet;
        f_targetSensor = new LiDAR(RESOLUTION, MAX_RANGE, 2 * Math.PI);
    }

    public Action genAction(ArmState state) {
        int index;
        double[] scanResult;
        double[] stimulus = new double[SEGMENT_COUNT * RESOLUTION];

        // Calculate the position and orientation of the point (alpha) between the dorsal
        //  and ventral nodes and collect the scanResult for each segment in the arm
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            // Activate the target sensor at the arm's i'th segment
            scanResult = f_targetSensor.scan(state, (i + 1));

            // Fill the stimulus array with the current scan results
            index = i * RESOLUTION;
            for (int j = 0; j < RESOLUTION; j++) {
                stimulus[index + j] = scanResult[j];
            }
        }

        // Activate the ANN to set the muscle contractions in the next time-step

        hyperNet.initSetBias();//TODO not needed here
        hyperNet.loadInputs(stimulus);
        hyperNet.reset();
        for (int i = 0; i < OctopusArm.getActivations(); i++) {
            hyperNet.activate();
        }
        double nextState[] = hyperNet.getOutputs();

        Action newAction = new Action();
        for (int i = 0, seg = 1; i < MUSCLE_COUNT; i += 3, seg++) {
            newAction.setDorsalMuscleContraction(seg, nextState[i]);
            newAction.setTransverseMuscleContraction(seg, nextState[i + 1]);
            newAction.setVentralMuscleContraction(seg, nextState[i + 2]);
        }
        return newAction;
    }

    public void addFitnessValue(int additionalFitness) {
        fitness += additionalFitness;
    }

    public String getID() {
        return "NA";
    }

    public int getFitness() {
        return fitness;
    }
}
