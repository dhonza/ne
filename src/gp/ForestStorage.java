package gp;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Aug 7, 2009
 * Time: 1:40:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForestStorage {
    public static void save(Forest forest, String fileName) {
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
            xmlEncoder.writeObject(forest);
            xmlEncoder.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
