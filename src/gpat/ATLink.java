package gpat;

import gp.INode;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 1, 2010
 * Time: 6:45:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATLink {
    final private INode from;
    final private INode to;
    final private long innovation;

    public ATLink(INode from, INode to, long innovation) {
        this.from = from;
        this.to = to;
        this.innovation = innovation;
    }

    public INode getFrom() {
        return from;
    }

    public INode getTo() {
        return to;
    }

    public long getInnovation() {
        return innovation;
    }

    @Override
    public String toString() {
        return "IN: " + innovation + " " + from + " ----> " + to; 
    }
}
