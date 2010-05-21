package hyper.experiments.reco.util;

import common.pmatrix.ParameterCombination;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 10, 2009
 * Time: 4:02:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternGeneratorFactory {
    public static PatternGenerator createByName(ParameterCombination parameters) {
        String className = parameters.getString("RECO.GENERATOR");
        PatternGenerator generator = null;
        try {

            Constructor constructor = Class.forName(className).getConstructor(ParameterCombination.class);
            generator = (PatternGenerator) constructor.newInstance(parameters);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return generator;
    }
}
