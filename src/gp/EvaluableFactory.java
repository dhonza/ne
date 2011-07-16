package gp;

import common.evolution.IEvaluable;
import common.pmatrix.ParameterCombination;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 7, 2009
 * Time: 10:17:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluableFactory {
    public static IEvaluable createByName(ParameterCombination combination) {
        IEvaluable evaluable = null;
        try {
            String className = combination.getString("PROBLEM");
            Constructor constructor = Class.forName(className).getConstructor(ParameterCombination.class);
            evaluable = (IEvaluable) constructor.newInstance(combination);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return evaluable;
    }
}
