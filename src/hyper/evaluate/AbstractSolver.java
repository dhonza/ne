package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.ParallelPopulationEvaluator;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.Substrate;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 11:18:52 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractSolver implements Solver {
    final protected ParameterCombination parameters;
    final protected Substrate substrate;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    protected Evaluable[] perThreadEvaluators;
    protected Problem problem;

    protected AbstractSolver(ParameterCombination parameters, Substrate substrate, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.substrate = substrate;
        this.stats = stats;
        this.reportStorage = reportStorage;
        initPerThreadEvaluators();
    }

    private void initPerThreadEvaluators() {
        boolean parallel = parameters.getBoolean("PARALLEL");
        int threads = parallel ? ParallelPopulationEvaluator.getNumberOfThreads() : 1;
        perThreadEvaluators = new Evaluable[threads];
        for (int i = (threads - 1); i >= 0; i--) {
            EvaluableSubstrateBuilder substrateBuilder = SubstrateBuilderFactory.createEvaluableSubstrateBuilder(substrate, parameters);

            problem = ProblemFactory.getProblem(parameters);

            Evaluable evaluator = EvaluableFactory.getEvaluable(parameters, substrateBuilder, problem);
            perThreadEvaluators[i] = evaluator;
        }
    }

    abstract public void solve();

    abstract public String getConfigString();
}