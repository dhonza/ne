package hyper.evaluate;

import common.RND;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.experiments.reco.ReportStorage;
import org.jppf.server.protocol.JPPFTask;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 6, 2010
 * Time: 11:54:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class JPPFSolver extends JPPFTask {
    final private Solver solver;
    final private int combinationId;
    final private int experimentId;
    final private ParameterCombination combination;
    final private ReportStorage reportStorage;
    final private Stats stats;

    public JPPFSolver(Solver solver, int combinationId, int experimentId, ParameterCombination combination, ReportStorage reportStorage, Stats stats) {
        this.solver = solver;
        this.combinationId = combinationId;
        this.experimentId = experimentId;
        this.combination = combination;
        this.reportStorage = reportStorage;
        this.stats = stats;
    }

    public void run() {
        RND.initializeTime();
        solver.solve();
        boolean storeRun = combination.getBoolean("PRINT.storeRun");
        if (storeRun) {
            reportStorage.storeSingleRunResults(combinationId, experimentId);
        }
        setResult("Done AAAXCXACSAAC");
    }

    public int getCombinationId() {
        return combinationId;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public Stats getStats() {
        return stats;
    }
}
