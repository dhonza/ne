package hyper.experiments.ale;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import common.MathUtil;
import common.evolution.EvaluationInfo;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import gp.GP;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.ale.io.Actions;
import hyper.substrate.ISubstrate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 */
public class ALEExperiment implements IProblem<INet> {

    private boolean solved = false;
    private IJavaALE ale;
    private static int process = 1;
    private final int maxFrames;

    public ALEExperiment(ParameterCombination parameters, ReportStorage reportStorage) {
        System.out.println("Initializing ALE - waiting for pipe connection...");
        String pipeDir = parameters.getString("ALE.PIPE_DIR");
        String pipe = pipeDir + process + "/ale_fifo_";
        ale = new JavaALEPipes(pipe);
        process++;
        maxFrames = parameters.getInteger("ALE.MAX_FRAMES");
        System.out.println("ALE initialized.");
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        double fitness = runGame(hyperNet);
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
        ale.setExportEnabled(true);
        evaluate(hyperNet);
        ale.setExportEnabled(false);
    }

    public double getTargetFitness() {
        return GP.TARGET_FITNESS;
    }

    public ISubstrate getSubstrate() {
        return ALESubstrateFactory.createGrayDirectionOnly(16, 21);
//        return ALESubstrateFactory.createGrayDirectionOnly(32, 42);
    }

    public List<String> getEvaluationInfoItemNames() {
        return ImmutableList.of();
    }

    private double runGame(INet hyperNet) {
        double averageReward = 0.0;
        int episodes = 1;
//        ale.setExportEnabled(true);
        for (int ep = 1; ep <= episodes; ep++) {
            ale.resetGame();

            List<Integer> l = new ArrayList<>();

            int reward = 0;
            while (!ale.isGameOver() && ale.getEpisodeFrameNumber() <= maxFrames) {
//                System.out.println(" " + ale.getEpisodeFrameNumber() + " " + reward);
                double[][] s = ale.getScreenGrayNormalizedRescaled(10);
//                System.out.println("m=" + MathematicaUtils.matrixToMathematica(s) + ";");


                hyperNet.loadInputs(Doubles.concat(s));
                hyperNet.activate();
                double[] outputs = hyperNet.getOutputs();
                int maxIdx = MathUtil.maxIndexFirst(outputs);
                int action = decodeAction(maxIdx, true);

                l.add(action);

                reward += ale.act(action);
//                reward += ale.act(RND.getInt(0, 17));
//                reward += ale.act(Actions.map("player_a_down"));

            }
//            System.out.println(l);
            System.out.println("\tEpisode: " + ep + " reward: " + reward + " frames:" + ale.getEpisodeFrameNumber());
            averageReward += reward;
        }
        return averageReward / episodes;
    }

    private static int decodeAction(int dir, boolean fire) {
        int action;
        if (fire) {
            switch (dir) {
                case 0:
                    action = Actions.map("player_a_upleftfire");
                    break;
                case 1:
                    action = Actions.map("player_a_upfire");
                    break;
                case 2:
                    action = Actions.map("player_a_uprightfire");
                    break;
                case 3:
                    action = Actions.map("player_a_leftfire");
                    break;
                case 4:
                    action = Actions.map("player_a_fire");
                    break;
                case 5:
                    action = Actions.map("player_a_rightfire");
                    break;
                case 6:
                    action = Actions.map("player_a_downleftfire");
                    break;
                case 7:
                    action = Actions.map("player_a_downfire");
                    break;
                case 8:
                    action = Actions.map("player_a_downrightfire");
                    break;
                default:
                    throw new IllegalStateException("Unknown action");
            }
        } else {
            switch (dir) {
                case 0:
                    action = Actions.map("player_a_upleft");
                    break;
                case 1:
                    action = Actions.map("player_a_up");
                    break;
                case 2:
                    action = Actions.map("player_a_upright");
                    break;
                case 3:
                    action = Actions.map("player_a_left");
                    break;
                case 4:
                    action = Actions.map("player_a_noop");
                    break;
                case 5:
                    action = Actions.map("player_a_right");
                    break;
                case 6:
                    action = Actions.map("player_a_downleft");
                    break;
                case 7:
                    action = Actions.map("player_a_down");
                    break;
                case 8:
                    action = Actions.map("player_a_downright");
                    break;
                default:
                    throw new IllegalStateException("Unknown action");
            }
        }
        return action;
    }
}
