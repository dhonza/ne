/**
 * Copyright (C) 2011 Brian Woolley
 *
 * This file is part of the octopusArm simulator.
 *
 * The octopusArm simulator is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *
 * created by Brian Woolley on May 6, 2010
 *
 * adapted by Jan Drchal 2012,
 * note, only LidarOnAllSegments2_GE substrate is supported
 */
package hyper.experiments.octopusArm;

import common.RND;
import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.octopusArm.fitness.AvgDistToTarget;
import hyper.experiments.octopusArm.fitness.FitnessStrategy;
import hyper.experiments.octopusArm.model.RunMetrics;
import hyper.experiments.octopusArm.model.Simulation;
import hyper.experiments.octopusArm.model.Target;
import hyper.experiments.octopusArm.model.Trial;
import hyper.experiments.octopusArm.userInterface.SimulationViewer;
import hyper.substrate.ISubstrate;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Brian Woolley
 */
public class OctopusArm implements IProblem<INet> {

    private static boolean enableDisplay;

    private final ReportStorage reportStorage;
    private static int activations;

    public OctopusArm(ParameterCombination parameters, ReportStorage reportStorage) {
        stimulusSize = parameters.getInteger(STIMULUS_SIZE_KEY, DEFAULT_STIMULUS_SIZE);
        agentBehaivor = parameters.getString(AGENT_BEHAVIOR_KEY, DEFAULT_AGENT_BEHAVIOR);
        numSegments = parameters.getInteger(NUM_SEGMENTS_KEY, DEFAULT_NUM_SEGMENTS);
        armLength = parameters.getDouble(ARM_LENGTH_KEY, DEFAULT_ARM_LENGTH);
        armWidth = parameters.getDouble(ARM_WIDTH_KEY, DEFAULT_ARM_WIDTH);
        armTaper = parameters.getDouble(ARM_TAPER_KEY, DEFAULT_ARM_TAPER);
        muscleResponse = parameters.getInteger(MUSCLE_RESPONSE_KEY, DEFAULT_MUSCLE_RESPONSE);
        muscleActivation = parameters.getInteger(MUSCLE_ACTIVATION_KEY, DEFAULT_MUSCLE_ACTIVATION);

        rangeSensorResolution = parameters.getInteger(RANGE_SENSOR_RESOLUTION_KEY, DEFAULT_RANGE_SENSOR_RESOLUTION);
        rangeSensorMax = parameters.getDouble(RANGE_SENSOR_MAX_KEY, DEFAULT_RANGE_SENSOR_MAX);
        numTrials = parameters.getInteger(NUM_TRIALS_KEY, DEFAULT_NUM_TRIALS);
        resetBetweenTrials = parameters.getBoolean(RESET_BETWEEN_TRIALS_KEY, DEFAULT_RESET_BETWEEN_TRIALS);
        resetOnArmBreak = parameters.getBoolean(RESET_ON_ARM_BREAK_KEY, DEFAULT_RESET_ON_ARM_BREAK);
        maxTimesteps = parameters.getInteger(TIMESTEPS_KEY, DEFAULT_TIMESTEPS);

        fitnessFunction = parameters.getString(FITNESS_FUNCTION_KEY, DEFAULT_FITNESS_FUNCTION);
        requireTipTouch = parameters.getBoolean(TIP_TOUCH_KEY, DEFAULT_TIP_TOUCH);
        targetReward = parameters.getInteger(TARGET_REWARD_KEY, DEFAULT_TARGET_REWARD);
        penalizeEnergyUse = parameters.getBoolean(PENALIZE_ENERGY_USE_KEY, DEFAULT_PENALIZE_ENERGY_USE);
        penalizeTimeUse = parameters.getBoolean(PENALIZE_TIME_USE_KEY, DEFAULT_PENALIZE_TIME_USE);

        gravity = parameters.getDouble(GRAVITY_KEY, DEFAULT_GRAVITY);
        waterLevel = parameters.getDouble(WATER_LEVEL_KEY, DEFAULT_WATER_LEVEL);
        buoyancy = parameters.getDouble(BUOYANCY_FORCE_KEY, DEFAULT_BUOYANCY_FORCE);

        activations = parameters.getInteger("NET_ACTIVATIONS");
        enableDisplay = parameters.getBoolean("OCTOPUS.SHOW_GRAPHICS", false);
        this.reportStorage = reportStorage;
    }

