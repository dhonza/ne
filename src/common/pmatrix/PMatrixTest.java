package common.pmatrix;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 7:31:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PMatrixTest {
    public static void main(String[] args) {
//        ParameterMatrixBuilder builder = new ParameterMatrixBuilder();
//        Parameter<Double> doubleParam = new Parameter<Double>("DOUBLE_PARAM", 1.5);
//        Parameter<Double> doubleParam2 = new Parameter<Double>("DOUBLE_PARAM", 0.5);
//        Parameter<Double> doubleParam3 = new Parameter<Double>("DOUBLE_PARAM", 3.0);
//        Parameter<Integer> integerParam = new Parameter<Integer>("INTEGER_PARAM", 3);
//        Parameter<Integer> integerParam2 = new Parameter<Integer>("INTEGER_PARAM", 4);
//        Parameter<String> stringParam = new Parameter<String>("STRING_PARAM", "ahoj");
//        Parameter<String> stringParam2 = new Parameter<String>("STRING_PARAM", "joha");

//        builder.add(doubleParam);
//        builder.add(doubleParam2);
//        builder.add(doubleParam3);
//        builder.addRange("DOUBLE_PARAM", 2.0, 3.0, 0.5);
//        builder.addRange("INTEGER_PARAM", 3, 4, 1);
//        builder.add(integerParam);
//        builder.add(integerParam2);
//        builder.add(stringParam);
//        builder.add(stringParam2);


        ParameterMatrixManager manager = ParameterMatrixStorage.load(new File("test.properties"));
        for (ParameterCombination parameterCombination : manager) {
            System.out.println(parameterCombination);
        }
    }
}
