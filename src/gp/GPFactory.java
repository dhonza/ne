package gp;

import common.evolution.PopulationManager;
import common.pmatrix.ParameterCombination;
import gp.terminals.Constant;
import gp.terminals.RNC;
import gp.terminals.Random;
import gpaac.AACTerminals;

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
    public static GPBase createByName(ParameterCombination parameters, PopulationManager populationManager, INode[] functions, INode[] terminals, String initialGenome) {
        GPBase gp = null;
        String className = parameters.getString("GP.TYPE");
        try {
            Constructor constructor = Class.forName(className).getConstructor(ParameterCombination.class, PopulationManager.class, INode[].class, INode[].class, String.class);
            gp = (GPBase) constructor.newInstance(parameters, populationManager, functions, terminals, initialGenome);
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

    public static INode[] createTerminalsByName(String className) {
        if (className.equals("gep.GEP")) {
            return new INode[]{new Constant(-1.0), new RNC()};
        }
        if (className.equals("gpaac.GPAAC")) {
            return new INode[]{new AACTerminals.Constant(-1.0)};
        }
        return new INode[]{new Constant(-1.0), new Random()};
    }
}
