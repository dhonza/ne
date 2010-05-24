package common.pmatrix;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 11:58:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterMatrixStorage {
    public static ParameterMatrixManager load(File file) {
        Configuration properties = null;
        try {
            //I use apache commons configurations instead of JDK Properties, mainly because ability to include other property files
            properties = new PropertiesConfiguration(file);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ParameterMatrixBuilder builder = new ParameterMatrixBuilder();

        Iterator it = properties.getKeys();
        while (it.hasNext()) {
            Object o = it.next();
            String paramName = (String) o;
            String[] listOfParams = properties.getStringArray(paramName);
            for (String param : listOfParams) {
                ANTLRStringStream input = new ANTLRStringStream(param);
                PropertyLexer lexer = new PropertyLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                PropertyParser parser = new PropertyParser(tokens);
                parser.setBuilder(builder);
                parser.setName(paramName);
                try {
                    parser.expr();
                } catch (RecognitionException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        return builder.buildManager();
    }

}
