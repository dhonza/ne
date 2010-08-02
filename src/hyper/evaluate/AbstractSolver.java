package hyper.evaluate;

import common.evolution.IEvaluable;
import common.evolution.EvolutionaryAlgorithmSolver;
import common.evolution.IGenotypeToPhenotype;
import common.evolution.PopulationManager;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.IEvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.experiments.reco.ReportStorage;

import java.util.ArrayList;
import java.util.List;

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

    protected PopulationManager<G, INet> populationManager;
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
                threads = PopulationManager.getNumberOfThreads();
            }
        }
        List<IGenotypeToPhenotype<G, INet>> perThreadConverters = new ArrayList<IGenotypeToPhenotype<G, INet>>(threads);
        List<IEvaluable<INet>> perThreadEvaluators = new ArrayList<IEvaluable<INet>>(threads);
        for (int i = (threads - 1); i >= 0; i--) {

            problem = ProblemFactory.getProblem(parameters, reportStorage);
            IEvaluableSubstrateBuilder substrateBuilder =
                    SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

            IEvaluable<INet> evaluator = new HyperEvaluator<INet>(substrateBuilder, problem);
            perThreadEvaluators.add(evaluator);

            IGenotypeToPhenotype<G, INet> converter = ConverterFactory.getConverter(parameters, substrateBuilder, problem);
            perThreadConverters.add(converter);
        }
        populationManager = new PopulationManager<G, INet>(parameters, perThreadConverters, perThreadEvaluators);
    }

    abstract public void solve();

    abstract public String getConfigString();
}
