package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.evaluate.printer.SNEATProgressPrinter1D;
import hyper.experiments.reco.FileProgressPrinter;
import hyper.experiments.reco.ReportStorage;
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

public class SNEATSolver extends AbstractSolver {
    private static Logger logger = Logger.getLogger("hyper.evaluate.SNEATSolver");

    private SNEAT sneat;

    protected SNEATSolver(ParameterCombination parameters, Stats stats, ReportStorage reportStorage) {
        super(parameters, stats, reportStorage);
        init();
    }


    private void init() {
        logger.getParent().setLevel(Level.OFF);

//        HyperNEATParameters.loadParameterFile();
        setActivationFunctions();
        double targetFitness = problem.getTargetFitness();
        SNEATExperiment exp = new SNEATExperiment(parameters, perThreadConverters, perThreadEvaluators, targetFitness);

        sneat = new SNEAT(exp);

        solver = new EvolutionaryAlgorithmSolver(sneat, stats, problem instanceof IProblemGeneralization);
        solver.addProgressPrinter(new SNEATProgressPrinter1D(sneat, problem, parameters));
        solver.addProgressPrinter(new FileProgressPrinter(sneat, problem, reportStorage, parameters));
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