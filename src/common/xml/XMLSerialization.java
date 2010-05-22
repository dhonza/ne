package common.xml;

import com.thoughtworks.xstream.XStream;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: May 22, 2010
 * Time: 5:52:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLSerialization {
    public static void save(Object o, String fileName) {
        XStream xs = new XStream();
        OutputStream fs = null;
        try {
            fs = new BufferedOutputStream(new FileOutputStream(fileName));
            xs.toXML(o, fs);
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Object load(String fileName) {
        XStream xs = new XStream();
        try {
            return xs.fromXML(new BufferedInputStream(new FileInputStream(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
