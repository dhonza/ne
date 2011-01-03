package hyper.experiments.robots;

import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import hyper.evaluate.IProblem;
import hyper.evaluate.IProblemGeneralization;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.ISubstrate;
import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.example.FRNNController;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.util.FrictionBuffer;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Robots implements IProblem<INet>, IProblemGeneralization<INet> {
    private Arena arena = null;
    private Vector<Active> agents = null;

    private int inputs = 5;
    private int hiddenOutputs = 3;

    public Robots(ParameterCombination parameters, ReportStorage reportStorage) {
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        createArena("cfg/vivae/scenarios/arena1.svg", true);
        setupAgent(0, hyperNet, 50);

        FitnessFunction avg = new AverageSpeed(arena);

        return new EvaluationInfo(avg.getFitness());
    }

    public boolean isSolved() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void show(INet hyperNet) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getTargetFitness() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ISubstrate getSubstrate() {
        return RobotsSubstrateFactory.create(inputs, hiddenOutputs);
    }

    public List<String> getEvaluationInfoItemNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EvaluationInfo evaluateGeneralization(INet hyperNet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void showGeneralization(INet hyperNet) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void createArena(String svgFilename, boolean visible) {
        if (visible) {
            JFrame f = new JFrame("Hyper Experiment");
            arena = new Arena(f);
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

    public void setupAgent(int number, INet hyperNet, double frictionDistance) {
        Active agent = agents.get(number);
        double sangle = -Math.PI;
//        double ai = Math.PI / (snum  - 1);
        FRNNController frnnc = new FRNNController();
        arena.registerController(agent, frnnc);
//        if (agent instanceof FRNNControlledRobot) {
//            ((FRNNControlledRobot) agent).setSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
//        }
    }
}
