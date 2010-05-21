package common.pmatrix;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 25, 2009
 * Time: 11:58:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterMatrixStorage {
    public static ParameterMatrixManager load(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ParameterMatrixBuilder builder = new ParameterMatrixBuilder();


        for (Object o : properties.keySet()) {
            String param = (String) o;
            ANTLRStringStream input = new ANTLRStringStream(properties.getProperty(param));
            PropertyLexer lexer = new PropertyLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PropertyParser parser = new PropertyParser(tokens);
            parser.setBuilder(builder);
            parser.setName(param);
            try {
                parser.expr();
            } catch (RecognitionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        return builder.buildManager();
    }

}
