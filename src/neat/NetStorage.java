/*
 * Created on 10.7.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package neat;

import common.RND;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 * @author drchal
 *         <p/>
 *         TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class NetStorage {

    public static Net[] loadMultiple(String ofileName) {
        Net[] nets = null;
        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(ofileName)));

            nets = (Net[]) xmlDecoder.readObject();

            xmlDecoder.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return nets;
    }

    public static Net load(String ofileName) {
        Net net = null;
        try {
            XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(ofileName)));

            net = (Net) xmlDecoder.readObject();

            xmlDecoder.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return net;
    }

    public static void save(Net onet, String ofileName) {
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(ofileName)));

            xmlEncoder.writeObject(onet);
            xmlEncoder.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void saveMultiple(Net[] onets, String ofileName) {
        BufferedWriter bw;
        try {
            XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(ofileName)));

            xmlEncoder.writeObject(onets);
            xmlEncoder.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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