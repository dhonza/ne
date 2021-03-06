package hyper.experiments.ale;

import java.awt.image.BufferedImage;

/**
 * Created by drchajan on 22/05/14.
 */
public class JavaALEDirect implements IJavaALE {
    static {
        System.loadLibrary("ALELinkLibrary");
    }

    private long nativeHandle;

    public JavaALEDirect(String romFileName) {
        initGame(romFileName);
    }

    private native boolean initGame(String name);

    @Override
    public void close() {
    }

    @Override
    public native int[] getLegalActions();

    @Override
    public native int act(int action);

    @Override
    public native boolean isGameOver();

    @Override
    public native boolean resetGame();

    @Override
    public native int getFrameNumber();

    @Override
    public native int getEpisodeFrameNumber();

    @Override
    public int getScreenWidth() {
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public int getScreenHeight() {
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public native double[][][] getScreenRGBNormalized();

    @Override
    public double[][] getScreenGrayNormalizedRescaled(int factor) {
        return new double[0][];
    }

    @Override
    public BufferedImage getScreenAsBufferedImage() {
        throw new IllegalStateException("Not yet implemented!");
    }

    @Override
    public native int[] getPalette();
}
