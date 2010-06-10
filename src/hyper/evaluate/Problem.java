package hyper.evaluate;

import common.net.INet;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 12:17:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Problem extends Serializable {
    public double evaluate(INet hyperNet);

    public boolean isSolved();

    public void show(INet hyperNet);

    public double getTargetFitness();
}
