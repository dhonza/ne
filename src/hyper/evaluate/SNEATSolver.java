package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.EvaluableSubstrateBuilder;
import hyper.builder.SubstrateBuilderFactory;
import hyper.evaluate.printer.SNEATProgressPrinter1D;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;
import hyper.substrate.Substrate;
import sneat.MaxEvaluationsStopCondition;
import sneat.MaxGenerationsStopCondition;
import sneat.SNEAT;
import sneat.TargetFitnessStopCondition;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 15, 2009
 * Time: 9:50:43 PM
 * To change this template use File | Settings | File Templates.
 */

public class SNEATSolver implements Solver {
    private static Logger logger = Logger.getLogger("hyper.evaluate.SNEATSolver");

    final private ParameterCombination parameters;
    final private Substrate substrate;
    final private Stats stats;
    final private Problem problem;
    final private ReportStorage reportStorage;

    private EvolutionaryAlgorithmSolver solver;
    private SNEAT sneat;

    public SNEATSolver(ParameterCombination parameters, Substrate substrate, Stats stats, Problem problem, ReportStorage reportStorage) {
        this.parameters = parameters;
        this.substrate = substrate;
        this.stats = stats;
        this.problem = problem;
        this.reportStorage = reportStorage;
        init();
    }

    private void init() {
        logger.getParent().setLevel(Level.OFF);

        EvaluableSubstrateBuilder substrateBuilder = SubstrateBuilderFactory.createEvaluableSubstrateBuilder(substrate, parameters);
        int inputsCPPN = 2 * substrateBuilder.getSubstrate().getMaxDimension();
        int outputsCPPN = substrateBuilder.getSubstrate().getNumOfConnections();

//        HyperNEATParameters.loadParameterFile();
        setActivationFunctions();
        SNEATExperiment exp = new SNEATExperiment(parameters, substrateBuilder, problem, inputsCPPN, outputsCPPN);

        sneat = new SNEAT(exp);

        solver = new EvolutionaryAlgorithmSolver(sneat, stats);
        solver.addProgressPrinter(new SNEATProgressPrinter1D(sneat, substrateBuilder.getSubstrate(), problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(sneat, reportStorage, parameters));
        solver.addStopCondition(new MaxGenerationsStopCondition(sneat));
        solver.addStopCondition(new MaxEvaluationsStopCondition(sneat));
        solver.addStopCondition(new TargetFitnessStopCondition(sneat));
        solver.addStopCondition(new SolvedStopCondition(problem));

    }

    private void setActivationFunctions() {
        ActivationFunctionFactory.setSameProbabilitiesForList(parameters.getString("SNEAT.FUNCTIONS"));
    }

    public void solve() {
        solver.run();
    }

    public String getConfigString() {
        return sneat.getConfigString();
    }
}