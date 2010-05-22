/*
 * Created on 10.7.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package neat;

import common.RND;
import common.xml.XMLSerialization;

/**
 * @author drchal
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class NetStorage {

    public static Net[] loadMultiple(String fileName) {
        return (Net[]) XMLSerialization.load(fileName);
    }

    public static Net load(String fileName) {
        return (Net) XMLSerialization.load(fileName);
    }

    public static void save(Net net, String fileName) {
        XMLSerialization.save(net, fileName);
    }

    public static void saveMultiple(Net[] nets, String fileName) {
        XMLSerialization.save(nets, fileName);
    }

    public static void main(String[] args) {
        Net net = new Net(1);
        int[] h = {3};
        net.createFeedForward(2, h, 1);
        //net.createFullyConnected(2, 0, 2);
        RND.initializeTime();
        net.randomizeWeights(-0.3, 0.3);
        save(net, "pokus.xml");
        Net net2 = load("pokus.xml");
        System.out.println(net);
        System.out.println(net2);
    }
}