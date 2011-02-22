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
    public static ATNodeImpl createByName(String className) {
        ATNodeImpl node = null;
        try {
            node = (ATNodeImpl) Class.forName(className).newInstance();
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
    public static ATNodeImpl[] createByNameList(String classNameList) {
        return createByNameList("", classNameList);
    }

    public static ATNodeImpl[] createByNameList(String prefix, String classNameList) {
        String[] classNames = Utils.extractIdentificators(classNameList);
        ATNodeImpl[] nodes = new ATNodeImpl[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            nodes[i] = createByName(prefix + classNames[i]);
        }
        return nodes;
    }
}