    public static int getActivations() {
        return activations;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        INetAgentAdapter adapter = new INetAgentAdapter(hyperNet);
        //changed from original Wooley's
//        Simulation simulation = new Simulation(adapter, getRandomTargetSet(getNumTrials()));
        Simulation simulation = new Simulation(adapter, getTargetSet(getNumTrials()));
        simulation.run();
        System.out.println("FITNESS = " + adapter.getFitness());
        System.out.flush();

        RunMetrics results = simulation.getRunMetrics();
        Map<String, Object> infoMap = new LinkedHashMap<String, Object>();
        infoMap.put("AVG_DISTANCE", results.getAverageDistToTarget());
        infoMap.put("TIME_ELAPSED", results.getTimeElapsed());
        infoMap.put("HITS", results.hitTargetCount());

        return new EvaluationInfo(simulation.getFitnessFunction().calculateFitness(results), infoMap);
    }

    public boolean isSolved() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void show(INet hyperNet) {
        INetAgentAdapter adapter = new INetAgentAdapter(hyperNet);
        Simulation simulation = new Simulation(adapter, getRandomTargetSet(getNumTrials()));
        if (enableDisplay) {
            simulation.enableDisplay(new SimulationViewer());
        }
        simulation.run();

        String agentID = simulation.getAgent().getID();
        RunMetrics results = simulation.getRunMetrics();
        DecimalFormat df = new DecimalFormat("###,###.##");

        StringBuffer runSummary = new StringBuffer("\n --------Simulation results for chromosome " + agentID + ".--------\n");

        int idx = 0;
        for (Trial t : results.getTrials()) {
            runSummary.append("TRIAL " + idx + ":  ");
            runSummary.append("AvgDist=" + df.format(t.getAverageDistToTarget()) + ", ");
            runSummary.append("Time=" + df.format(t.getTimeElapsed()) + ", ");
            runSummary.append("Fitness=" + df.format(simulation.getFitnessFunction().calculateFitness(t)) + ", ");
            runSummary.append("Target (" + t.getFinalTargetPosition().getX() + ", " + t.getFinalTargetPosition().getY() + ") -- ");
            if (t.getTargetHitCount() > 0) runSummary.append("HIT!\n");
            else runSummary.append("MISS\n");
            idx++;
        }
        runSummary.append("OVERALL:  ");
        runSummary.append("AvgDist=" + df.format(results.getAverageDistToTarget()) + ", ");
        runSummary.append("Time=" + df.format(results.getTimeElapsed()) + ", ");
        runSummary.append("Fitness=" + df.format(simulation.getFitnessFunction().calculateFitness(results)) + ", -- ");
        runSummary.append("(hit " + results.hitTargetCount() + " of " + getNumTrials() + " targets).\n");
        System.out.println(runSummary);
    }

    public double getTargetFitness() {
        return getMaxFitnessValue();
    }

    public ISubstrate getSubstrate() {
        return OctopusArmSubstrateFactory.createInputHiddenOutputNoBias(numSegments, getRangeSensorResolution());
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> listOfNames = new ArrayList<String>();
        listOfNames.add("AVG_DISTANCE");
        listOfNames.add("TIME_ELAPSED");
        listOfNames.add("HITS");
        return listOfNames;
    }


    /**
     * The maximum fitness score an individual can achieve.  This value based one {@link FitnessStrategy#getMaxFitnessValue()}.
     *
     * @return The maximum theoretic fitness value.
     */
    public int getMaxFitnessValue() {
        return getFitnessStrategy().getMaxFitnessValue();
    }

