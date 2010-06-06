package hyper.experiments.reco.util;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 10, 2009
 * Time: 3:47:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PatternGenerator extends Serializable {
    double[][] generateInputPatterns();

    double[][] generateOutputPatterns();
}
