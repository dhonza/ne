package hyper.experiments.ale;

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

    double[][][] getScreenRGBNormalized();

    double[][] getScreenGrayNormalizedRescaled(int factor);

    int[] getPalette();

    void setExportEnabled(boolean enabled);
}
