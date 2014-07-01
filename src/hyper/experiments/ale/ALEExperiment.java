package hyper.experiments.ale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Doubles;
import common.MathUtil;
import common.evolution.*;
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
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 1/3/11
 * Time: 3:50 PM
 */
public class ALEExperiment implements IProblem<INet>, IBehavioralDiversity {
    private final ReportStorage reportStorage;

    private boolean solved = false;
    private final String aleDir;
    private final IJavaALE ale;
    private static int process = 1;

    private BasicSubstrate substrate;

    private final int maxFrames;
    private final int downsampleFactor;
    private final int frameSkip;

    private boolean export = false;
    private MovieGenerator frame;
    private MovieGenerator[] frameActivities;
    private int exportSequence = 1;

    private static boolean aleRunning = false;

    protected final IDistanceWithPrepare<Object, EvaluationInfo> behavioralDistance;


    public ALEExperiment(ParameterCombination parameters, ReportStorage reportStorage) {
        this.reportStorage = reportStorage;
        aleDir = parameters.getString("ALE.DIR");
        runALE(parameters);
        System.out.println("Initializing ALE - waiting for pipe connection...");
        String pipe = aleDir + process + "/ale_fifo_";
        ale = new JavaALEPipes(pipe);
        process++;
        maxFrames = parameters.getInteger("ALE.MAX_FRAMES");
        downsampleFactor = parameters.getInteger("ALE.DOWNSAMPLE_FACTOR");
        frameSkip = parameters.getInteger("ALE.FRAME_SKIP");
        System.out.println("ALE initialized.");
        behavioralDistance = ALEBehavioralDistanceFactory.createDistance(this, parameters.getString("ALE.BEHAVIORAL_DIVERSITY"));
    }

    private synchronized void runALE(ParameterCombination parameters) {
        if (aleRunning) {
            return;
        }
        aleRunning = true;
        int threads = 1;
        if (parameters.getBoolean("PARALLEL")) {
            if (parameters.contains("PARALLEL.FORCE_THREADS")) {
                threads = parameters.getInteger("PARALLEL.FORCE_THREADS");
            } else {
                threads = PopulationManager.getNumberOfThreads();
            }
        }

        String command = "./run.sh " + threads;
        try {
            Runtime.getRuntime().exec(command, new String[]{}, new File(aleDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EvaluationInfo evaluate(INet hyperNet) {
        EvaluationInfo evaluationInfo = runGame(hyperNet);
//        System.out.println("Fitness reached: " + fitness);
        if (evaluationInfo.getFitness() >= GP.TARGET_FITNESS) {
            solved = true;
        }
        return evaluationInfo;
    }

    public boolean isSolved() {
        return solved;
    }

    public void show(INet hyperNet) {
        System.out.println("Export...");
        export = true;
        evaluate(hyperNet);
        export = false;
        exportSequence++;
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
        substrate = ALESubstrateFactory.createGrayDirectionOnly(w, h, false);
        frameActivities = new MovieGenerator[3];//TODO get from substrate (inputs + other non bias layers)
        return substrate;
    }

    public List<String> getEvaluationInfoItemNames() {
        return ImmutableList.of();
    }

    private EvaluationInfo runGame(INet hyperNet) {
        double averageReward = 0.0;
        int episodes = 1;

        ImmutableList.Builder<Double> outputBuilder = ImmutableList.builder();

        for (int ep = 1; ep <= episodes; ep++) {
            ale.resetGame();

            if (export) {
                frame = new MovieGenerator(reportStorage.getReportFile("frames", exportSequence + ".zip"), "frame");
                for (int i = 0; i < frameActivities.length; i++) {
                    frameActivities[i] = new MovieGenerator(reportStorage.getReportFile("frames", exportSequence + "_l" + i + ".zip"), "frame");
                }

            }
            int reward = 0;
            int action = -1;
            int toSkip = 0;
            while (!ale.isGameOver() && ale.getEpisodeFrameNumber() <= maxFrames) {

                if (toSkip == 0) {
                    double[][] s = ale.getScreenGrayNormalizedRescaled(downsampleFactor);
                    hyperNet.reset();
                    hyperNet.loadInputs(Doubles.concat(s));
                    hyperNet.activate();
                    double[] outputs = hyperNet.getOutputs();

                    outputBuilder.addAll(Doubles.asList(outputs));

                    int maxIdx = MathUtil.maxIndexFirst(outputs);
//                    action = decodeHorizontalFireAction(maxIdx);
                    action = decodeAction(maxIdx, true);
                    toSkip = frameSkip;
                } else {
                    toSkip--;
                }
                exportFrame(hyperNet);


                reward += ale.act(action);
//                reward += ale.act(RND.getInt(0, 17));
//                reward += ale.act(Actions.map("player_a_down"));

            }

            exportFinish();

            double bonus = 0.0;
            averageReward += reward + bonus;
        }
        double fitness = averageReward / episodes;
        ImmutableMap<String, Object> paramMap = ImmutableMap.of("OUTPUTS", (Object) outputBuilder.build());

        return new EvaluationInfo(fitness, paramMap);
    }

    @Override
    public ImmutableList<Double> behavioralDiversity(ImmutableList<EvaluationInfo> evaluationInfos) {
        return BehavioralDiversityUtils.behavioralDiversity(behavioralDistance, evaluationInfos);
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

    private void exportFrame(INet hyperNet) {
        if (export) {
            frame.record(ale.getScreenAsBufferedImage());
            PrecompiledFeedForwardNet net = (PrecompiledFeedForwardNet) hyperNet;
            frameActivities[0].record(grayScreenToImage(MathUtil.partition(net.getActivities(0), ale.getScreenWidth() / downsampleFactor)));
            frameActivities[1].record(grayScreenToImage(MathUtil.partition(net.getActivities(1), ale.getScreenWidth() / downsampleFactor)));
            frameActivities[2].record(grayScreenToImage(MathUtil.partition(net.getActivities(2), 3)));

        }

    }

    private void exportFinish() {
        if (export) {
            frame.close();
            for (MovieGenerator frameActivity : frameActivities) {
                frameActivity.close();
            }

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
