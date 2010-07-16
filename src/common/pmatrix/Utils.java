package common.pmatrix;

import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 8, 2009
 * Time: 8:37:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static String[] extractIdentificators(String source) {
        return source.split("\\s*;\\s*");
    }

    public static void setStaticParameters(ParameterCombination parameters, Class targetClass, String prefix) {
        for (String parameterName : parameters) {
            String start = prefix + ".";
            if (parameterName.startsWith(start)) {
                String variableName = parameterName.replace(start, "");
                setParameter(parameters, targetClass, null, parameterName, variableName);
            }
        }
    }

    public static void setParameters(ParameterCombination parameters, Object targetInstance, String prefix) {
        for (String parameterName : parameters) {
            String start = prefix + ".";
            if (parameterName.startsWith(start)) {
                String variableName = parameterName.replace(start, "");
                setParameter(parameters, targetInstance.getClass(), targetInstance, parameterName, variableName);
            }
        }
    }

    private static void setParameter(ParameterCombination parameters, Class targetClass, Object targetInstance, String parameterName, String variableName) {
        try {
            Field field = targetClass.getField(variableName);
            Object value = null;
            if (field.getType() == int.class) {
                int v = parameters.getInteger(parameterName);
                field.setInt(targetInstance, v);
                value = v;
            } else if (field.getType() == long.class) {
                int v = parameters.getInteger(parameterName);
                field.setInt(targetInstance, v);
                value = v;
            } else if (field.getType() == double.class) {
                double v = parameters.getDouble(parameterName);
                field.setDouble(targetInstance, v);
                value = v;
            } else if (field.getType() == float.class) {
                double v = parameters.getDouble(parameterName);
                field.setFloat(targetInstance, (float) v);
                value = v;
            } else if (field.getType() == boolean.class) {
                boolean v = parameters.getBoolean(parameterName);
                field.setBoolean(targetInstance, v);
                value = v;
            } else if (field.getType() == String.class) {
                String v = parameters.getString(parameterName);
                field.set(targetInstance, v);
                value = v;
            }
            System.out.println("SET: " + parameterName + " = " + value);
        } catch (NoSuchFieldException e) {
            System.out.println("IGNORE: " + parameterName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}