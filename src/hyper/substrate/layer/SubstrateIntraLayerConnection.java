package hyper.substrate.layer;

import hyper.substrate.node.INode;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 15, 2009
 * Time: 1:28:40 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class is immutable.
 */
public class SubstrateIntraLayerConnection implements Serializable {
    final private INode from;
    final private INode to;

    public SubstrateIntraLayerConnection(INode from, INode to) {
        this.from = from;
        this.to = to;
    }

    public INode getFrom() {
        return from;
    }

    public INode getTo() {
        return to;
    }
}
