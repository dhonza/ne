package gpat;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 1, 2010
 * Time: 6:45:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATLinkGene implements Comparable<ATLinkGene> {
    public static class NoInnovationComparator implements Comparator<ATLinkGene> {
        private static NoInnovationComparator instance = new NoInnovationComparator();

        private NoInnovationComparator() {
        }

        public static NoInnovationComparator getInstance() {
            return instance;
        }

        public int compare(ATLinkGene left, ATLinkGene right) {
            if (left.getTo().getId() < right.getTo().getId()) {
                return -1;
            } else if (left.getTo().getId() > right.getTo().getId()) {
                return 1;
            }
            if (left.getToChildrenIdx() < right.getToChildrenIdx()) {
                return -1;
            } else if (left.getToChildrenIdx() > right.getToChildrenIdx()) {
                return 1;
            }
            if (left.getFrom().getId() < right.getFrom().getId()) {
                return -1;
            } else if (left.getFrom().getId() > right.getFrom().getId()) {
                return 1;
            }
            return 0;
        }
    }

    final private ATNode from;
    final private ATNode to;
    final private long innovation;
    final private int toChildrenIdx;

    public ATLinkGene(ATNode from, ATNode to, long innovation, int toChildrenIdx) {
        this.from = from;
        this.to = to;
        this.innovation = innovation;
        this.toChildrenIdx = toChildrenIdx;
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

    public int getToChildrenIdx() {
        return toChildrenIdx;
    }

    public int compareTo(ATLinkGene other) {
        if (this.innovation < other.innovation) {
            return -1;
        }
        if (this.innovation > other.innovation) {
            return 1;
        }
        return 0;
    }

    @Override
    // ATLinks are equal when to and from ids are the same
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ATLinkGene)) return false;

        ATLinkGene atLink = (ATLinkGene) o;

        if (!(from.getId() == atLink.from.getId())) return false;
        if (!(to.getId() == atLink.to.getId())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 10000 * from.getId() + to.getId();
    }

    @Override
    public String toString() {
        return from.getId() + "(" + from.getName() + ") ----> " + to.getId() + "(" + to.getName() + ") IN: " + innovation + " CH: " + toChildrenIdx;
    }
}
