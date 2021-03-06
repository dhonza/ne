package hyper.experiments.ale;

import java.awt.image.BufferedImage;

/**
 * Created by drchajan on 23/05/14.
 */
public interface IJavaALE {
    void close();

    int[] getLegalActions();

    int act(int action);

    boolean isGameOver();

    boolean resetGame();

    int getFrameNumber();

    int getEpisodeFrameNumber();

    int getScreenWidth();

    int getScreenHeight();

    double[][][] getScreenRGBNormalized();

    double[][] getScreenGrayNormalizedRescaled(int factor);

    BufferedImage getScreenAsBufferedImage();

    int[] getPalette();
}
