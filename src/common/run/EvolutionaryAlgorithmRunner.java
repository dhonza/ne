package common.run;

import common.stats.Stats;
import hyper.evaluate.printer.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: 8/31/11
 * Time: 10:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EvolutionaryAlgorithmRunner {
    void run(Stats stats, ReportStorage reportStorage);
}
