package hyper.experiments.robots;

//import robot.IRobotInterface;
//import robot.RobotWithSensors;
//import robot.VivaeControllerAdapter;
//import robot.VivaeRobot;

import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.substrate.ISubstrate;
import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.controllers.VivaeController;
import vivae.example.IExperiment;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.robots.IRobotInterface;
import vivae.robots.IRobotWithSensorsInterface;
import vivae.robots.VivaeRobot;

import javax.swing.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */

// This legacy VIVAE code has been commented out. Remove after rewriting to new interfaces!

/*
public class Robots {

}    //*/
///*
public class Robots implements IProblem<INet>, IExperiment {
    private Arena arena = null;
    private Vector<Active> agents = null;

    private int inputs;
    private int hiddenOutputs;
    private String scenarioFileName;
    private boolean showGraphics;
    private int steps;

    private JFrame f = null;

    protected int stepsDone;
    ArrayList<IRobotInterface> robots = new ArrayList<IRobotInterface>();
    ArrayList<VivaeController> controllers = new ArrayList<VivaeController>();

    public Robots(ParameterCombination parameters, ReportStorage reportStorage) {
        inputs = parameters.getInteger("ROBOTS.FRICTION_SENSORS");
        hiddenOutputs = parameters.getInteger("ROBOTS.NEURONS");
        scenarioFileName = parameters.getString("ROBOTS.SCENARIO");
        showGraphics = parameters.getBoolean("ROBOTS.SHOW_GRAPHICS");
        steps = parameters.getInteger("ROBOTS.STEPS");
    }

    public int getStepsDone() {
        return stepsDone;
    }

    public List<IRobotInterface> getRobots() {
        return robots;
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        robots.clear();
        controllers.clear();
        createArena(scenarioFileName, true);
        FitnessFunction avg = new AverageSpeed(this);

        for (int i = 0; i < agents.size(); i++) {
            IRobotWithSensorsInterface robot = new VivaeRobot("Robot" + i);
            robots.add(robot);
            NetController controller = new NetController(robot, hyperNet);
            controllers.add(controller);
        }

//        setupExperiment();
//        arena.run();//TODO use this one!
        startExperiment(true);
        System.out.println("Fitness reached: " + avg.getFitness());

        return new EvaluationInfo(avg.getFitness());
    }

    public boolean isSolved() {
        return false;
    }

    public void show(INet hyperNet) {
        System.out.println("Press any key");
        Scanner s = new Scanner(System.in);
        String token = s.next();

        robots.clear();
        controllers.clear();
        createArena(scenarioFileName, showGraphics);
        FitnessFunction avg = new AverageSpeed(this);

        for (int i = 0; i < agents.size(); i++) {
            IRobotWithSensorsInterface robot = new VivaeRobot("Robot" + i);
            robots.add(robot);
            NetController controller = new NetController(robot, hyperNet);
            controllers.add(controller);
        }

//        arena.run();//TODO use this one!
        startExperiment(true);
        System.out.println("Fitness reached: " + avg.getFitness());
    }

    public double getTargetFitness() {
        return 10.0;
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
            f = new JFrame("Hyper Experiment");
            arena = Arena.renewArena(f);
            arena.totalStepsPerSimulation = steps;

            f.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(arena);
            f.setVisible(true);

            arena.loadScenario(svgFilename);
            arena.isVisible = true;
            arena.setAllArenaPartsAntialiased(true);

        } else {
            arena = Arena.renewArena(f);
            arena.totalStepsPerSimulation = steps;
            arena.loadScenario(svgFilename);
            arena.isVisible = false;
            arena.setLoopSleepTime(0);
        }
//        arena.frictionBuffer = new FrictionBuffer(arena);

//        for(Active a: arena.getActives()) {
//            robots.add((IRobotInterface) a);
//        }
        agents = arena.getActives();
    }

    public void setupAgent(int number) {
        Active agent = agents.get(number);
//        arena.registerController(agent, vivaeControllerAdapters[number]);

//        if (agent instanceof NetControlledRobotFriction) {
//            ((NetControlledRobotFriction) agent).setSensors(inputs, -Math.PI / 2, Math.PI / (inputs - 1), 30);
//        }

//        if (agent instanceof NetControlledRobotDistance) {
//            ((NetControlledRobotDistance) agent).setSensors(inputs, -Math.PI / 2, Math.PI / (inputs - 1), 200);
//        }
    }

    public void setupExperiment() {
        for (int i = 0; i < agents.size(); i++) {
            setupAgent(i);
        }
        arena.init();
    }

    public void startExperiment(boolean isVisible) {
        int loopSleepTime = 10;
        int worldStepsPerLoop = 1;


        arena.setScreenSize(640, 480);
        arena.initWorld();

        for (int i = 1; i < 5000; i++) {

            ListIterator<VivaeController> ci = controllers.listIterator();
            while (ci.hasNext()) {
                ci.next().moveControlledObject();
            }

            arena.getWorld().step();
            arena.moveVivaes();

            if (isVisible) {
                arena.repaint();
                if (loopSleepTime > 0) {
                    try {
                        Thread.sleep(loopSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            stepsDone++;
        }
    }
}
//*/
