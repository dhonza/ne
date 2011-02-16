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
        int toCount;

        private Link(int fromId, int toId, int toCount) {
            this.fromId = fromId;
            this.toId = toId;
            this.toCount = toCount;
        }

        @Override
        public int hashCode() {
            return (fromId + "_" + toId + "_" + toCount).hashCode();
        }


        @Override
        public boolean equals(Object o) {
            Link other = (Link) o;
            return fromId == other.fromId &&
                    toId == other.toId &&
                    toCount == other.toCount;
        }

        @Override
        public String toString() {
            return "Link{" +
                    "fromInnovation=" + fromId +
                    ", toInnovation=" + toId +
                    ", toCount=" + toCount +
                    '}';
        }
    }

    private static class Node {
        ATLinkGene replacedLink;
        Class type;

        private Node(ATLinkGene replacedLink, Class type) {
            this.replacedLink = replacedLink;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return replacedLink.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            Node other = (Node) o;
            return replacedLink.getInnovation() == other.replacedLink.getInnovation() && type == other.type;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "replacedLink=" + replacedLink +
                    ", type=" + type +
                    '}';
        }
    }

    public static class NodeInnovation {
        private long fromInnovation;
        private long toInnovation;
        private int nodeId;

        public NodeInnovation(long fromInnovation, long toInnovation, int nodeId) {
            this.fromInnovation = fromInnovation;
            this.toInnovation = toInnovation;
            this.nodeId = nodeId;
        }

        public long getFromInnovation() {
            return fromInnovation;
        }

        public long getToInnovation() {
            return toInnovation;
        }

        public int getNodeId() {
            return nodeId;
        }

        @Override
        public String toString() {
            return "NodeInnovation{" +
                    "fromInnovation=" + fromInnovation +
                    ", toInnovation=" + toInnovation +
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

    public long getLinkInnovation(int fromId, int toId, int toCount) {
        Link link = new Link(fromId, toId, toCount);
        if (links.containsKey(link)) {
            return links.get(link);
        } else {
            links.put(link, linkInnovation);
            return linkInnovation++;
        }
    }

    public NodeInnovation getNodeInnovation(ATLinkGene linkToReplace, Class type) {
        Node node = new Node(linkToReplace, type);
        if (nodes.containsKey(node)) {
            return nodes.get(node);
        } else {
            NodeInnovation innovation = new NodeInnovation(
                    getLinkInnovation(linkToReplace.getFrom().getId(), nodeInnovation, 1),
                    getLinkInnovation(nodeInnovation, linkToReplace.getTo().getId(), 1),
                    nodeInnovation++
            );
            nodes.put(node, innovation);
            return innovation;
        }

    }

//    public void storeLinkInnovation(int fromId, int toId, int toCount, long innovation) {
//        Link link = new Link(fromId, toId, toCount);
//        links.put(link, innovation);
//    }

//    public NodeInnovation forceNewNodeInnovation(int fromId, int toId, Class type) {
//        Node node = new Node(fromId, toId, type);
//        NodeInnovation innovation = new NodeInnovation(linkInnovation,
//                linkInnovation + 1, nodeInnovation++);
//        linkInnovation += 2;
//        nodes.put(node, innovation);
//        return innovation;
//
//    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("INNOVATIONS:\n");
        for (Link link : links.keySet()) {
            b.append(links.get(link)).append(": ").append(link).append('\n');
        }
        for (Node node : nodes.keySet()) {
            b.append(nodes.get(node)).append('\n');
        }
        return b.toString();
    }
}
