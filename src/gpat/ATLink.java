package gpat;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 1, 2010
 * Time: 6:45:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATLink {
    final private ATNode from;
    final private ATNode to;
    final private long innovation;

    public ATLink(ATNode from, ATNode to, long innovation) {
        this.from = from;
        this.to = to;
        this.innovation = innovation;
    }

    public ATNode getFrom() {
        return from;
    }

    public ATNode getTo() {
        return to;
    }

    public long getInnovation() {
        return innovation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ATLink)) return false;

        ATLink atLink = (ATLink) o;

        if (!from.equals(atLink.from)) return false;
        if (!to.equals(atLink.to)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 10000 * from.getId() + to.getId();
    }

    @Override
    public String toString() {
        return from.getId() + " ----> " + to.getId() + " IN: " + innovation;
    }
}
