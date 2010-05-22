package common.evolution;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 18, 2009
 * Time: 4:53:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProgressPrinter {
    public void printGeneration();

    public void printProgress();

    public void printFinished();
}