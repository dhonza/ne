package hyper.substrate.node;

import hyper.substrate.ICoordinate;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 13, 2009
 * Time: 2:18:08 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Implementing class must be immutable.
 */
public interface Node extends Serializable {
    public ICoordinate getCoordinate();

    public NodeType getType();
}
