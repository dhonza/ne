package gpat;

import common.pmatrix.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 8, 2009
 * Time: 8:48:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ATNodeFactory {
    public static ATNode createByName(String className) {
        ATNode node = null;
        try {
            node = (ATNode) Class.forName(className).newInstance();
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
    public static ATNode[] createByNameList(String classNameList) {
        return createByNameList("", classNameList);
    }

    public static ATNode[] createByNameList(String prefix, String classNameList) {
        String[] classNames = Utils.extractIdentificators(classNameList);
        ATNode[] nodes = new ATNode[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            nodes[i] = createByName(prefix + classNames[i]);
        }
        return nodes;
    }
}