    /**
     * Tests the evolved controller's ability to touch targets located in the arms operating area.
     * Arms are evaluated against a field of 410 targets (spaced at 0.1 unit intervals).  This test
     * is generally reserved for testing champion solutions for their ability to interpolate and/or
     * extrapolate motion trajectories for targets at different locations.  Results are written to
     * standard out.
     *
     * @param chrom The individual chromosome to be tested
     */
    /*
    public void generalizationTest(Chromosome chrom) {
        DecimalFormat df = new DecimalFormat("###.##");

        List<Target> testTargets = new ArrayList<Target>();
        for (double x = -1; x < 1; x += 0.1) {
            for (double y = -1; y < 1; y += 0.1) {
                testTargets.add(new Target(x, y, 0.1));
            }
        }
        simulation = new Simulation(buildPhenotype(chrom), testTargets);
        for (Trial t : simulation.getRunMetrics().getTrials()) {
            double x = t.getFinalTargetPosition().getX();
            double y = t.getFinalTargetPosition().getY();
            if (t.getTargetHitCount() > 0) {
                System.out.println("test.hit " + " (" + df.format(x) + ", " + df.format(y) + ")");
            } else {
                if (t.getTimeElapsed() >= OctopusArm.getMaxTimesteps()) {
                    System.out.println("test.miss" + " (" + df.format(x) + ", " + df.format(y) + ")");
                } else {
                    System.out.println("test.broken" + " (" + df.format(x) + ", " + df.format(y) + ")");
                }
            }
        }
    }
    */

    /*
      * EXPERIMENT PARAMETERS
      */
    private static final String STIMULUS_SIZE_KEY = "stimulus.size";
    private static final int DEFAULT_STIMULUS_SIZE = 0;
    private static int stimulusSize = DEFAULT_STIMULUS_SIZE;

    /**
     * The number of stimulus (input) nodes in the evolved network (ANN or CPPN).
     * This value is set by the <code>stimulus.size</code> parameter in the properties file.
     *
     * @return The size of the network's stimulus (input) layer.
     */
    public static int getStimulusSize() {
        return stimulusSize;
    }

    private static final String AGENT_BEHAVIOR_KEY = "octoputArm.agent.behavior";
    private static final String DEFAULT_AGENT_BEHAVIOR = "random";
    private static String agentBehaivor = DEFAULT_AGENT_BEHAVIOR;

    private static final String FITNESS_FUNCTION_KEY = "octopusArm.fitness.function";
    private static final String DEFAULT_FITNESS_FUNCTION = "random";
    private static String fitnessFunction = DEFAULT_FITNESS_FUNCTION;

    /**
     * The fitness function applied during evaluation.  Specified by the property <code>octopusArm.fitness.function</code>.
     * The values:  <code>[avg.distance.to.target]</code> are currently supported.
     *
     * @return The {@link FitnessStrategy} object for this evaluation.
     */
    public static FitnessStrategy getFitnessStrategy() {
        if (fitnessFunction.equalsIgnoreCase("avg.distance.to.target")) {
            return new AvgDistToTarget();
        }
        return new AvgDistToTarget();
    }

    private final static String PENALIZE_ENERGY_USE_KEY = "octopusArm.fitness.penalize.energy";
    private static final boolean DEFAULT_PENALIZE_ENERGY_USE = false;
    private static boolean penalizeEnergyUse = DEFAULT_PENALIZE_ENERGY_USE;

    /**
     * Should the fitness function penalize energy expended during a trial or run.
     * Set by property <code>octopusArm.fitness.penalize.energy</code>.
     *
     * @return Fitness function will/will not penalize energy use.
     */
    public static boolean penalizeEnergyUse() {
        return penalizeEnergyUse;
    }

    private static final String PENALIZE_TIME_USE_KEY = "octopusArm.fitness.penalize.time";
    private static final boolean DEFAULT_PENALIZE_TIME_USE = false;
    private static boolean penalizeTimeUse = DEFAULT_PENALIZE_TIME_USE;

    /**
     * Should the fitness function penalize for the time required to reach the target.
     * Set by property <code>octopusArm.fitness.penalize.time</code>.
     *
     * @return Fitness function will/will not penalize time to target.
     */
    public static boolean penalizeTimeUse() {
        return penalizeTimeUse;
    }

    private static final String TIP_TOUCH_KEY = "octopusArm.fitness.require.tip.touch";
    private static final boolean DEFAULT_TIP_TOUCH = true;
    private static boolean requireTipTouch = DEFAULT_TIP_TOUCH;

