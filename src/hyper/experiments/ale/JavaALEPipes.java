package hyper.experiments.ale;

import hyper.experiments.ale.io.ALEPipes;
import hyper.experiments.ale.io.Actions;
import hyper.experiments.ale.movie.MovieGenerator;
import hyper.experiments.ale.screen.ColorPalette;
import hyper.experiments.ale.screen.NTSCPalette;
import hyper.experiments.ale.screen.ScreenConverter;
import hyper.experiments.ale.screen.ScreenMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by drchajan on 23/05/14.
 */
public class JavaALEPipes implements IJavaALE {
    private final static int[] LEGAL_ACTIONS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    private ALEPipes io;

    private boolean observed = false;

    private int frameNumber = 1;
    private int episodeFrameNumber = 1;

    private final ScreenConverter converter = new ScreenConverter(new NTSCPalette());
    private final MovieGenerator movieGenerator = new MovieGenerator("frames/frame");
    private final ColorPalette colorMap;

    private boolean exportEnabled = false;

    public JavaALEPipes(String pipeBasename) {
        init(pipeBasename);
        colorMap = new NTSCPalette();
    }

    private void init(String pipeBasename) {
        io = null;
        try {
            if (pipeBasename != null) {
                io = new ALEPipes(pipeBasename + "out", pipeBasename + "in");
            } else {
                io = new ALEPipes();
            }

            // Determine which information to request from ALE
            io.setUpdateScreen(true);
            io.setUpdateRam(false);
            io.setUpdateRL(true);
            io.initPipes();
        } catch (IOException e) {
            System.err.println("Could not initialize pipes: " + e.getMessage());
            System.exit(1);
        }
    }

    private void observe() {
        if (!observed) {
            boolean ret = io.observe();
            observed = true;
            if (exportEnabled) {
                ScreenMatrix screen = io.getScreen();
                BufferedImage image = converter.convert(screen);
                movieGenerator.record(image);
            }
        }
    }

    @Override
    public void close() {
        resetGame();
        io.close();
    }

    @Override
    public int[] getLegalActions() {
        return LEGAL_ACTIONS;
    }

    @Override
    public int act(int action) {
        observe();
        io.act(action);
        observed = false;
        frameNumber++;
        episodeFrameNumber++;
        return io.getRLData().reward;
    }

    @Override
    public boolean isGameOver() {
        observe();
        return io.getRLData().isTerminal;
    }

    @Override
    public boolean resetGame() {
        observe();
        act(Actions.map("system_reset"));
        episodeFrameNumber = 1;
        return true;
    }

    @Override
    public int getFrameNumber() {
        return frameNumber;
    }

    @Override
    public int getEpisodeFrameNumber() {
        return episodeFrameNumber;
    }

    @Override
    public double[][][] getScreenRGBNormalized() {
        observe();
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public double[][] getScreenGrayNormalizedRescaled(int factor) {
        observe();
        ScreenMatrix m = io.getScreen();
        int lh = m.height / factor;
        int lw = m.width / factor;
        double[][] s = new double[lh][lw];
        for (int r = 0; r < lh; r++) {
            for (int c = 0; c < lw; c++) {
                //down sample: take max
                double maxGray = 0.0;
                for (int r1 = 0; r1 < factor; r1++) {
                    for (int c1 = 0; c1 < factor; c1++) {
                        Color col = colorMap.get(m.matrix[c * factor + c1][r * factor + r1]);
//                        double gray = (col.getRed() + col.getGreen() + col.getBlue()) / 3.0; //mean
                        double gray = 0.21 * col.getRed() + 0.72 * col.getGreen() + 0.07 * col.getBlue(); //luminance
//                        maxGray = gray > maxGray ? gray : maxGray;
                        maxGray += gray;
                    }
                }
//                s[r][c] = maxGray;
                s[r][c] = maxGray / (factor * factor);
            }
        }
        return s;
    }

    @Override
    public int[] getPalette() {
        observe();
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public void setExportEnabled(boolean exportEnabled) {
        this.exportEnabled = exportEnabled;
    }
}
