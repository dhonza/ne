package hyper.evaluate;

import common.evolution.IEvaluable;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.IGenotypeToPhenotype;
import common.evolution.ParallelPopulationEvaluator;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.experiments.reco.ReportStorage;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 21, 2010
 * Time: 11:18:52 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractSolver<G, INet> implements Solver {
    final protected ParameterCombination parameters;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    protected EvolutionaryAlgorithmSolver solver;

    protected ParallelPopulationEvaluator<G, INet> populationEvaluator;
    protected IProblem<INet> problem;

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
        IGenotypeToPhenotype<G, INet>[] perThreadConverters = new IGenotypeToPhenotype[threads];
        IEvaluable<INet>[] perThreadEvaluators = new IEvaluable[threads];
        for (int i = (threads - 1); i >= 0; i--) {

            problem = ProblemFactory.getProblem(parameters, reportStorage);
            IEvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

            IEvaluable<INet> evaluator = new HyperEvaluator<INet>(substrateBuilder, problem);
            perThreadEvaluators[i] = evaluator;

            IGenotypeToPhenotype<G, INet> converter = ConverterFactory.getConverter(parameters, substrateBuilder, problem);
            perThreadConverters[i] = converter;
        }
        populationEvaluator = new ParallelPopulationEvaluator<G, INet>(perThreadConverters, perThreadEvaluators);
    }

    abstract public void solve();

    abstract public String getConfigString();
}
