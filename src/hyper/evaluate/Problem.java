package hyper.evaluate;

import neat.Net;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 12:17:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Problem {
    public double evaluate(Net hyperNet);

    public void show(Net hyperNet);

    public double getTargetFitness();
}
