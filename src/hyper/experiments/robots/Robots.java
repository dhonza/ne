package hyper.experiments.robots;

import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import gp.GP;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.robots.distance.NetControlledRobotDistance;
import hyper.experiments.robots.friction.NetControlledRobotFriction;
import hyper.substrate.ISubstrate;
import robot.IRobotInterface;
import robot.RobotWithSensors;
import robot.VivaeControllerAdapter;
import robot.VivaeRobot;
import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Robots implements IProblem<INet> {
    private Arena arena = null;
    private Vector<Active> agents = null;
    private VivaeControllerAdapter[] vivaeControllerAdapters;

    private int inputs;
    private int hiddenOutputs;
    private String scenarioFileName;
    private boolean showGraphics;
    private int steps;

    private boolean solved = false;

    public Robots(ParameterCombination parameters, ReportStorage reportStorage) {
        inputs = parameters.getInteger("ROBOTS.FRICTION_SENSORS");
        hiddenOutputs = parameters.getInteger("ROBOTS.NEURONS");
        scenarioFileName = parameters.getString("ROBOTS.SCENARIO");
        showGraphics = parameters.getBoolean("ROBOTS.SHOW_GRAPHICS");
        steps = parameters.getInteger("ROBOTS.STEPS");
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        createArena(scenarioFileName, false);
        FitnessFunction avg = new AverageSpeed(arena);

        vivaeControllerAdapters = new VivaeControllerAdapter[agents.size()];
        for (int i = 0; i < agents.size(); i++) {
            IRobotInterface robot = new VivaeRobot((RobotWithSensors) agents.get(i));
            NetController controller = new NetController(robot, hyperNet);
            vivaeControllerAdapters[i] = new VivaeControllerAdapter(controller);
        }

        setupExperiment();
        arena.run();

        double fitness = avg.getFitness();
        System.out.println("Fitness reached: " + fitness);
        if (fitness >= GP.TARGET_FITNESS) {
            solved = true;
        }
        return new EvaluationInfo(fitness);
    }

    public boolean isSolved() {
        return solved;
    }

    public void show(INet hyperNet) {
//        System.out.println("Press any key");
//        Scanner s = new Scanner(System.in);
//        String token = s.next();
        createArena(scenarioFileName, showGraphics);
        FitnessFunction avg = new AverageSpeed(arena);

        vivaeControllerAdapters = new VivaeControllerAdapter[agents.size()];
        for (int i = 0; i < agents.size(); i++) {
            IRobotInterface robot = new VivaeRobot((RobotWithSensors) agents.get(i));
            NetController controller = new NetController(robot, hyperNet);
            vivaeControllerAdapters[i] = new VivaeControllerAdapter(controller);
        }
        setupExperiment();
        arena.run();
        System.out.println("Fitness reached: " + avg.getFitness());
    }

    public double getTargetFitness() {
        return GP.TARGET_FITNESS;
    }

    public ISubstrate getSubstrate() {
        return RobotsSubstrateFactory.create(inputs, hiddenOutputs);
    }

    public List<String> getEvaluationInfoItemNames() {
        List<String> listOfNames = new ArrayList<String>();
        return listOfNames;
    }

    private void createArena(String svgFilename, boolean visible) {
        if (visible) {
            JFrame f = new JFrame("Hyper Experiment");
            arena = new Arena(f);
            arena.totalStepsPerSimulation = steps;
            arena.loadScenario(svgFilename);
            arena.setAllArenaPartsAntialiased(true);
            f.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(arena);
            f.setVisible(visible);
            arena.isVisible = visible;
        } else {
            arena = new Arena(null);
            arena.totalStepsPerSimulation = steps;
            arena.loadScenario(svgFilename);
            //TODO: otestovat, jestli je toto volani nutne..
//            arena.setAllArenaPartsAntialiased(true);
            arena.isVisible = false;
            arena.setLoopSleepTime(0);
        }
//        arena.frictionBuffer = new FrictionBuffer(arena);
        agents = arena.getActives();
    }

    public void setupAgent(int number) {
        Active agent = agents.get(number);
        arena.registerController(agent, vivaeControllerAdapters[number]);

        if (agent instanceof NetControlledRobotFriction) {
            ((NetControlledRobotFriction) agent).setSensors(inputs, -Math.PI / 2, Math.PI / (inputs - 1), 30);
        }

        if (agent instanceof NetControlledRobotDistance) {
            ((NetControlledRobotDistance) agent).setSensors(inputs, -Math.PI / 2, Math.PI / (inputs - 1), 200);
        }
    }

    public void setupExperiment() {
        for (int i = 0; i < agents.size(); i++) {
            setupAgent(i);
        }
        arena.init();
    }
}
