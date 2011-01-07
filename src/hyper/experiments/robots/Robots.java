package hyper.experiments.robots;

import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.substrate.ISubstrate;
import robot.IRobotInterface;
import robot.VivaeControllerAdapter;
import robot.VivaeRobot;
import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.util.FrictionBuffer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
    private VivaeControllerAdapter vivaeControllerAdapter;

    private int inputs = 5;
    private int hiddenOutputs = 3;

    public Robots(ParameterCombination parameters, ReportStorage reportStorage) {
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        createArena("cfg/vivae/scenarios/oval1_h.svg", false);
        FitnessFunction avg = new AverageSpeed(arena);

        IRobotInterface robot = new VivaeRobot((NetControlledRobot) agents.get(0));
        NetController controller = new NetController(robot, hyperNet);
        vivaeControllerAdapter = new VivaeControllerAdapter(controller);

        setupExperiment();
        arena.run();

        return new EvaluationInfo(avg.getFitness());
    }

    public boolean isSolved() {
        return false;
    }

    public void show(INet hyperNet) {
//        System.out.println("Press any key");
//        Scanner s = new Scanner(System.in);
//        String token = s.next();
        createArena("cfg/vivae/scenarios/oval1_h.svg", true);
        FitnessFunction avg = new AverageSpeed(arena);

        IRobotInterface robot = new VivaeRobot((NetControlledRobot) agents.get(0));
        NetController controller = new NetController(robot, hyperNet);
        vivaeControllerAdapter = new VivaeControllerAdapter(controller);

        setupExperiment();
        arena.run();
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
            JFrame f = new JFrame("Hyper Experiment");
            arena = new Arena(f);
            arena.totalStepsPerSimulation = 1500;
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
            arena.loadScenario(svgFilename);
            //TODO: otestovat, jestli je toto volani nutne..
//            arena.setAllArenaPartsAntialiased(true);
            arena.isVisible = false;
            arena.setLoopSleepTime(0);
        }
        arena.frictionBuffer = new FrictionBuffer(arena);
        agents = arena.getActives();
    }

    public void setupAgent(int number) {
        Active agent = agents.get(number);
        arena.registerController(agent, vivaeControllerAdapter);

        if (agent instanceof NetControlledRobot) {
            ((NetControlledRobot) agent).setSensors(inputs, -Math.PI / 2, Math.PI / (inputs - 1), 30);
        }
    }

    public void setupExperiment() {
        int agentnum = agents.size();
        for (int i = 0; i < agentnum; i++) {
            setupAgent(i);
        }
        arena.init();
    }
}