    /**
     * Should the trial require that the octopusArm touch the target with the tip of the arm.
     * Set by property <code>octopusArm.fitness.require.tip.touch</code>
     *
     * @return Targets must be touched by the tip of the arm.
     */
    public static boolean requireTipTouch() {
        return requireTipTouch;
    }


    /*
      * SIMULATOR PARAMETERS
      */

    private final static String NUM_SEGMENTS_KEY = "octopusArm.segments";
    private static final int DEFAULT_NUM_SEGMENTS = 10;
    private static int numSegments = DEFAULT_NUM_SEGMENTS;

    /**
     * The number of segments in the arm (not including the arm base).  Segments are numbered from 1 to n.
     * Set by property <code>octopusArm.segments</code>
     *
     * @return The number of segment in the arm.
     */
    public static int getSegmentCount() {
        return numSegments;
    }

    private static final String ARM_LENGTH_KEY = "octopusArm.arm.length";
    private static final double DEFAULT_ARM_LENGTH = 1.0;
    private static double armLength = DEFAULT_ARM_LENGTH;

    /**
     * The length of the arm.  Set by property <code>octopusArm.arm.length</code>.
     *
     * @return The length of the arm.
     */
    public static double getArmLength() {
        return armLength;
    }

    private static final String ARM_WIDTH_KEY = "octopusArm.arm.width";
    private static final double DEFAULT_ARM_WIDTH = 0.15;
    private static double armWidth = DEFAULT_ARM_WIDTH;

    /**
     * The width of the arm at the base.  Set by property <code>octopusArm.arm.width</code>.
     *
     * @return The width of the arm at the base.
     */
    public static double getArmWidth() {
        return armWidth;
    }

    private static final String ARM_TAPER_KEY = "octopusArm.arm.taper";
    private static final double DEFAULT_ARM_TAPER = 0.7;
    private static double armTaper = DEFAULT_ARM_TAPER;

    /**
     * The ratio of the arm's width at the tip over the width at the base.
     * Set by property <code>octopusArm.arm.taper</code>.
     *
     * @return The taper of the arm from base to tip.
     */
    public static double getArmTaper() {
        return armTaper;
    }

    private static final String MUSCLE_RESPONSE_KEY = "octopusArm.muscle.response";
    private static final int DEFAULT_MUSCLE_RESPONSE = 100;
    private static int muscleResponse = DEFAULT_MUSCLE_RESPONSE;

    /**
     * The response rate of a muscles in the arm.  The value specified is the number of timesteps required
     * to change the contraction force from relaxed (0.0) to maximum contraction (1.0).
     * Set by property <code>octopusArm.muscle.response</code>.
     *
     * @return The response rate of the muscles in the arm.
     */
    public static int getMuscleResponse() {
        return muscleResponse;
    }

    private static final String MUSCLE_ACTIVATION_KEY = "octopusArm.muscle.activation";
    private static final int DEFAULT_MUSCLE_ACTIVATION = 1500;
    private static int muscleActivation = DEFAULT_MUSCLE_ACTIVATION;

    /**
     * The tension of a relaxed muscle in the arm.  Muscle contractions are modeled as spring joints, thus
     * increasing the <em>k</em>-value causes the spring to contract.
     * Set by property <code>octopusArm.muscle.activation</code>.
     *
     * @return The tension of a relaxed muscle.
     */
    public static int getMuscleActivation() {
        return muscleActivation;
    }

    private static final String RANGE_SENSOR_RESOLUTION_KEY = "octopusArm.sensorArray.range.resolution";
    private static final int DEFAULT_RANGE_SENSOR_RESOLUTION = 11;
    private static int rangeSensorResolution = DEFAULT_RANGE_SENSOR_RESOLUTION;

    /**
     * The number of range readings in the field of view.
     * Set by property <code>octopusArm.sensorArray.range.resolution</code>.
     *
     * @return The number of range reading.
     */
    public static int getRangeSensorResolution() {
        return rangeSensorResolution;
    }

