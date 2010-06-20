package gp;

import common.evolution.Evaluable;
import hyper.evaluate.GPEvaluator;

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
    public static GP createByName(String className, Evaluable[] perThreadEvaluators, Node[] functions, Node[] terminals) {
        GP gp = null;
        try {
            Constructor constructor = Class.forName(className).getConstructor(Evaluable[].class, Node[].class, Node[].class);
            gp = (GP) constructor.newInstance(perThreadEvaluators, functions, terminals);
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
}
