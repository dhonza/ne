package gp;

import common.pmatrix.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 8, 2009
 * Time: 8:48:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodeFactory {
    public static Node createByName(String className) {
        Node node = null;
        try {
            node = (Node) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return node;
    }

    /**
     * @param classNameList contains list of class names separated by commas and possibly whitespaces.
     * @return created nodes
     */
    public static Node[] createByNameList(String classNameList) {
        return createByNameList("", classNameList);
    }

    public static Node[] createByNameList(String prefix, String classNameList) {
        String[] classNames = Utils.extractIdentificators(classNameList);
        Node[] nodes = new Node[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            nodes[i] = createByName(prefix + classNames[i]);
        }
        return nodes;
    }
}
