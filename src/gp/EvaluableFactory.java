package gp;

import common.evolution.IEvaluable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 7, 2009
 * Time: 10:17:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluableFactory {
    public static IEvaluable createByName(String className) {
        IEvaluable evaluable = null;
        try {
            evaluable = (IEvaluable) Class.forName(className).newInstance();
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

        return evaluable;
    }
}
