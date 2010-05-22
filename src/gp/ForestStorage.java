package gp;

import common.xml.XMLSerialization;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 7, 2009
 * Time: 1:40:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForestStorage {
    public static void save(Forest forest, String fileName) {
        XMLSerialization.save(forest, fileName);
    }
}
