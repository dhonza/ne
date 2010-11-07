package gpat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 7, 2010
 * Time: 8:44:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATInnovationHistory {
    private static class Link {
        int fromId;
        int toId;

        private Link(int fromId, int toId) {
            this.fromId = fromId;
            this.toId = toId;
        }

        @Override
        public int hashCode() {
            return (fromId + "_" + toId).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            Link other = (Link) o;
            return fromId == other.fromId && toId == other.toId;
        }

        @Override
        public String toString() {
            return "Link{" +
                    "fromId=" + fromId +
                    ", toId=" + toId +
                    '}';
        }
    }

    private static class Node {
        int fromId;
        int toId;
        Class type;

        private Node(int fromId, int toId, Class type) {
            this.fromId = fromId;
            this.toId = toId;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return (fromId + "_" + toId).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            Node other = (Node) o;
            return fromId == other.fromId && toId == other.toId && type == other.type;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "fromId=" + fromId +
                    ", toId=" + toId +
                    ", type=" + type +
                    '}';
        }
    }

    public static class NodeInnovation {
        private long fromId;
        private long toId;
        private int nodeId;

        public NodeInnovation(long fromId, long toId, int nodeId) {
            this.fromId = fromId;
            this.toId = toId;
            this.nodeId = nodeId;
        }

        public long getFromId() {
            return fromId;
        }

        public long getToId() {
            return toId;
        }

        public int getNodeId() {
            return nodeId;
        }

        @Override
        public String toString() {
            return "NodeInnovation{" +
                    "fromId=" + fromId +
                    ", toId=" + toId +
                    ", nodeId=" + nodeId +
                    '}';
        }
    }

    private Map<Link, Long> links = new HashMap<Link, Long>();
    private Map<Node, NodeInnovation> nodes = new HashMap<Node, NodeInnovation>();

    private long linkInnovation = 0;
    private int nodeInnovation;

    public ATInnovationHistory(int nodeInnovation) {
        this.nodeInnovation = nodeInnovation;
    }

    public long getLinkInnovation(int fromId, int toId) {
        Link link = new Link(fromId, toId);
        if (links.containsKey(link)) {
            return links.get(link);
        } else {
            links.put(link, linkInnovation);
            return linkInnovation++;
        }
    }

    public int getInitialNodeInnovation() {
        return nodeInnovation++;
    }

    public NodeInnovation getNodeInnovation(int fromId, int toId, Class type) {
        Node node = new Node(fromId, toId, type);
        if (nodes.containsKey(node)) {
            return nodes.get(node);
        } else {
            NodeInnovation innovation = new NodeInnovation(linkInnovation,
                    linkInnovation + 1, nodeInnovation++);
            linkInnovation += 2;
            nodes.put(node, innovation);
            return innovation;
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Link link : links.keySet()) {
            b.append(links.get(link)).append(", ");
        }
        b.append('\n');
        for (Node node : nodes.keySet()) {
            b.append(nodes.get(node)).append('\n');
        }
        return b.toString();
    }
}
