package hyper.evaluate;

import common.evolution.EvolutionaryAlgorithmSolver;
import common.pmatrix.ParameterCombination;
import common.stats.Stats;
import hyper.builder.NetSubstrateBuilder;
import hyper.evaluate.printer.SNEATProgressPrinter1D;
import sneat.LastGenerationStopCondition;
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

/**
 * mela by dostat z venku: parametry NEAT, progress printer (stejny pro GP)
 */
public class SNEATSolver implements Solver {
    private static Logger logger = Logger.getLogger("hyper.evaluate.SNEATSolver");

    final private ParameterCombination parameters;
    final private NetSubstrateBuilder substrateBuilder;
    final private Stats stats;
    final private Problem problem;

    public SNEATSolver(ParameterCombination parameters, NetSubstrateBuilder substrateBuilder, Stats stats, Problem problem) {
        this.parameters = parameters;
        this.substrateBuilder = substrateBuilder;
        this.stats = stats;
        this.problem = problem;
    }

    private void setActivationFunctions() {
        ActivationFunctionFactory.setSameProbabilitiesForList(parameters.getString("SNEAT.FUNCTIONS"));
    }

    public void solve() {
        logger.getParent().setLevel(Level.OFF);
        int inputsCPPN = 2 * substrateBuilder.getSubstrate().getMaxDimension();
        int outputsCPPN = substrateBuilder.getSubstrate().getNumOfConnections();

//        HyperNEATParameters.loadParameterFile();
        setActivationFunctions();
        SNEATExperiment exp = new SNEATExperiment(parameters, substrateBuilder, problem, inputsCPPN, outputsCPPN);

        SNEAT sneat = new SNEAT(exp);

        EvolutionaryAlgorithmSolver solver = new EvolutionaryAlgorithmSolver(sneat);
        solver.addProgressPrinter(new SNEATProgressPrinter1D(sneat, substrateBuilder.getSubstrate(), problem));
        solver.addStopCondition(new LastGenerationStopCondition(sneat));
        solver.addStopCondition(new TargetFitnessStopCondition(sneat));
        solver.addStopCondition(new SolvedStopCondition(problem));
        solver.run();


//        XmlGenomeWriterStatic.Write(new File("bestGenome.xml"), (NeatGenome) ea.getBestGenome(), ActivationFunctionFactory.getActivationFunction("NullFn"));


        /*
        stats.addSample("STAT_GENERATIONS", population.getGeneration());
        */
    }
}