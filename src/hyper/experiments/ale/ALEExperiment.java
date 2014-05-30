package hyper.experiments.ale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Doubles;
import common.MathUtil;
import common.evolution.EvaluationInfo;
import common.net.INet;
import common.net.precompiled.PrecompiledFeedForwardNet;
import common.pmatrix.ParameterCombination;
import gp.GP;
import hyper.evaluate.IProblem;
import hyper.evaluate.printer.ReportStorage;
import hyper.experiments.ale.io.Actions;
import hyper.experiments.ale.movie.MovieGenerator;
import hyper.substrate.BasicSubstrate;
import hyper.substrate.ISubstrate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private final String aleDir;
    private final IJavaALE ale;
    private static int process = 1;

    private BasicSubstrate substrate;

    private final int maxFrames;
    private final int downsampleFactor;
    private boolean exportActivities = false;
    private MovieGenerator[] frameActivities;

    private static boolean aleRunning = false;

    public ALEExperiment(ParameterCombination parameters, ReportStorage reportStorage) {
        aleDir = parameters.getString("ALE.DIR");
        runALE();
        System.out.println("Initializing ALE - waiting for pipe connection...");
        String pipe = aleDir + process + "/ale_fifo_";
        ale = new JavaALEPipes(pipe);
        process++;
        maxFrames = parameters.getInteger("ALE.MAX_FRAMES");
        downsampleFactor = parameters.getInteger("ALE.DOWNSAMPLE_FACTOR");

        System.out.println("ALE initialized.");
    }

    private synchronized void runALE() {
        if (aleRunning) {
            return;
        }
        aleRunning = true;
        String command = "./run.sh";
        try {
            Runtime.getRuntime().exec(command, new String[]{}, new File(aleDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        System.out.println("Export...");
        ale.setExportEnabled(true);
        exportActivities = true;
        evaluate(hyperNet);
        ale.setExportEnabled(false);
        exportActivities = false;
        System.out.println("Export finished.");

    }

    public double getTargetFitness() {
        return GP.TARGET_FITNESS;
    }

    public ISubstrate getSubstrate() {
        int w = ale.getScreenWidth() / downsampleFactor;
        int h = ale.getScreenHeight() / downsampleFactor;

        System.out.println("downsample factor: " + downsampleFactor + " (" +
                ale.getScreenWidth() + "x" + ale.getScreenHeight() +
                " -> " + w + "x" + h + ")");
        substrate = ALESubstrateFactory.createGrayDirectionOnly(w, h, true);
        frameActivities = new MovieGenerator[3];//TODO get from substrate (inputs + other non bias layers)
        return substrate;
    }

    public List<String> getEvaluationInfoItemNames() {
        return ImmutableList.of();
    }

    private double runGame(INet hyperNet) {
        double averageReward = 0.0;
        int episodes = 1;

        for (int ep = 1; ep <= episodes; ep++) {
            ale.resetGame();

            List<Integer> actionList = new ArrayList<>();

            if (exportActivities) {
                for (int i = 0; i < frameActivities.length; i++) {
                    frameActivities[i] = new MovieGenerator("frames" + "/" + ale.getExportSequence() + "/l" + i + "/frame");
                }

            }
            int reward = 0;
            while (!ale.isGameOver() && ale.getEpisodeFrameNumber() <= maxFrames) {

                double[][] s = ale.getScreenGrayNormalizedRescaled(downsampleFactor);
                hyperNet.reset();
                hyperNet.loadInputs(Doubles.concat(s));
                hyperNet.activate();

                exportActivities(hyperNet);

                double[] outputs = hyperNet.getOutputs();
                int maxIdx = MathUtil.maxIndexFirst(outputs);
//                int action = decodeAction(maxIdx, true);
                int action = decodeHorizontalFireAction(maxIdx);

                actionList.add(action);

                reward += ale.act(action);
//                reward += ale.act(RND.getInt(0, 17));
//                reward += ale.act(Actions.map("player_a_down"));

            }


            ImmutableSet<Integer> acts = ImmutableSet.copyOf(actionList);

            System.out.println("\tEpisode: " + ep + " reward: " + reward + " frames:" + ale.getEpisodeFrameNumber() + " actions: " + acts.size());
            if (acts.size() > 1) {
                System.out.println(actionList);
            }
            double bonus = 0.0;
            if (acts.size() > 1 && acts.size() < 4) {
                bonus = 3 * acts.size();
            }
            averageReward += reward + bonus;
        }
//        averageReward = RND.getDouble(1.0, 100.0);
        return averageReward / episodes;
    }

    private static int decodeHorizontalFireAction(int dir) {
        if (dir < 0 || dir > 2) {
            throw new IllegalStateException("Unknown direction!");
        }
        return decodeAction(dir + 3, true);
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

    private void exportActivities(INet hyperNet) {
        if (exportActivities) {
            PrecompiledFeedForwardNet net = (PrecompiledFeedForwardNet) hyperNet;
            frameActivities[0].record(grayScreenToImage(MathUtil.partition(net.getActivities(0), ale.getScreenWidth() / downsampleFactor)));
            frameActivities[1].record(grayScreenToImage(MathUtil.partition(net.getActivities(1), ale.getScreenWidth() / downsampleFactor)));
            frameActivities[2].record(grayScreenToImage(MathUtil.partition(net.getActivities(2), 3)));

        }

    }

    private static BufferedImage grayScreenToImage(double[][] s) {
        BufferedImage img = new BufferedImage(s[0].length, s.length, BufferedImage.TYPE_INT_RGB);
        for (int r = 0; r < s.length; r++) {
            double[] row = s[r];
            for (int c = 0; c < row.length; c++) {
                Color col = new Color((float) s[r][c], (float) s[r][c], (float) s[r][c]);
                img.setRGB(c, r, col.getRGB());
            }
        }
        return img;
    }
}
