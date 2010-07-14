package hyper.evaluate;

import common.evolution.Evaluable;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.ParallelPopulationEvaluator;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.experiments.reco.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 11:18:52 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractSolver implements Solver {
    final protected ParameterCombination parameters;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    protected EvolutionaryAlgorithmSolver solver;

    protected Evaluable[] perThreadEvaluators;
    protected IProblem problem;

    protected AbstractSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.stats = stats;
        this.reportStorage = reportStorage;
        initPerThreadEvaluators();
    }

    private void initPerThreadEvaluators() {
        boolean parallel = parameters.getBoolean("PARALLEL");
        int threads = 1;
        if (parallel) {
            if (parameters.contains("PARALLEL.FORCE_THREADS")) {
                threads = parameters.getInteger("PARALLEL.FORCE_THREADS");
            } else {
                threads = ParallelPopulationEvaluator.getNumberOfThreads();
            }
        }
        perThreadEvaluators = new Evaluable[threads];
        for (int i = (threads - 1); i >= 0; i--) {

            problem = ProblemFactory.getProblem(parameters, reportStorage);
            EvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

            Evaluable evaluator = EvaluableFactory.getEvaluable(parameters, substrateBuilder, problem);
            perThreadEvaluators[i] = evaluator;
        }
    }

    abstract public void solve();

    abstract public String getConfigString();
}
