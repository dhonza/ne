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
    private static class LinkTwoNodes {
        int fromId;
        int toId;
        int toCount;

        private LinkTwoNodes(int fromId, int toId, int toCount) {
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
            LinkTwoNodes other = (LinkTwoNodes) o;
            return fromId == other.fromId &&
                    toId == other.toId &&
                    toCount == other.toCount;
        }

        @Override
        public String toString() {
            return "LinkTwoNodes{" +
                    "fromId=" + fromId +
                    ", toId=" + toId +
                    ", toCount=" + toCount +
                    '}';
        }
    }

    private static class NodeFromLink {
        ATLinkGene replacedLink;
        Class type;

        private NodeFromLink(ATLinkGene replacedLink, Class type) {
            this.replacedLink = replacedLink;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return replacedLink.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            NodeFromLink other = (NodeFromLink) o;
            return replacedLink.getInnovation() == other.replacedLink.getInnovation() && type == other.type;
        }

        @Override
        public String toString() {
            return "NodeFromLink{" +
                    "replacedLink=" + replacedLink +
                    ", type=" + type +
                    '}';
        }
    }

    private static class RootReplacement {
        int oldRootId;
        Class newRootType;

        private RootReplacement(int oldRootId, Class newRootType) {
            this.oldRootId = oldRootId;
            this.newRootType = newRootType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RootReplacement that = (RootReplacement) o;

            if (oldRootId != that.oldRootId) return false;
            if (newRootType != null ? !newRootType.equals(that.newRootType) : that.newRootType != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = oldRootId;
            result = 31 * result + (newRootType != null ? newRootType.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "RootReplacement{" +
                    "oldRootId=" + oldRootId +
                    ", newRootType=" + newRootType +
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

    public static class RootInnovation {
        private long fromInnovation;
        private int rootId;

        public RootInnovation(long fromInnovation, int rootId) {
            this.fromInnovation = fromInnovation;
            this.rootId = rootId;
        }

        public long getFromInnovation() {
            return fromInnovation;
        }

        public int getRootId() {
            return rootId;
        }
    }

    private Map<LinkTwoNodes, Long> links = new HashMap<LinkTwoNodes, Long>();
    private Map<NodeFromLink, NodeInnovation> nodes = new HashMap<NodeFromLink, NodeInnovation>();
    private Map<RootReplacement, RootInnovation> roots = new HashMap<RootReplacement, RootInnovation>();

    private long linkInnovation = 0;
    private int nodeInnovation;

    public ATInnovationHistory(int nodeInnovation) {
        this.nodeInnovation = nodeInnovation;
    }

    public long getLinkInnovation(int fromId, int toId, int toCount) {
        LinkTwoNodes link = new LinkTwoNodes(fromId, toId, toCount);
        if (links.containsKey(link)) {
            return links.get(link);
        } else {
            links.put(link, linkInnovation);
            return linkInnovation++;
        }
    }

    public NodeInnovation getNodeInnovation(ATLinkGene linkToReplace, Class type) {
        NodeFromLink node = new NodeFromLink(linkToReplace, type);
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

    public RootInnovation getInsertRootInnovation(int id, Class type) {
        RootReplacement node = new RootReplacement(id, type);
        if (roots.containsKey(node)) {
            return roots.get(node);
        } else {
            RootInnovation innovation = new RootInnovation(
                    linkInnovation++,
                    nodeInnovation++);
            roots.put(node, innovation);
            return innovation;
        }
    }

//    public void storeLinkInnovation(int fromId, int toId, int toCount, long innovation) {
//        Link link = new Link(fromId, toId, toCount);
//        links.put(link, innovation);
//    }

//    public NodeInnovation forceNewNodeInnovation(int fromId, int toId, Class newRootType) {
//        Node node = new Node(fromId, toId, newRootType);
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
        for (LinkTwoNodes link : links.keySet()) {
            b.append(links.get(link)).append(": ").append(link).append('\n');
        }
        for (NodeFromLink node : nodes.keySet()) {
            b.append(nodes.get(node)).append(": ").append(node).append('\n');
        }
        for (RootReplacement root : roots.keySet()) {
            b.append(roots.get(root)).append(": ").append(root).append('\n');
        }
        return b.toString();
    }
}
