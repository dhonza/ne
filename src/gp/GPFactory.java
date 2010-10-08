package gp;

import common.evolution.PopulationManager;
import gp.terminals.Constant;
import gp.terminals.RNC;
import gp.terminals.Random;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 7, 2009
 * Time: 10:23:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class GPFactory {
    public static GPBase createByName(String className, PopulationManager populationManager, INode[] functions, INode[] terminals) {
        GPBase gp = null;
        try {
            Constructor constructor = Class.forName(className).getConstructor(PopulationManager.class, INode[].class, INode[].class);
            gp = (GPBase) constructor.newInstance(populationManager, functions, terminals);
        } catch (NoSuchMethodException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println(e.getCause());
            e.printStackTrace();
            System.exit(1);
        }
        return gp;
    }

    public static Node[] createTerminalsByName(String className) {
        if (className.equals("gep.GEP")) {
            return new Node[]{new Constant(-1.0), new RNC()};
        }
        return new Node[]{new Constant(-1.0), new Random()};
    }
}
