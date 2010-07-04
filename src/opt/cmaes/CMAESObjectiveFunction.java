package opt.cmaes;

import cma.fitness.IObjectiveFunction;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 4, 2010
 * Time: 3:26:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CMAESObjectiveFunction extends IObjectiveFunction {
    int getDim();
    
    boolean isSolved();
}
