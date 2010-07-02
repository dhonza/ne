package hyper.evaluate;

import common.net.INet;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.cppn.CPPN;
import hyper.cppn.FakeArrayCPPN;
import hyper.experiments.reco.ReportStorage;
import opt.sade.SADE;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 2, 2010
 * Time: 4:26:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectSADESolver implements Solver {
    final protected ParameterCombination parameters;
    final protected Stats stats;
    final protected ReportStorage reportStorage;

    public DirectSADESolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.stats = stats;
        this.reportStorage = reportStorage;
    }

    public void solve() {
        Problem problem = ProblemFactory.getProblem(parameters, reportStorage);
        EvaluableSubstrateBuilder substrateBuilder =
                SubstrateBuilderFactory.createEvaluableSubstrateBuilder(problem.getSubstrate(), parameters);

        DirectSADEObjectiveFunction function = new DirectSADEObjectiveFunction(substrateBuilder, problem);
        SADE opt = new SADE(function);
        opt.fitnessCallsLimit = 1000000;

        opt.run();

        double[] weights = opt.getBsf();
        CPPN aCPPN = new FakeArrayCPPN(weights, substrateBuilder.getSubstrate().getMaxDimension());
        substrateBuilder.build(aCPPN);
        INet hyperNet = substrateBuilder.getNet();
        System.out.println(hyperNet);
        problem.show(hyperNet);
    }

    public String getConfigString() {
        return "IMPLEMENT!: DirectSADESolver.getConfigString()";
    }
}