    private static final String RANGE_SENSOR_MAX_KEY = "octopusArm.sensorArray.range.max";
    private static final double DEFAULT_RANGE_SENSOR_MAX = 9.0;
    private static double rangeSensorMax = DEFAULT_RANGE_SENSOR_MAX;

    /**
     * The maximum range value of the sensor.  All range reading beyond this value are capped at this distance.
     * Set by property <code>octopusArm.sensorArray.range.max</code>
     *
     * @return The max range reported by the sensor.
     */
    public static double getRangeSensorMax() {
        return rangeSensorMax;
    }


    private static final String NUM_TRIALS_KEY = "octopusArm.trials";
    private static final int DEFAULT_NUM_TRIALS = 1;
    private static int numTrials = DEFAULT_NUM_TRIALS;

    /**
     * The number of trials per run.  Set by property <code>octopusArm.trials</code>.
     *
     * @return The number of trials per run.
     */
    public static int getNumTrials() {
        return numTrials;
    }

    private final static String RESET_BETWEEN_TRIALS_KEY = "octopusArm.trials.resetForEachTarget";
    private static final boolean DEFAULT_RESET_BETWEEN_TRIALS = false;
    private static boolean resetBetweenTrials = DEFAULT_RESET_BETWEEN_TRIALS;

    /**
     * Should the arm reset to the initial position after each trial.
     * Set by property <code>octopusArm.trials.resetForEachTarget</code>.
     *
     * @return The arm is/is not reset after each trial.
     */
    public static boolean resetBetweenTrials() {
        return resetBetweenTrials;
    }

    private final static String RESET_ON_ARM_BREAK_KEY = "octopusArm.trials.resetOnArmBreak";
    private static final boolean DEFAULT_RESET_ON_ARM_BREAK = true;
    private static boolean resetOnArmBreak = DEFAULT_RESET_ON_ARM_BREAK;

    /**
     * Should the arm reset when an arm break has been detected.  If true, then the arm is returned to the initial
     * position and the trial continues.  If false, then the trial ends.  Set by property <code>octopusArm.trials.resetOnArmBreak</code>.
     *
     * @return The arm is/is not reset when a break occurs.
     */
    public static boolean resetOnArmBreak() {
        return resetOnArmBreak;
    }

    private final static String TIMESTEPS_KEY = "octopusArm.timesteps";
    private static final int DEFAULT_TIMESTEPS = Integer.MAX_VALUE;
    private static int maxTimesteps = DEFAULT_TIMESTEPS;

    /**
     * The maximum number of timesteps in each trial.  Trials end early when the target is touched or
     * when and arm break occurs (if {@link #resetOnArmBreak()}=false).
     * Set by property <code>octopusArm.timesteps</code>.
     *
     * @return The maximum duration of each trial.
     */
    public static int getMaxTimesteps() {
        return maxTimesteps;
    }


    /*
      * Environment Parameters
      */
    private static final String GRAVITY_KEY = "octopusArm.env.gravity";
    private static final double DEFAULT_GRAVITY = 9.8;
    private static double gravity = DEFAULT_GRAVITY;

    /**
     * The force of gravity acting on the arm, typically 9.81 m/(s*s) on earth.
     * Set by property <code>octopusArm.env.gravity</code>.
     *
     * @return The force of gravity.
     */
    public static double getGravity() {
        return gravity;
    }

    private static final String BUOYANCY_FORCE_KEY = "octopusArm.env.buoyancy";
    private static final double DEFAULT_BUOYANCY_FORCE = 9.85;
    private static double buoyancy = DEFAULT_BUOYANCY_FORCE;

    /**
     * The buoyancy force acting in opposition to gravity.
     * Set by property <code>octopusArm.env.buoyancy<
     *
     * @return The buoyancy force.
     */
    public static double getBuoyancyForce() {
        return buoyancy;
    }

    private static final String WATER_LEVEL_KEY = "octopusArm.env.water.level";
    private static final double DEFAULT_WATER_LEVEL = 1.0;
    private static double waterLevel = DEFAULT_WATER_LEVEL;

    /**
     * The water level, typically 1.0.  Objects below the water line are subject to the buoyancy force.
     * Set by property <code>octopusarm.env.water.depth</code>.
     *
     * @return The water level.
     */
    public static double getWaterLevel() {
        return waterLevel;
    }

