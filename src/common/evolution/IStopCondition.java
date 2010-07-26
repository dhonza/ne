package common.evolution;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 2:54:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IStopCondition extends Serializable {
    public boolean isMet();
}
