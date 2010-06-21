package common;

/**
 * <p>Title: Common Classes</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jan Drchal
 * @version 0001
 */

/**
 * This class has methods for profiling.
 */

public class Bench {

    private long startTime;
    private long lastTimeInterval;

    /**
     * Creates new Bench and starts measuring of time.
     */
    public Bench() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Resets the timer.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns the measured time.
     *
     * @return measured time in miliseconds
     */
    public long stop() {
        lastTimeInterval = System.currentTimeMillis() - startTime;
        return lastTimeInterval;
    }

    public long getLastTimeInterval() {
        return lastTimeInterval;
    }
}