    /*
      * Target Parameters
      */

    private final static String TARGET_REWARD_KEY = "octopusArm.fitness.hit.target.reward";
    private static final int DEFAULT_TARGET_REWARD = 10000;
    private static int targetReward = DEFAULT_TARGET_REWARD;

    /**
     * The reward for touching the target.  Set by the property <code>octopusArm.fitness.hit.target.reward</code>.
     *
     * @return The reward for touching the target.
     */
    public static int getTargetReward() {
        return targetReward;
    }

    private Map<Integer, Target> targets;

    /**
     * Get a random target from the set of training targets.
     *
     * @return A random target.
     */
    public Target getRandomTarget() {
        return getTarget("random");
    }

    /**
     * A collection of random training targets.
     *
     * @param count The number of targets requested.
     * @return The random collection of targets.
     */
    public Collection<Target> getRandomTargetSet(int count) {
        if (targets == null) initTargets();

        List<Target> targetSet = new ArrayList<Target>(targets.values());

        while (targetSet.size() < count) {
            targetSet.addAll(targetSet);
        }

        RND.shuffle(targetSet);
        targetSet = targetSet.subList(0, count);
        return targetSet;
    }

    public Collection<Target> getTargetSet(int count) {
        if (targets == null) initTargets();

        List<Target> targetSet = new ArrayList<Target>(targets.values());

        while (targetSet.size() < count) {
            targetSet.addAll(targetSet);
        }

//        RND.shuffle(targetSet);
        targetSet = targetSet.subList(0, count);
        return targetSet;
    }

    /**
     * Get a target by name.  A random target is returned otherwise.
     *
     * @param name The name of a target (e.g. "training.1").
     * @return The specified target (or a random target otherwise).
     */
    public Target getTarget(String name) {
        if (targets == null) initTargets();

        if (name.equalsIgnoreCase("training.0")) return targets.get(0);
        else if (name.equalsIgnoreCase("training.1")) return targets.get(1);
        else if (name.equalsIgnoreCase("training.2")) return targets.get(2);
        else if (name.equalsIgnoreCase("training.3")) return targets.get(3);
        else if (name.equalsIgnoreCase("training.4")) return targets.get(4);
        else if (name.equalsIgnoreCase("training.5")) return targets.get(5);
//		else if (name.equalsIgnoreCase("training.6")) return targets.get(6);
        else
            return targets.get((int) Math.floor(targets.size() * Math.random()));
    }

    /**
     * The collection of training targets.
     *
     * @return The collection of training targets.
     */
    public Collection<Target> getTargets() {
        if (targets == null) initTargets();
        return targets.values();
    }

    private void initTargets() {
        // Designed for experiment 2
        targets = new HashMap<Integer, Target>();
//		targets.put(0, new Target(-0.85, 0.5, 0.1));
        targets.put(0, new Target(-0.5, 1.0, 0.1));
        targets.put(1, new Target(0.0, 0.75, 0.1));
        targets.put(2, new Target(0.25, 0.5, 0.1));
//		targets.put(3, new Target( 0.35, 0.0,  0.1));
        targets.put(3, new Target(0.25, -0.5, 0.1));
        targets.put(4, new Target(0.0, -0.75, 0.1));
        targets.put(5, new Target(-0.5, -1.0, 0.1));
//		targets.put(7, new Target(-0.85, -0.4, 0.1));
    }

    /**
     * The maximum number of segments per arm is 20.  This limit is imposed based on empirical experience.
     */
    public static final int MAX_SEGMENTS = 20;

    /**
     * The minimum number of segments per arm is 1.  This includes the arm base and one arm segment.
     */
    public static final int MIN_SEGMENTS = 1;

    private static final void copyright() {
        System.out.println("OctopusArm Simulator v1.0, Copyright (C) 2011 Brian Woolley\n"
                + "The OctopusArm Simulator comes with ABSOLUTELY NO WARRANTY\n"
                + "This is free software, and you are welcome to redistribute it\n"
                + "under the conditions of the GNU General Public License.");
    }
}
