package hyper.evaluate;

import common.evolution.Evaluable;
import common.net.INet;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import cz.cvut.felk.cig.jcool.benchmark.method.ant.aaca.AACAMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.ant.aco.ACOMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.ant.daco.DACOMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.cmaes.CMAESMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.direct.DirectMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.genetic.de.DifferentialEvolutionMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.genetic.sade.SADEMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.cg.ConjugateGradientMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.gradient.qn.QuasiNewtonMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.hgapso.HGAPSOMethod;
import cz.cvut.felk.cig.jcool.benchmark.method.pso.PSOMethod;
import cz.cvut.felk.cig.jcool.benchmark.stopcondition.SimpleStopCondition;
import cz.cvut.felk.cig.jcool.core.Point;
import cz.cvut.felk.cig.jcool.core.ValuePoint;
import cz.cvut.felk.cig.jcool.core.ValuePointListTelemetry;
import cz.cvut.felk.cig.jcool.experiment.BasicExperimentRunner;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRun;
import cz.cvut.felk.cig.jcool.experiment.ExperimentRunner;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.cppn.FakeArrayCPPN;
import hyper.experiments.reco.ReportStorage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 11:11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectEncodingSolver implements Solver {
    final private ParameterCombination parameters;
    final private Stats stats;
    final private ReportStorage reportStorage;

    private Problem problem;
    private EvaluableSubstrateBuilder substrateBuilder;
    private Evaluable evaluator;

    public DirectEncodingSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.stats = stats;
        this.reportStorage = reportStorage;

        problem = ProblemFactory.getProblem(parameters, reportStorage);
        substrateBuilder = SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);
//        evaluator = EvaluableFactory.getEvaluable(parameters, substrateBuilder, problem);
    }

    public void solve() {
        final ExecutorService es = Executors.newSingleThreadExecutor();

        final ExperimentRunner runner = new BasicExperimentRunner(es);
        runner.setFunction(new DirectEncodingFunction(problem, substrateBuilder));
//        SADEMethod method = new SADEMethod();
        HGAPSOMethod method = new HGAPSOMethod();
        ((SimpleStopCondition)method.getStopConditions()[0]).setUse(false);


        runner.setMethod(method);
        runner.setSolver(cz.cvut.felk.cig.jcool.solver.SolverFactory.getNewInstance(1000));

        runner.startExperiment();

        ExperimentRun run = runner.getExperimentResults();

        System.out.println("====================");
        System.out.println("Function: " + run.getFunction().getName());
        System.out.println("Method: " + run.getMethod().getName());
        System.out.println("Solver: " + run.getSolver().getName());
        System.out.println("--------------------");
        System.out.println("Solution: " + run.getResults().getSolution());
        System.out.println("# of iterations: " + run.getResults().getNumberOfIterations());
        System.out.println("Statistics: " + run.getResults().getStatistics());

        es.shutdown();

        ValuePointListTelemetry telemetry = (ValuePointListTelemetry) run.getResults().getSolution();
        List<ValuePoint> valuePoints = telemetry.getValue();
        ValuePoint minimum = ValuePoint.at(Point.at(0), Double.POSITIVE_INFINITY);
        for (ValuePoint valuePoint : valuePoints) {
            if (valuePoint.getValue() < minimum.getValue()) {
                minimum = valuePoint;
            }
        }

        FakeArrayCPPN aCPPN = new FakeArrayCPPN(minimum.getPoint().toArray(), substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        System.out.println(hyperNet);
        problem.show(hyperNet);
    }

    public String getConfigString() {
        return "IMPLEMENT DirectEncodingSolver.getConfigString()";
    }

    public static void main(String[] args) {
        DirectEncodingSolver ds = new DirectEncodingSolver(null, null, null);
        ds.solve();
    }
}
