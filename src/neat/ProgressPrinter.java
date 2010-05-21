package neat;

/**
 * User: honza
 * Date: May 19, 2006
 * Time: 9:37:28 AM
 */
public abstract class ProgressPrinter {
    protected Population pop;

    public ProgressPrinter(Population pop) {
        this.pop = pop;
    }


    abstract public void printGeneration();


    abstract public void printProgress();

    abstract public void printFinished();
}